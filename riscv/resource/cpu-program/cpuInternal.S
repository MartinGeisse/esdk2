.option norvc

// startup / interrupt entry switch
    j cpuInternalMain
    j cpuInternalInterruptEntry

// startup logic
cpuInternalMain:
    j biosMain

// interrupt logic
cpuInternalInterruptEntry:

// TODO test this
    // since we're using the omega general purpose register set, copy the GP from the regular set
    .word 0x00018191 // ADDI gp, other_gp, 0

    // go to normal interrupt service routine
    j biosInterruptEntry
