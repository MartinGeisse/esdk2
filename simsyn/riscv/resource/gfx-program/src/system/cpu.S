.option norvc

.globl	cpuGetExceptionCode
.type	cpuGetExceptionCode, @function
cpuGetExceptionCode:
    csrr a0, 0
    ret
