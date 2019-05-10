.option norvc

/*
    la x1, outputText
    lui x2, 0x00004
    lb x3, 0(x1)
    sw x3, 0(x2)
    la x1, outputText
    lui x2, 0x00004
    lb x3, 1(x1)
    sw x3, 4(x2)
    la x1, outputText
    lui x2, 0x00004
    lb x3, 2(x1)
    sw x3, 8(x2)
    la x1, outputText
    lui x2, 0x00004
    lb x3, 3(x1)
    sw x3, 12(x2)
    la x1, outputText
    lui x2, 0x00004
    lb x3, 4(x1)
    sw x3, 16(x2)
    la x1, outputText
    lui x2, 0x00004
    lb x3, 5(x1)
    sw x3, 20(x2)
    la x1, outputText
    lui x2, 0x00004
    lb x3, 6(x1)
    sw x3, 24(x2)
    la x1, outputText
    lui x2, 0x00004
    lb x3, 7(x1)
    sw x3, 28(x2)
    la x1, outputText
    lui x2, 0x00004
    lb x3, 8(x1)
    sw x3, 32(x2)
    la x1, outputText
    lui x2, 0x00004
    lb x3, 9(x1)
    sw x3, 36(x2)
    la x1, outputText
    lui x2, 0x00004

*/
/*
    la x1, outputText
    lui x2, 0x00004

    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4

    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4

    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4

    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4

    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4

    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4

    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4

    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4

    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4

    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4
*/

    // these dummy ADDI commands seem to make "more" of the output string work. Without a J command, it works. Additional
    // commands after the "loop" label do NOT help. I'm assuming that the absolute location of the jump target seems to
    // have an effect.

    la x1, outputText
    lui x2, 0x00004

loop:

    lb x3, 0(x1)
    // beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4

    j loop

hang:
    j hang

outputText:
    .string "Hello world!"
