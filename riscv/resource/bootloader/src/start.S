.option norvc

// helper for GP initialization, see below
.section .sdata
sdata:
.text

// startup / interrupt entry switch
entryPoint:
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

    // wait until SDRAM is ready
    li a0, 500
    call delay

    // indicate status
    // li a0, 1
    // call drawLine

    // the bootloader is located at the end of the memory (see below)
    j bootloader

interruptEntry:
hang:
    j hang

////////////////////////////////////////////////////////////////////////////////////////////////////

delay:
    beq a0, x0, return
    addi t0, x0, 1000
innerLoop:
    addi x0, x0, 0 // NOP = 4 cycles
    addi x0, x0, 0 // 8 cycles
    addi x0, x0, 0 // 12
    addi x0, x0, 0 // 16
    addi x0, x0, 0 // 20
    addi x0, x0, 0 // 24
    addi x0, x0, 0 // 28
    addi t0, t0, -1 // 44
    bne t0, x0, innerLoop // 48 cycles ~~ 1 µs
    addi a0, a0, -1
    j delay

return:
    ret

////////////////////////////////////////////////////////////////////////////////////////////////////

// some empty space so the bootloader won't overwrite itself
.skip 5000

////////////////////////////////////////////////////////////////////////////////////////////////////

drawLine:
    li t0, 0x80000000 // screen memory
    li t1, 64
drawLineLoop:
    sb a0, 0(t0)
    addi t0, t0, 1
    addi t1, t1, -1
    bnez t1, drawLineLoop
    ret

////////////////////////////////////////////////////////////////////////////////////////////////////

bootloader:
    li s0, 0x00080000 // serial port
    // TODO: li s1, 0x80200000 // start of non-screen memory
    // TODO li s1, 0 // small memory (to write to)
    li s1, 0x80000000 // TODO test
    li s2, 0 // end-of-stream detection timer
firstByteLoop:
    lw s3, 0(s0) // read byte
    bgez s3, available // check if available
    j firstByteLoop
moreBytesLoop:
    lw s3, 0(s0) // read byte
    bgez s3, available // check if available
    addi s2, s2, 1 // advance timer
    li s3, 5000000 // check if expired
    bne s2, s3, moreBytesLoop // otherwise loop
    // TODO li a0, 5 // indicate status
    // TODO call drawLine // ...
    // TODO j entryPoint // run loaded program
    j entryPoint
available:
    // TODO li a0, 2 // indicate status
    // TODO call drawLine // ...
    sb s3, 0(s1) // write byte
    addi s1, s1, 1 // advance write pointer
    li s2, 0 // reset end-of-stream detection timer
    j moreBytesLoop
