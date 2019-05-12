.option norvc

    lui x1, 0x00004

loop:
	lw x2, 0(x1)
	beq x2, x0, loop
	jal x30, printByte
	j loop

printByte:
	addi x3, x2, 0
	srl x2, x2, 4
	jal x31, printDigit
	addi x2, x3, 0
	jal x31, printDigit
	jr x30

printDigit:
	andi x2, x2, 15
	addi x2, x2, -10
	blt x2, x0, printNumericDigit
	addi x2, x2, 97
	j printChar
printNumericDigit:
	addi x2, x2, 58
	j printChar

printChar:
	sw x2, 0(x1)
	addi x1, x1, 4
	jr x31
