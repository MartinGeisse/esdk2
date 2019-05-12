.option norvc

    lui x1, 0x00004

loop:

	lw x2, 0(x1)
	beq x2, x0, loop
	li x2, 64
	sw x2, 0(x1)
	addi x1, x1, 4
	j loop
