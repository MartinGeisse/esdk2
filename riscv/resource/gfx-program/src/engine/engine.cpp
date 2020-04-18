
extern "C" {
    #include "../system/util.h"
    #include "../system/draw.h"
}

#include "Vector2.h"
#include "Vector3.h"
#include "Plane2.h"
#include "engine.h"

// constants
Fixed SCREEN_WIDTH_FIXED;
Fixed HALF_SCREEN_WIDTH_FIXED;
Fixed SCREEN_HEIGHT_FIXED;
Fixed HALF_SCREEN_HEIGHT_FIXED;
Fixed FOV_UNIT_FIXED;
Fixed NEAR_Z;

// vertices
int vertexCount;
Vector3 vertices[maxVertices];

// vertex indices (primitives use this table to share vertices since they are expensive to transform)
int vertexIndexCount;
int vertexIndices[maxVertexIndices];

// polygons
int polygonCount;
Polygon polygons[maxPolygons];

// collision detection
int collisionPlaneCount;
CollisionPlane collisionPlanes[maxCollisionPlanes];

// sectors
int sectorCount;
Sector sectors[maxSectors];

// player
Transform3 playerTransform;
int playerSectorIndex;

// internal data
static Transform3 inversePlayerTransform;
static Vector3 transformedVertices[maxVertices];
static Vector2 projectedVertices[maxVertices]; // only valid if (z >= NEAR_Z), otherwise we must clip BEFORE projection

// internal data: 2d clipping
static const int maxClipperStackSize = 64;
static Plane2 clipperStack[maxClipperStackSize];
static int clipperStackStart;
static int clipperStackEnd;

// internal data: current polygon
static int currentPolygonVertexCount3;
static Vector3 currentPolygonVertices3[64];
static Vector3 currentPolygonBackupVertices3[64];
static int currentPolygonVertexCount2;
static Vector2 currentPolygonVertices2[64];
static Vector2 currentPolygonBackupVertices2[64];
static Fixed currentPolygonEvaluations[64];

void initializeEngine() {
    SCREEN_WIDTH_FIXED = intToFixed(SCREEN_WIDTH);
    HALF_SCREEN_WIDTH_FIXED = intToFixed(HALF_SCREEN_WIDTH);
    SCREEN_HEIGHT_FIXED = intToFixed(SCREEN_HEIGHT);
    HALF_SCREEN_HEIGHT_FIXED = intToFixed(HALF_SCREEN_HEIGHT);
    FOV_UNIT_FIXED = intToFixed(FOV_UNIT);
    NEAR_Z = buildFixed(0, 6554); // 0.1
    vertexCount = 0;
    vertexIndexCount = 0;
    polygonCount = 0;
    collisionPlaneCount = 0;
    sectorCount = 0;
    playerTransform = Transform3();
    playerSectorIndex = 0;
}

// ----------------------------------------------------------------------------------------------------------------

// called once for all non-near vertices, and on demand for near vertices after clipping
static Vector2 project(Vector3 v) {
    return Vector2(v.x / v.z, v.y / v.z);
}

static inline Fixed screenTransformX(Fixed x) {
    return HALF_SCREEN_WIDTH_FIXED + x * FOV_UNIT_FIXED;
}

static inline Fixed screenTransformY(Fixed y) {
    return HALF_SCREEN_HEIGHT_FIXED - y * FOV_UNIT_FIXED;
}

static void renderLine(Vector2 a, Vector2 b) {

    // clip against screen boundaries / portal boundaries
    for (int i = clipperStackStart; i < clipperStackEnd; i++) {
        Plane2 *clipper = clipperStack + i;
        Fixed va = clipper->evaluate(a);
        Fixed vb = clipper->evaluate(b);
        if (va < getFixedEpsilon()) {
            if (vb < getFixedEpsilon()) {
                // invisible
                return;
            } else {
                // point a clipped away
                a -= (b - a) * va / (vb - va);
            }
        } else if (vb < getFixedEpsilon()) {
            // point b clipped away
            b -= (a - b) * vb / (va - vb);
        } // else: fully visible WRT this clipper
    }

    // draw clipped line TODO
    setDrawColor(2);
    drawLine(
        fixedToInt(screenTransformX(a.x)),
        fixedToInt(screenTransformY(a.y)),
        fixedToInt(screenTransformX(b.x)),
        fixedToInt(screenTransformY(b.y))
    );

}

static void renderLine(int vertexIndex1, int vertexIndex2) {
    // For "near" vertices, we must clip against the near plane, then transform the clipped vertex. For non-near
    // vertices, we can use the shared, already-transformed vertices. Note that if one vertex is near, we need the
    // non-projected version of the other vertex too, to perform clipping itself.
    Fixed vaz = transformedVertices[vertexIndex1].z - NEAR_Z;
    Fixed vbz = transformedVertices[vertexIndex2].z - NEAR_Z;

    if (vaz < getFixedZero()) {
        if (vbz < getFixedZero()) {
            // invisible
            return;
        } else {
            // first point clipped away
            Vector3 a = transformedVertices[vertexIndex1];
            Vector3 b = transformedVertices[vertexIndex2];
            a -= (b - a) * vaz / (vbz - vaz);
            renderLine(project(a), projectedVertices[vertexIndex2]);
        }
    } else if (vbz < getFixedZero()) {
        // second point clipped away
        Vector3 a = transformedVertices[vertexIndex1];
        Vector3 b = transformedVertices[vertexIndex2];
        b -= (a - b) * vbz / (vaz - vbz);
        renderLine(projectedVertices[vertexIndex1], project(b));
    } else {
        // fully visible WRT the near plane, so use the shared, already-transformed vertices
        renderLine(projectedVertices[vertexIndex1], projectedVertices[vertexIndex2]);
    }
}

// fills the "currentPolygon*" data structures
static void projectAndClipPolygon(int *polygonVertexIndices, int vertexCount) {

    // First, check if near plane clipping is necessary , i.e. at least one vertex is nearer than the near plane.
    // Also, fail fast if ALL vertices are nearer than the near plane.
    int anyNearVertex = 0, anyFarVertex = 0;
    for (int i = 0; i < vertexCount; i++) {
        Fixed z = transformedVertices[polygonVertexIndices[i]].z;
        if (z > NEAR_Z) {
            anyFarVertex = 1;
        } else {
            anyNearVertex = 1;
        }
    }
    if (!anyFarVertex) {
        currentPolygonVertexCount2 = 0;
        return;
    }
    if (anyNearVertex) {

        // near plane clipping is needed, so first make a copy of the vertices
        currentPolygonVertexCount3 = vertexCount;
        for (int i = 0; i < vertexCount; i++) {
            currentPolygonVertices3[i] = transformedVertices[polygonVertexIndices[i]];
        }

        // Evaluate all vertices against the near plane. While we could evaluate on the fly with a simple subtraction,
        // this might lead to this block and the 2d one below being merged in the future since we keep them similar
        // to each other.
        // During clipping, we read from the backup array, to handle the case that one vertex in the polygon
        // becomes two vertices after clipping. This happens if only a single vertex is clipped away.
        for (int j = 0; j < currentPolygonVertexCount3; j++) {
            currentPolygonEvaluations[j] = currentPolygonVertices3[j].z - NEAR_Z;
            currentPolygonBackupVertices3[j] = currentPolygonVertices3[j];
        }

        // clip each side
        int outputVertexCount = 0;
        for (int j = 0; j < currentPolygonVertexCount3; j++) {
            Vector3 currentVertex = currentPolygonBackupVertices3[j];
            Fixed currentEvaluation = currentPolygonEvaluations[j];
            if (currentEvaluation > getFixedZero()) {
                currentPolygonVertices3[outputVertexCount] = currentVertex;
                outputVertexCount++;
                continue;
            }
            int previousIndex = (j == 0 ? currentPolygonVertexCount3 - 1 : j - 1);
            int nextIndex = (j == currentPolygonVertexCount3 - 1 ? 0 : j + 1);
            Fixed previousEvaluation = currentPolygonEvaluations[previousIndex];
            Fixed nextEvaluation = currentPolygonEvaluations[nextIndex];

            // we normally want to treat vertices on the clip plane as invisible, so an infinitesimal piece of polygon
            // is treated as invisible. We have to make an exception though: A vertex on the plane with both neighbors
            // fully visible would produce two identical split vertices. This again causes unnecessary computations.
            // Even worse, for portals we have to handle it as a special case either here or when building the new
            // clippers as a line between two identical points cannot define a valid clipper.
            // Not handling it here causes followup problems when clipping against the next clipper, too.
            if (previousEvaluation > getFixedZero() && nextEvaluation > getFixedZero() && currentEvaluation > getFixedMinusEpsilon()) {
                currentPolygonVertices3[outputVertexCount] = currentVertex;
                outputVertexCount++;
                continue;
            }

            if (previousEvaluation > getFixedZero()) {
                currentPolygonVertices3[outputVertexCount] =
                    currentVertex - (currentPolygonBackupVertices3[previousIndex] - currentVertex) *
                        currentEvaluation / (previousEvaluation - currentEvaluation);
                outputVertexCount++;
            }
            if (nextEvaluation > getFixedZero()) {
                currentPolygonVertices3[outputVertexCount] =
                    currentVertex - (currentPolygonBackupVertices3[nextIndex] - currentVertex) *
                        currentEvaluation / (nextEvaluation - currentEvaluation);
                outputVertexCount++;
            }
        }
        currentPolygonVertexCount3 = outputVertexCount;

        // project the clipped vertices
        currentPolygonVertexCount2 = currentPolygonVertexCount3;
        for (int i = 0; i < currentPolygonVertexCount3; i++) {
            currentPolygonVertices2[i] = project(currentPolygonVertices3[i]);
        }

    } else {

        // just copy the already projected vertices
        currentPolygonVertexCount2 = vertexCount;
        for (int i = 0; i < vertexCount; i++) {
            currentPolygonVertices2[i] = projectedVertices[polygonVertexIndices[i]];
        }

    }

    // 2d clipping against screen / portal boundaries
    for (int i = clipperStackStart; i < clipperStackEnd; i++) {
        Plane2 *clipper = clipperStack + i;

        // Evaluate all vertices against this clipper, and fail fast if none is visible.
        // Also, during clipping, we read from the backup array, to handle the case that one vertex in the polygon
        // becomes two vertices after clipping. This happens if only a single vertex is clipped away.
        int anyVertexVisible = 0;
        for (int j = 0; j < currentPolygonVertexCount2; j++) {
            Fixed evaluation = clipper->evaluate(currentPolygonVertices2[j]);
            if (evaluation > getFixedZero()) {
                anyVertexVisible = 1;
            }
            currentPolygonEvaluations[j] = evaluation;
            currentPolygonBackupVertices2[j] = currentPolygonVertices2[j];
        }
        if (!anyVertexVisible) {
            currentPolygonVertexCount2 = 0;
            return;
        }

        // clip each side
        int outputVertexCount = 0;
        for (int j = 0; j < currentPolygonVertexCount2; j++) {
            Vector2 currentVertex = currentPolygonBackupVertices2[j];
            Fixed currentEvaluation = currentPolygonEvaluations[j];
            if (currentEvaluation > getFixedZero()) {
                currentPolygonVertices2[outputVertexCount] = currentVertex;
                outputVertexCount++;
                continue;
            }
            int previousIndex = (j == 0 ? currentPolygonVertexCount2 - 1 : j - 1);
            int nextIndex = (j == currentPolygonVertexCount2 - 1 ? 0 : j + 1);
            Fixed previousEvaluation = currentPolygonEvaluations[previousIndex];
            Fixed nextEvaluation = currentPolygonEvaluations[nextIndex];

            // again, prevent generating two identical split vertices from a single vertex on the clipper
            if (previousEvaluation > getFixedZero() && nextEvaluation > getFixedZero() && currentEvaluation > getFixedMinusEpsilon()) {
                currentPolygonVertices2[outputVertexCount] = currentVertex;
                outputVertexCount++;
                continue;
            }

            if (previousEvaluation > getFixedZero()) {
                currentPolygonVertices2[outputVertexCount] =
                    currentVertex - (currentPolygonBackupVertices2[previousIndex] - currentVertex) *
                        currentEvaluation / (previousEvaluation - currentEvaluation);
                outputVertexCount++;
            }
            if (nextEvaluation > getFixedZero()) {
                currentPolygonVertices2[outputVertexCount] =
                    currentVertex - (currentPolygonBackupVertices2[nextIndex] - currentVertex) *
                        currentEvaluation / (nextEvaluation - currentEvaluation);
                outputVertexCount++;
            }
        }
        currentPolygonVertexCount2 = outputVertexCount;
    }

}

static void renderSector(int sectorIndex) {

    Sector *sector = sectors + sectorIndex;
    int *sectorVertexIndices = vertexIndices + sector->vertexIndexStart;
    Polygon *polygon = polygons + sector->polygonStart;

    // draw portals
    for (int i = 0; i < sector->portalCount; i++) {

        // clip the portal
        projectAndClipPolygon(sectorVertexIndices, polygon->vertexCount);
        if (currentPolygonVertexCount2 >= 3) {

            // Check winding (backface culling). We don't really need this, but without it we'll draw a whole sector
            // against an "impossible" set of clippers, slowing things down.
            int correctWinding = 1;
            Plane2 firstSide = buildPlane2FromPoints(currentPolygonVertices2[0], currentPolygonVertices2[1]);
            for (int j = 2; j < currentPolygonVertexCount2; j++) {
                if (firstSide.evaluate(currentPolygonVertices2[j]) < getFixedMinusEpsilon()) {
                    correctWinding = 0;
                    break;
                }
            }
            if (correctWinding) {

                // save current clipper stack
                int oldClipperStackStart = clipperStackStart;

                // install the clipped portal as the only active clippers; protect against clipper stack overflow
                clipperStackStart = clipperStackEnd;
                clipperStackEnd += currentPolygonVertexCount2;
                if (clipperStackEnd <= maxClipperStackSize) {
                    for (int j = 0; j < currentPolygonVertexCount2; j++) {
                        // We have to invert the order here because while all logic seems to expect counter-clockwise winding for
                        // portals, the transformation to screen coordinates (+y pointing down!) actually inverts that.
                        clipperStack[clipperStackStart + j] = buildPlane2FromPoints(
                            currentPolygonVertices2[j == 0 ? currentPolygonVertexCount2 - 1 : j - 1],
                            currentPolygonVertices2[j]
                        );
                    }

                    // render the target sector with the new clipper stack
                    renderSector(polygon->targetSectorOrColor);

                }

                // restore clipper stack
                clipperStackEnd = clipperStackStart;
                clipperStackStart = oldClipperStackStart;

            }
        }

        // advance to the next polygon and its vertices
        sectorVertexIndices += polygon->vertexCount;
        polygon++;

    }

    // draw solid polygons
    for (int i = 0; i < sector->solidPolygonCount; i++) {
        projectAndClipPolygon(sectorVertexIndices, polygon->vertexCount);
        for (int j = 2; j < currentPolygonVertexCount2; j++) {
            setDrawColor(4);
            // TODO
            /*
            al_draw_filled_triangle(
                FTF(screenTransformX(currentPolygonVertices2[0].x)),
                FTF(screenTransformY(currentPolygonVertices2[0].y)),
                FTF(screenTransformX(currentPolygonVertices2[j - 1].x)),
                FTF(screenTransformY(currentPolygonVertices2[j - 1].y)),
                FTF(screenTransformX(currentPolygonVertices2[j].x)),
                FTF(screenTransformY(currentPolygonVertices2[j].y)),
                splitLineColor
            );
            */
        }
        sectorVertexIndices += polygon->vertexCount;
        polygon++;
    }

    // draw lines
    for (int i = 0; i < sector->lineCount; i++) {
        renderLine(sectorVertexIndices[0], sectorVertexIndices[1]);
        sectorVertexIndices += 2;
    }

}

void render() {

    // transform all vertices
    inversePlayerTransform = playerTransform.getInverse();
    for (int i = 0; i < vertexCount; i++) {
        Vector3 v = inversePlayerTransform * vertices[i];
        transformedVertices[i] = v;
        if (v.z >= NEAR_Z) {
            projectedVertices[i] = project(v);
        }
    }

    // reset clipping
    clipperStackStart = 0;
    clipperStackEnd = 4;
    clipperStack[0] = buildPlane2FromPoints(Vector2(getFixedMinusOne(), getFixedMinusOne()), Vector2(getFixedOne(), getFixedMinusOne()));
    clipperStack[1] = buildPlane2FromPoints(Vector2(getFixedOne(), getFixedMinusOne()), Vector2(getFixedOne(), getFixedOne()));
    clipperStack[2] = buildPlane2FromPoints(Vector2(getFixedOne(), getFixedOne()), Vector2(getFixedMinusOne(), getFixedOne()));
    clipperStack[3] = buildPlane2FromPoints(Vector2(getFixedMinusOne(), getFixedOne()), Vector2(getFixedMinusOne(), getFixedMinusOne()));

    // render
    renderSector(playerSectorIndex);

}

CollisionPlane *findCollisionPlane(Vector3 position) {
    Sector *sector = sectors + playerSectorIndex;
    for (int i = 0; i < sector->collisionPlaneCount; i++) {
        CollisionPlane *collisionPlane = collisionPlanes + sector->collisionPlaneStart + i;
        Fixed v = collisionPlane->plane.evaluate(position);
        if (v < getFixedZero()) {
            return collisionPlane;
        }
    }
    return NULL;
}
