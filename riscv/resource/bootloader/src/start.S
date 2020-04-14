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

    // the bootloader is located at the end of the memory (see below)
    j bootloader

interruptEntry:
hang:
    j hang

////////////////////////////////////////////////////////////////////////////////////////////////////

// some empty space so the bootloader won't overwrite itself
.skip 5000

bootloader:
    li t0, 0x00080000 // serial port
    li t1, 0x80000000 // memory
    li t2, 0 // end-of-stream detection timer
    li t5, 5 // TODO TEST
bootloaderLoop:
    lw t3, 0(t0) // read byte
    bltz t3, notAvailable // check if available
    sb t5, 0(t1) // write byte
    addi t1, t1, 1 // advance write pointer
    li t2, 0 // reset end-of-stream detection timer
    j bootloaderLoop
notAvailable:
    addi t2, t2, 1 // advance timer
    li t3, 10000000 // check if expired
    bne t2, t3, bootloaderLoop // otherwise loop
    // TODO expired
    li t1, 0x80000000
    li t5, 7
    sb t5, 0(t1) // write byte
    sb t5, 1(t1) // write byte
hang2:
    j hang2

