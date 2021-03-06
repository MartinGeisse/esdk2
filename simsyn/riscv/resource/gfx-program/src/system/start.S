.option norvc

.globl	entryPoint

// helper for GP initialization, see below
.section .sdata
sdataSection:
.text
textSection:

////////////////////////////////////////////////////////////////////////////////////////////////////
// external locations
////////////////////////////////////////////////////////////////////////////////////////////////////

.set keyStateTable, (0x00004000 - 32)
.set scanCodeBuffer, (0x00004000 - 64)
.set keyboardController, 0x01000000

////////////////////////////////////////////////////////////////////////////////////////////////////
// entry point
////////////////////////////////////////////////////////////////////////////////////////////////////

// startup logic
entryPoint:

    .option push
    .option norelax
    la gp, __global_pointer$
    .option pop

    // initialize the SP register to start below the key state table
    li sp, scanCodeBuffer

    // enable interrupts (0000 0100 0010 0010 1000 0101 0000 1011)
    li t0, 1
    .word 0x0422850b

    // main program
    call main

hang:
    j hang
