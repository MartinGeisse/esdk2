
#include "engine/Vector2.h"
#include "engine/Vector3.h"
#include "engine/Plane2.h"
#include "engine/Plane3.h"
#include "engine/Transform3.h"
#include "engine/engine.h"
#include "level.h"

extern "C" {
    #include "system/util.h"
    #include "system/draw.h"
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

int internalDemo() {
    initializeEngine();
    buildLevel();

    int drawPlane = 0;
	while (true) {

        // flip and clear screen
        drawPlane = 1 - drawPlane;
        selectDrawPlane(drawPlane);
        selectDisplayPlane(1 - drawPlane);
        clearScreen(0);

        // main 3d rendering
		render();

        // movement
        if (KEY_STATE(0x6b)) {
            moveRelative(-getSpeed(), getFixedZero(), getFixedZero());
        }
        if (KEY_STATE(0x74)) {
            moveRelative(getSpeed(), getFixedZero(), getFixedZero());
        }
        if (KEY_STATE(0x75)) {
            moveRelative(getFixedZero(), getSpeed(), getFixedZero());
        }
        if (KEY_STATE(0x72)) {
            moveRelative(getFixedZero(), -getSpeed(), getFixedZero());
        }
/*
        ALLEGRO_KEYBOARD_STATE keyboardState;
        al_get_keyboard_state(&keyboardState);
        if (al_key_down(&keyboardState, ALLEGRO_KEY_ESCAPE)) {
            break;
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_D)) {
            moveRelative(getSpeed(), getFixedZero(), getFixedZero());
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_A)) {
            moveRelative(-getSpeed(), getFixedZero(), getFixedZero());
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_E)) {
            moveRelative(getFixedZero(), getSpeed(), getFixedZero());
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_C)) {
            moveRelative(getFixedZero(), -getSpeed(), getFixedZero());
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_W)) {
            moveRelative(getFixedZero(), getFixedZero(), getSpeed());
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_X)) {
            moveRelative(getFixedZero(), getFixedZero(), -getSpeed());
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_RIGHT)) {
            playerTransform.m *= Matrix3(
                getRotationCos(), getFixedZero(), getRotationSin(),
                getFixedZero(), getFixedOne(), getFixedZero(),
                -getRotationSin(), getFixedZero(), getRotationCos()
            );
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_LEFT)) {
            playerTransform.m *= Matrix3(
                getRotationCos(), getFixedZero(), -getRotationSin(),
                getFixedZero(), getFixedOne(), getFixedZero(),
                getRotationSin(), getFixedZero(), getRotationCos()
            );
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_UP)) {
            playerTransform.m *= Matrix3(
                getFixedOne(), getFixedZero(), getFixedZero(),
                getFixedZero(), getRotationCos(), -getRotationSin(),
                getFixedZero(), getRotationSin(), getRotationCos()
            );
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_DOWN)) {
            playerTransform.m *= Matrix3(
                getFixedOne(), getFixedZero(), getFixedZero(),
                getFixedZero(), getRotationCos(), getRotationSin(),
                getFixedZero(), -getRotationSin(), getRotationCos()
            );
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_Q)) {
            playerTransform.m *= Matrix3(
                getRotationCos(), -getRotationSin(), getFixedZero(),
                getRotationSin(), getRotationCos(), getFixedZero(),
                getFixedZero(), getFixedZero(), getFixedOne()
            );
        }
        if (al_key_down(&keyboardState, ALLEGRO_KEY_R)) {
            playerTransform.m *= Matrix3(
                getRotationCos(), getRotationSin(), getFixedZero(),
                -getRotationSin(), getRotationCos(), getFixedZero(),
                getFixedZero(), getFixedZero(), getFixedOne()
            );
        }
*/
        delay(100);
	}
    return 0;
}

extern "C" {
    int demo() {
        return internalDemo();
    }
}
