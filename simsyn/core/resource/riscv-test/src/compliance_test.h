
.option norvc

#define RV_COMPLIANCE_RV32M
#define RV_COMPLIANCE_CODE_BEGIN
#define RVTEST_IO_INIT
#define RVTEST_IO_WRITE_STR(x,y)
#define RVTEST_IO_CHECK()
#define RV_COMPLIANCE_HALT j outputLoopStart
#define RV_COMPLIANCE_CODE_END

#define RV_COMPLIANCE_DATA_BEGIN rvComplianceDataBegin:
#define RV_COMPLIANCE_DATA_END rvComplianceDataEnd:
#define RVTEST_IO_ASSERT_GPR_EQ(a,b,c)

.global rvComplianceDataBegin
.global rvComplianceDataEnd
.global outputLoopStart
