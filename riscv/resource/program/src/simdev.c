
#include "simdev.h"

static volatile int *simulationDevice = (volatile int *)0x40000000;

int simdevIsSimulation() {
    return *simulationDevice;
}

void simdevMessage(char *message) {
    simulationDevice[1] = 0;
}
