
#include "simdev.h"

static volatile int *simulationDevice = (volatile int *)0x40000000;

int simdevIsSimulation() {
    return *simulationDevice;
}

void simdevMessage(char *message) {
    simulationDevice[1] = 0;
}

void simdevShowInt(char *label, int value) {
    simulationDevice[1] = 1;
}
