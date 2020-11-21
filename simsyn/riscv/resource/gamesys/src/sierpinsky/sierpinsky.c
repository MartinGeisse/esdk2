
#include "../random.h"
#include "../divrem.h"
#include "../draw.h"

/**
 * Taken from https://www.allegro.cc/depot/SerpinskysTriangle
 */
void sierpinskyMain() {
    clearScreen(0);
	unsigned int pos_x = 320, pos_y = 240;
	while (1) {
		switch (urem(getRandom(), 3)) {

			case 0:
				pos_x = pos_x >> 1;
				pos_y = (pos_y + 480) >> 1;
			    break;

			case 1: 
				pos_x = (pos_x + 320) >> 1;
				pos_y = pos_y >> 1;
			    break;

			case 2: 
				pos_x = (pos_x + 640) >> 1;
				pos_y = (pos_y + 480) >> 1;
			    break;

		}
		setPixel(pos_x, pos_y, 7);
	}
}
