
start:
    addi x1, x0, 42
    sw x1, -8(x0)
    addi x1, x0, 50
    sw x1, -8(x0)
hang:
    j hang
