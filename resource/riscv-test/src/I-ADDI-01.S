.option norvc
.global rvComplianceDataBegin
.global rvComplianceDataEnd
.global outputLoopStart

	# trying assembler syntax for LUI
	lui x1, 0x12000000

    # Addresses for test data and results
    la      x1, test_A1_data
    la      x2, test_A1_res

    # testdata
    lw      x3, 0(x1)
    sw      x3, 0(x2)

    # HALT
    j outputLoopStart




    .align 4
test_A1_data:
    .word 0

rvComplianceDataBegin:
    .align 4
test_A1_res:
    .fill 1, 4, -1
rvComplianceDataEnd:
