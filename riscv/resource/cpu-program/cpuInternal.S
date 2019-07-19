.option norvc

// startup / interrupt entry switch
    j cpuInternalMain
    j cpuInternalInterruptEntry

// startup logic
cpuInternalMain:
    j biosMain

// interrupt logic
cpuInternalInterruptEntry:
    j biosInterruptEntry
