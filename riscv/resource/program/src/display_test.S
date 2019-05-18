.option norvc

    // build zero register in case x0 doesn't work
    sub x1, x1, x1

    // build data to write
    addi x2, x1, 65

    // build display start address
    addi x3, x1, 1024   // 0x400
    add x3, x3, x3     // 0x800
    add x3, x3, x3     // 0x1000
    add x3, x3, x3     // 0x2000
    add x3, x3, x3     // 0x4000

    // write data
    sw x2, 0(x3)

    // filler instructions in case the jump has problems
    addi x31, x1, 0
    add x31, x1, x1
    add x31, x1, x1
    add x31, x1, x1

    // hang
hang:
    j hang
