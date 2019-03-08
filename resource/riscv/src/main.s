
XOR x0, x0, x0

ADDI x1, x0, 1
SW x1, 0(x0)
ADDI x1, x0, 2
SW x1, 0(x0)
ADDI x1, x0, 3
SW x1, 0(x0)

ADDI x1, x0, 1
SW x1, 1(x0)
ADDI x1, x0, 2
SW x1, 1(x0)
ADDI x1, x0, 3
SW x1, 1(x0)

ADDI x1, x0, 1
SW x1, 3(x0)
ADDI x1, x0, 2
SW x1, 3(x0)
ADDI x1, x0, 3
SW x1, 3(x0)

hang:
	BEQ x0, x0, hang

testcode:
	.word 0x12345678

.data
testdata:
	.word 0x87654321

.bss
testbss:
	.word 0
