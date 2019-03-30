.option norvc

start:
    addi x1, x0, 42
    sw x1, -32(x0)
    addi x1, x0, 50
    sw x1, -32(x0)
    addi x0, x0, 50
    sw x0, -32(x0)

    sw x0, -36(x0)
