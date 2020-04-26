
#include "engine/Vector2.h"
#include "engine/Vector3.h"
#include "engine/Plane2.h"
#include "engine/Plane3.h"
#include "engine/Transform3.h"
#include "engine/engine.h"
#include "engine/Fixed.h"

#include "level.h"

extern "C" {
    #include <divrem.h>
    #include <draw.h>
    #include "system/chargen.h"
    #include "system/util.h"
    #include "system/terminal.h"
    #include "system/profiling.h"
}

static volatile unsigned char *keyStateTable = (volatile unsigned char *)(0x00004000 - 32);
#define KEY_STATE(scancode) ((keyStateTable[(scancode) >> 3] & (1 << ((scancode) & 7))) != 0)

inline Fixed getSpeed() {
    return buildFixed(0, 6554); // 0.1
}

inline Fixed getRotationSin() {
    return buildFixed(0, 6543); // sin(0.1)
}

inline Fixed getRotationCos() {
    return buildFixed(0, 65209); // cos(0.1)
}

static void moveRelative(Fixed dx, Fixed dy, Fixed dz) {
    Vector3 newPosition = playerTransform.v + playerTransform.m * Vector3(dx, dy, dz);
    Sector *sector = sectors + playerSectorIndex;
    for (int i = 0; i < sector->collisionPlaneCount; i++) {
        CollisionPlane *collisionPlane = collisionPlanes + sector->collisionPlaneStart + i;
        Fixed v = collisionPlane->plane.evaluate(newPosition);
        if (v < getFixedZero()) {
            if (collisionPlane->targetSector < 0) {
                newPosition = collisionPlane->plane.projectPointOntoPlane(newPosition);
            } else {
                playerSectorIndex = collisionPlane->targetSector;
                sector = sectors + playerSectorIndex;
                i = -1;
            }
        }
    }
    playerTransform.v = newPosition;
}

static int lastFrameTimestamp;

int internalDemo() {
    setFont(CHARACTER_DATA);
    initializeEngine();
    buildLevel();
    int drawPlane = 0;
	while (true) {

        //
        termInitialize();

	    // FPS counter
	    {
	        int now = *(unsigned int *)0x00100000;
	        int frameDuration = now - lastFrameTimestamp;
	        lastFrameTimestamp = now;
	        int tenths = udiv(62500000, frameDuration);
	        int fps = udiv(tenths, 10);
	        tenths -= fps * 10;

            setDrawColor(5);
            termPrintString("FPS: ");
            termPrintInt(fps);
            termPrintChar('.');
            termPrintInt(tenths);
            termPrintln();
	    }

	    profReset();

        // flip and clear screen
        drawPlane = 1 - drawPlane;
        selectDrawPlane(drawPlane);
        selectDisplayPlane(1 - drawPlane);
        clearScreen(0);

        // profLog("after clear screen");

        // main 3d rendering
		render();

        // profLog("after rendering");

        // movement
        if (KEY_STATE(0x23)) {
            moveRelative(getSpeed(), getFixedZero(), getFixedZero());
        }
        if (KEY_STATE(0x1c)) {
            moveRelative(-getSpeed(), getFixedZero(), getFixedZero());
        }
        if (KEY_STATE(0x24)) {
            moveRelative(getFixedZero(), getSpeed(), getFixedZero());
        }
        if (KEY_STATE(0x21)) {
            moveRelative(getFixedZero(), -getSpeed(), getFixedZero());
        }
        if (KEY_STATE(0x1d)) {
            moveRelative(getFixedZero(), getFixedZero(), getSpeed());
        }
        if (KEY_STATE(0x22)) {
            moveRelative(getFixedZero(), getFixedZero(), -getSpeed());
        }
        if (KEY_STATE(0x74)) {
            playerTransform.m *= Matrix3(
                getRotationCos(), getFixedZero(), getRotationSin(),
                getFixedZero(), getFixedOne(), getFixedZero(),
                -getRotationSin(), getFixedZero(), getRotationCos()
            );
        }
        if (KEY_STATE(0x6b)) {
            playerTransform.m *= Matrix3(
                getRotationCos(), getFixedZero(), -getRotationSin(),
                getFixedZero(), getFixedOne(), getFixedZero(),
                getRotationSin(), getFixedZero(), getRotationCos()
            );
        }
        if (KEY_STATE(0x75)) {
            playerTransform.m *= Matrix3(
                getFixedOne(), getFixedZero(), getFixedZero(),
                getFixedZero(), getRotationCos(), -getRotationSin(),
                getFixedZero(), getRotationSin(), getRotationCos()
            );
        }
        if (KEY_STATE(0x72)) {
            playerTransform.m *= Matrix3(
                getFixedOne(), getFixedZero(), getFixedZero(),
                getFixedZero(), getRotationCos(), getRotationSin(),
                getFixedZero(), -getRotationSin(), getRotationCos()
            );
        }
        if (KEY_STATE(0x15)) {
            playerTransform.m *= Matrix3(
                getRotationCos(), -getRotationSin(), getFixedZero(),
                getRotationSin(), getRotationCos(), getFixedZero(),
                getFixedZero(), getFixedZero(), getFixedOne()
            );
        }
        if (KEY_STATE(0x2d)) {
            playerTransform.m *= Matrix3(
                getRotationCos(), getRotationSin(), getFixedZero(),
                -getRotationSin(), getRotationCos(), getFixedZero(),
                getFixedZero(), getFixedZero(), getFixedOne()
            );
        }

        // profLog("after keyboard handling");

        setDrawColor(5);
        profDisplay();

        // delay(50); // TODO since the code is loaded from SDRAM, this function won't work correctly anymore
	}
    return 0;
}

extern "C" {
    int demo() {
        return internalDemo();
    }
}
