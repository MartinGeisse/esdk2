.option norvc

// helper for GP initialization, see below
.section .sdata
sdataSection:
.text
textSection:

////////////////////////////////////////////////////////////////////////////////////////////////////
// external locations
////////////////////////////////////////////////////////////////////////////////////////////////////

.set keyStateTable, (0x00004000 - 32)

////////////////////////////////////////////////////////////////////////////////////////////////////
// entry point
////////////////////////////////////////////////////////////////////////////////////////////////////

// startup logic
entryPoint:
    // Initialize the GP register in a way that fools the ld tool, disallowing link-time optimization (specifically,
    // section relaxation) so it doesn't try to use GP to initialize GP. To do that, we load an address that has a
    // distance greater than 0x800 from the assumed / intended value of GP; 0x800 is the largest possible immediate
    // offset for loads/stores and immediate constants for ADDI. Then, we correct the error at run-time.
    la gp, sdataSection - 1
    addi gp, gp, 2
    addi gp, gp, 0x7ff

    // initialize the SP register to start below the key state table
    la sp, keyStateTable

    // enable interrupts (0000 0100 0010 0010 1000 0101 0000 1011)
    li t0, 1
    .word 0x0422850b

    // main program
    call main

hang:
    j hang
