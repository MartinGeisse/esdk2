.option norvc
.global rvComplianceDataBegin
.global rvComplianceDataEnd
.global outputLoopStart

outputLoopStart:

    # load output data start label
	la x1, rvComplianceDataBegin

	# simulate the ".align 4" that the tests contain *after* that label (align to (2^4 bytes) = 4 words)
    andi x2, x1, 0xfffffff0
    beq x2, x1, outputLoop
    addi x1, x2, 16

outputLoop:
	la x2, rvComplianceDataEnd
	beq x1, x2, outputLoopEnd
	lw x2, 0(x1)
	sw x2, -32(x0)
	addi x1, x1, 4
	j outputLoop
outputLoopEnd:
	sw x0, -36(x0)
