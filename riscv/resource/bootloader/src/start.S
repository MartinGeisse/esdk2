.option norvc

// helper for GP initialization, see below
.section .sdata
sdataSection:
.text
textSection:

////////////////////////////////////////////////////////////////////////////////////////////////////
// external locations
////////////////////////////////////////////////////////////////////////////////////////////////////

.set payloadEntryPoint, 0x80200000
.set keyStateTable, (0x00004000 - 32)
.set keyboardController, 0x00004000

////////////////////////////////////////////////////////////////////////////////////////////////////
// entry points
////////////////////////////////////////////////////////////////////////////////////////////////////

entryPoint:
    j start
    j interruptEntry

////////////////////////////////////////////////////////////////////////////////////////////////////
// function table for built-in functions
////////////////////////////////////////////////////////////////////////////////////////////////////

// the following are built-in library functions so they can be executed from small (fast) RAM
functionTable:
    .word udivrem
    .word udiv
    .word urem
    .word div
    .word drawLineInternal
    .word drawHorizontalLineInternal
    .word drawHalfTriangleInternal

////////////////////////////////////////////////////////////////////////////////////////////////////
// startup
////////////////////////////////////////////////////////////////////////////////////////////////////

start:
    // Initialize the GP register in a way that fools the ld tool, disallowing link-time optimization (specifically,
    // section relaxation) so it doesn't try to use GP to initialize GP. To do that, we load an address that has a
    // distance greater than 0x800 from the assumed / intended value of GP; 0x800 is the largest possible immediate
    // offset for loads/stores and immediate constants for ADDI. Then, we correct the error at run-time.
    la gp, sdataSection  - 1
    addi gp, gp, 2
    addi gp, gp, 0x7ff

    // initialize the SP register to start below the key state table
    li sp, keyStateTable

    // clear keyboard states
    li t0, 0
    li t1, keyStateTable
    sw t0, 0(t1)
    sw t0, 4(t1)
    sw t0, 8(t1)
    sw t0, 12(t1)
    sw t0, 16(t1)
    sw t0, 20(t1)
    sw t0, 24(t1)
    sw t0, 28(t1)

    // wait until SDRAM is ready
    li a0, 500
    call delay

    // indicate status
    li a0, 1
    call drawLine

    // the bootloader is located at the end of the memory (see below)
    j bootloader

////////////////////////////////////////////////////////////////////////////////////////////////////
// interrupts
////////////////////////////////////////////////////////////////////////////////////////////////////

interruptRegisterSaveArea:
    .word 0
    .word 0
    .word 0
    .word 0
    .word 0

keyReleasePrefixReceived:
    .word 0

// currently only used for the keyboard
interruptEntry:

    // save registers
    sw t0, %lo(interruptRegisterSaveArea)(x0)
    sw t1, %lo(interruptRegisterSaveArea + 4)(x0)
    sw t2, %lo(interruptRegisterSaveArea + 8)(x0)
    sw t3, %lo(interruptRegisterSaveArea + 12)(x0)
    sw t4, %lo(interruptRegisterSaveArea + 16)(x0)

    // load scan code
    li t0, keyboardController
    lw t0, 0(t0)

    // ignore fake keypress indication markers
    li t1, 0xe0
    beq t0, t1, interruptExit
    li t1, 0xe1
    beq t0, t1, interruptExit

    // recognize key release prefix
    li t1, 0xf0
    bne t0, t1, currentScancodeIsNotKeyReleasePrefix
    li t1, 1
    sw t1, %lo(keyReleasePrefixReceived)(x0)
    j interruptExit
currentScancodeIsNotKeyReleasePrefix:

    // 8 bits per byte -> lowest 3 scancode bits indicate the bit inside the byte; high 5 scancode bits indicate the
    // byte in the key state table. First, build the bit mask in t1.
    li t1, 1
    andi t2, t0, 7
    sll t1, t1, t2

    // then build the byte offset in t0
    srl t0, t0, 3

    // load and clear keyReleasePrefixReceived (goes into t2)
    lw t2, %lo(keyReleasePrefixReceived)(x0)
    sw x0, %lo(keyReleasePrefixReceived)(x0)

    // finally, set or clear the relevant bit
    li t3, keyStateTable
    add t3, t3, t0
    lb t4, 0(t3)
    bnez t2, onKeyUp
onKeyDown:
    or t4, t4, t1
    j storeKeyState
onKeyUp:
    not t1, t1
    and t4, t4, t1
storeKeyState:
    sb t4, 0(t3)

interruptExit:

    // restore registers
    lw t4, %lo(interruptRegisterSaveArea + 16)(x0)
    lw t3, %lo(interruptRegisterSaveArea + 12)(x0)
    lw t2, %lo(interruptRegisterSaveArea + 8)(x0)
    lw t1, %lo(interruptRegisterSaveArea + 4)(x0)
    lw t0, %lo(interruptRegisterSaveArea)(x0)

    // return from interrupt (0000 1000 0000 0000 1000 0000 0000 1011)
    .word 0x0800800b

////////////////////////////////////////////////////////////////////////////////////////////////////
// delay
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
// colored status line
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
// bootloader
////////////////////////////////////////////////////////////////////////////////////////////////////

bootloader:
    li s0, 0x00080000 // serial port
    li s1, 0x80200000 // start of non-screen memory
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
    li a0, 5 // indicate status
    call drawLine // ...
    tail payloadEntryPoint // run loaded program
available:
    li a0, 2 // indicate status
    call drawLine // ...
    sb s3, 0(s1) // write byte
    addi s1, s1, 1 // advance write pointer
    li s2, 0 // reset end-of-stream detection timer
    j moreBytesLoop
