
#include "keyboard.h"
#include "term.h"

void delay(int n);

static unsigned int * const SCREEN = (unsigned int *)0x01000000;
static volatile unsigned int * const SIGNAL_LOGGER = (unsigned int * const)0x42000000;

static int sample(int signalIndex, int sampleIndex) {
    return (SIGNAL_LOGGER[sampleIndex] >> signalIndex) & 1;
}

static const char * const labels[32] = {
    "mainState0",
    "mainState1",
    "mainState2",
    "mainState3",
    "mainState4",
    "CK",
    "CKE",
    "CS'",
    "RAS'",
    "CAS'",
    "WE'",
    "*data",
    "*strobe",
    "ODT",
    "RESET'",
    "LDQS",
    "UDQS",
    "d0",
    "d1",
    "d2",
    "d3",
    "d4",
    "d5",
    "d6",
    "d7",
    "",
    "",
    "",
    "",
    "",
    "",
    "",
};

void displaySignalLoggerMain(void) {
    int startSampleIndex = 0;
    int groupSize = 1;
    int startSignalIndex = 0;
    while (1) {

        // clear screen
        for (int y = 0; y < 30; y++) {
            for (int x = 0; x < 80; x++) {
                SCREEN[(y << 7) + x] = ' ';
            }
            pollKeyboard();
        }

        // drawing

        const int sampleCount = 8192;
        for (int signalIndexDelta = 0; signalIndexDelta < 10; signalIndexDelta++) {
            int signalIndex = startSignalIndex + signalIndexDelta;
            int y = signalIndexDelta + signalIndexDelta + signalIndexDelta;
            const char *label = labels[signalIndex];
            for (int i = 0; i < 10; i++) {
                char c = label[i];
                if (c == 0) {
                    break;
                } else {
                    SCREEN[((y + 1) << 7) + i] = c;
                }
            }
            for (int x = 0, sampleIndex = startSampleIndex; x < 70 && sampleIndex + groupSize <= sampleCount; x++, sampleIndex += groupSize) {
                int firstSampleValue = sample(signalIndex, sampleIndex);
                int sampleValue = firstSampleValue;
                for (int i = 0; i < groupSize; i++) {
                    int anotherSampleValue = sample(signalIndex, sampleIndex + i);
                    if (anotherSampleValue != firstSampleValue) {
                        sampleValue = -1;
                    }
                }
                if (sampleValue < 0) {
                    SCREEN[((y + 1) << 7) + x + 10] = '#';
                } else if (sampleValue) {
                    SCREEN[(y << 7) + x + 10] = '_';
                    SCREEN[((y + 1) << 7) + x + 10] = '.';
                } else {
                    SCREEN[(y << 7) + x + 10] = '.';
                    SCREEN[((y + 1) << 7) + x + 10] = '_';
                }
            }
            pollKeyboard();
        }

        // movement
        int cooldown = 0;
        do {
            pollKeyboard();
            if (keyStates[0x75]) {
                startSignalIndex--;
                if (startSignalIndex < 0) {
                    startSignalIndex = 0;
                }
                cooldown = 100;
            }
            if (keyStates[0x72]) {
                startSignalIndex++;
                if (startSignalIndex > 25) {
                    startSignalIndex = 25;
                }
                cooldown = 100;
            }
            if (keyStates[0x1c]) {
                if (groupSize > 1) {
                    groupSize = (groupSize >> 1) + (groupSize >> 2);
                    cooldown = 100;
                }
            }
            if (keyStates[0x1a]) {
                if (groupSize < 100) {
                    groupSize += (groupSize >> 2);
                    groupSize += (groupSize >> 4);
                    groupSize++;
                    cooldown = 100;
                }
            }
            if (keyStates[0x6b]) {
                startSampleIndex -= groupSize << 1;
                if (startSampleIndex < 0) {
                    startSampleIndex = 0;
                }
                cooldown = 50;
            }
            if (keyStates[0x74]) {
                startSampleIndex += groupSize << 1;
                cooldown = 50;
            }
            if (keyStates[0x76]) {
                return;
            }
        } while (cooldown == 0);
        for (int i = 0; i < cooldown; i++) {
            delay(1);
            pollKeyboard();
        }

    }
}
