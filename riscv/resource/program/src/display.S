
.globl	displayWrite
.type	displayWrite, @function

displayWrite:
    sll a1, a1, 7
    add a0, a0, a1
    lui a1, 0x00001
    add a1, a1, a0
    sb a2, 0(a1)
    ret
