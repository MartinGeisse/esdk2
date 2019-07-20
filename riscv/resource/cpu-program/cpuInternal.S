.option norvc

// startup / interrupt entry switch
    j cpuInternalMain
    j cpuInternalInterruptEntry

// startup logic
cpuInternalMain:
    j biosMain

// interrupt logic
cpuInternalInterruptEntry:

    // TODO before moving to OS code, copy the GP register to the omega-GP register!
    j biosInterruptEntry
