
void delay(int n);

static unsigned int * const SCREEN = (unsigned int *)0x01000000;
static volatile unsigned int * const KEYBOARD = (unsigned int * const)0x02000000;
static volatile unsigned int * const SIGNAL_LOGGER = (unsigned int * const)0x04000000;

static int keyStates[256];

static void pollKeyboard(void) {
    while (1) {
        int keyCode = *KEYBOARD;
        if (keyCode == 0) {
            break;
        }
        int newState = 1;
        if (keyCode == 0xf0) {
            newState = 0;
            do {
                keyCode = *KEYBOARD;
            } while (keyCode == 0);
        }
        keyStates[keyCode] = newState;
    }
}

static int sample(int signalIndex, int sampleIndex) {
    return (SIGNAL_LOGGER[sampleIndex] >> signalIndex) & 1;
}

void displaySignalLoggerMain(void) {
    int startSampleIndex = 0, groupSizeShift = 0;
    int groupSize = (1 << groupSizeShift);
    while (1) {

        // clear screen
        for (int y = 0; y < 30; y++) {
            for (int x = 0; x < 80; x++) {
                SCREEN[(y << 7) + x] = ' ';
            }
            pollKeyboard();
        }

        // drawing
        const int sampleCount = 1024;
        int displayedGroups = (sampleCount - startSampleIndex) >> groupSizeShift;
        if (displayedGroups > 80) {
            displayedGroups = 80;
        }
        for (int signalIndex = 0; signalIndex < 7; signalIndex++) {
            int y = signalIndex << 2;
            for (int groupIndex = 0, sampleIndex = startSampleIndex; groupIndex < displayedGroups; groupIndex++, sampleIndex += groupSize) {
                int firstSampleValue = sample(signalIndex, sampleIndex);
                int sampleValue = firstSampleValue;
                for (int i = 0; i < groupSize; i++) {
                    int anotherSampleValue = sample(signalIndex, sampleIndex + i);
                    if (anotherSampleValue != firstSampleValue) {
                        sampleValue = -1;
                    }
                }
                if (sampleValue < 0) {
                    SCREEN[((y + 1) << 7) + groupIndex] = '#';
                } else if (sampleValue) {
                    SCREEN[(y << 7) + groupIndex] = '_';
                } else {
                    SCREEN[((y + 1) << 7) + groupIndex] = '_';
                }
            }
            pollKeyboard();
        }

        // movement
        int cooldown = 0;
        do {
            pollKeyboard();
            if (keyStates[0x75]) {
                if (groupSize > 1) {
                    groupSize = (groupSize >> 1) + (groupSize >> 2);
                    cooldown = 100;
                }
            }
            if (keyStates[0x72]) {
                groupSize += (groupSize >> 2);
                groupSize += (groupSize >> 4);
                groupSize++;
                cooldown = 100;
            }
            if (keyStates[0x6b]) {
                startSampleIndex -= groupSize;
                if (startSampleIndex < 0) {
                    startSampleIndex = 0;
                }
                cooldown = 50;
            }
            if (keyStates[0x74]) {
                startSampleIndex += groupSize;
                cooldown = 50;
            }
        } while (cooldown == 0);
        for (int i = 0; i < cooldown; i++) {
            delay(1);
            pollKeyboard();
        }


    }
}
