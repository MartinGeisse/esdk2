.option norvc

// helper for GP initialization, see below
.section .sdata
sdataSection:
.text
textSection:

////////////////////////////////////////////////////////////////////////////////////////////////////
// external locations
////////////////////////////////////////////////////////////////////////////////////////////////////

.globl payloadStartAddress
.set payloadStartAddress, 0x04000000 // dummy for now
.set keyStateTable, 0xfffff000
.set scanCodeBuffer, 0xfffff020
.set keyboardController, 0x81000000

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
    .word selectDrawPlane
    .word selectDisplayPlane
    .word clearScreen
    .word setPixel
    .word setDrawColor
    .word drawPixel
    .word drawAxisAlignedRectangle
    .word drawLine
    .word drawTriangle
    .word drawCharacter
    .word drawText
    .word setFont
    .word scroll
    .word termInitialize
    .word termPrintString
    .word termPrintChar
    .word termPrintInt
    .word termPrintUnsignedInt
    .word termPrintHexInt
    .word termPrintUnsignedHexInt
    .word termPrintln
    .word termPrintlnString
    .word termPrintlnChar
    .word termPrintlnInt
    .word termPrintlnUnsignedInt
    .word termPrintlnHexInt
    .word termPrintlnUnsignedHexInt
    .word profReset
    .word profLog
    .word profDisplay

////////////////////////////////////////////////////////////////////////////////////////////////////
// startup
////////////////////////////////////////////////////////////////////////////////////////////////////

start:

    .option push
    .option norelax
    la gp, __global_pointer$
    .option pop

    // initialize the SP register to start below the frame buffer
    li sp, 15 * 1024 * 1024

    // clear keyboard states, receive logic, scan code buffer
    li t1, keyStateTable
    sw x0, 0(t1)
    sw x0, 4(t1)
    sw x0, 8(t1)
    sw x0, 12(t1)
    sw x0, 16(t1)
    sw x0, 20(t1)
    sw x0, 24(t1)
    sw x0, 28(t1)
    la t1, keyReleasePrefixReceived
    sw x0, 0(t1)
    la t1, scanCodeBufferWriteIndex
    sw x0, 0(t1)
    la t1, scanCodeBuffer
    sb x0, 0(t1)

    // wait until SDRAM is ready TODO commented out for simulation
    // li a0, 500
    // call delay

    // invoke the main method
    call main
hang:
    j hang

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

scanCodeBufferWriteIndex:
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

    // store scan code in buffer
    // ... get current and next index to store the scancode and the terminator
    la t3, scanCodeBufferWriteIndex
    lw t1, 0(t3)
    addi t2, t1, 1
    andi t2, t2, 31
    sw t2, 0(t3)
    // ... store scan code and terminator
    la t3, scanCodeBuffer
    add t4, t3, t2
    sb x0, 0(t4)
    add t4, t3, t1
    sb t0, 0(t4)

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
