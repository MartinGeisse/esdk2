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
    // TODO la gp, sdata - 1
    // TODO addi gp, gp, 2
    // TODO addi gp, gp, 0x7ff

    // initialize the SP register
    // TODO lui sp, 0x00004

    // test program
    li s0, 0x80000000
    li s1, 0
testProgramLoop:
    sb s1, 0(s0)
    addi s1, s1, 1
    li a0, 300
    call delay
    j testProgramLoop

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
