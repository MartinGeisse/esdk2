
#include "simdev.h"

static volatile int *simulationDevice = (volatile int *)0xff000000;

int simdevIsSimulation() {
    return *simulationDevice;
}

void simdevMessage(char *message) {
    simulationDevice[1] = 0;
}

void simdevShowInt(char *label, int value) {
    simulationDevice[1] = 1;
}

void simdevFillWordsShowInt(void *pointer, int value, int wordCount) {
    simulationDevice[2] = 0;
}

void simdevSelectDisplayPlane(int plane) {
    simulationDevice[3] = plane;
}
