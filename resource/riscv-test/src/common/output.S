.option norvc
.global rvComplianceDataBegin
.global rvComplianceDataEnd
.global outputLoopStart

outputLoopStart:
	la x1, rvComplianceDataBegin
outputLoop:
	la x2, rvComplianceDataEnd
	beq x1, x2, outputLoopEnd
	lw x2, 0(x1)
	sw x2, -32(x0)
	addi x1, x1, 4
	j outputLoop
outputLoopEnd:
	sw x0, -36(x0)
