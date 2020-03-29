.option norvc

// helper for GP initialization, see below
.section .sdata
sdata:
.text

// startup / interrupt entry switch
    j start
    j interruptEntry

// startup logic
start:
    // Initialize the GP register in a way that fools the ld tool, disallowing link-time optimization (specifically,
    // section relaxation) so it doesn't try to use GP to initialize GP. To do that, we load an address that has a
    // distance greater than 0x800 from the assumed / intended value of GP; 0x800 is the largest possible immediate
    // offset for loads/stores and immediate constants for ADDI. Then, we correct the error at run-time.
    la gp, sdata - 1
    addi gp, gp, 2
    addi gp, gp, 0x7ff

    // initialize the SP register
    lui sp, 0x00004

    // main program
    call main

hang:
    j hang

// interrupt logic
interruptEntry:

    // since we're using the alternative general purpose register set, copy the GP from the regular set
    .word 0x00018191 // ADDI gp, other_gp, 0

    // TODO establish interrupt handler stack pointer

    // load instruction location (== return address) into a0
    .word 00000000000000000000010100001011

    call exception
    j hang
