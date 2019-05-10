
    la x1, outputText
    lui x2, 0x00004
loop:
    lb x3, 0(x1)
    beq x3, x0, hang
    sw x3, 0(x2)
    addi x1, x1, 1
    addi x2, x2, 4
    j loop

hang:
    j hang

outputText:
    .string "Hello world!"
