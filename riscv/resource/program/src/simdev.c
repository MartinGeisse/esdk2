
#include "simdev.h"

static volatile int *simulationDevice;

void simdevInitialize() {
    simulationDevice = (volatile int *)0x40000000;
}

void simdevMessage(char *message) {
    simulationDevice[1] = 0;
}
