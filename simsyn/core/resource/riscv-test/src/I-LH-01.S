# RISC-V Compliance Test I-LH-01
#
# Copyright (c) 2017, Codasip Ltd.
# Copyright (c) 2018, Imperas Software Ltd. Additions
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#      * Redistributions of source code must retain the above copyright
#        notice, this list of conditions and the following disclaimer.
#      * Redistributions in binary form must reproduce the above copyright
#        notice, this list of conditions and the following disclaimer in the
#        documentation and/or other materials provided with the distribution.
#      * Neither the name of the Codasip Ltd., Imperas Software Ltd. nor the
#        names of its contributors may be used to endorse or promote products
#        derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
# IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
# THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
# PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Codasip Ltd., Imperas Software Ltd.
# BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
# DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
# THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
# Specification: RV32I Base Integer Instruction Set, Version 2.0
# Description: Testing instruction LH.

#include "compliance_test.h"
#include "compliance_io.h"
#include "test_macros.h"

# Test Virtual Machine (TVM) used by program.
RV_COMPLIANCE_RV32M

# Test code region
RV_COMPLIANCE_CODE_BEGIN

    RVTEST_IO_INIT
    RVTEST_IO_ASSERT_GPR_EQ(x31, x0, 0x00000000)
    RVTEST_IO_WRITE_STR(x31, "# Test Begin\n")

    # ---------------------------------------------------------------------------------------------
    RVTEST_IO_WRITE_STR(x31, "# Test part A1 - test base address + 0\n");

    # Addresses for test data and results
    la      x31, test_A1_data
    la      x2, test_A1_res

    # Test
    lh      x3, 0(x31)
    lh      x4, 2(x31)

    # Store results
    sw      x3, 0(x2)
    sw      x4, 4(x2)

    //
    // Assert
    //
    RVTEST_IO_CHECK()
    RVTEST_IO_ASSERT_GPR_EQ(x2, x3, 0xFFFFF222)
    RVTEST_IO_ASSERT_GPR_EQ(x2, x4, 0x000011F1)

    RVTEST_IO_WRITE_STR(x31, "# Test part A1  - Complete\n");

    # ---------------------------------------------------------------------------------------------
    RVTEST_IO_WRITE_STR(x31, "# Test part A2 - test base address - 1\n");

    # Addresses for test data and results
    la      x24, test_A2_data + 1
    la      x5, test_A2_res

    # Test
    lh      x25, -1(x24)
    lh      x26, +1(x24)

    # Store results
    sw      x25, 0(x5)
    sw      x26, 4(x5)

    RVTEST_IO_ASSERT_GPR_EQ(x5, x25, 0x000044F4)
    RVTEST_IO_ASSERT_GPR_EQ(x5, x26, 0xFFFFF333)

    RVTEST_IO_WRITE_STR(x31, "# Test part A2  - Complete\n");

    # ---------------------------------------------------------------------------------------------
    RVTEST_IO_WRITE_STR(x31, "# Test part A3 - test base address + 1\n");

    # Addresses for test data and results
    la      x7, test_A3_data - 1
    la      x8, test_A3_res

    # Test
    lh      x30, 1(x7)
    lh      x31, 3(x7)

    # Store results
    sw      x30, 0(x8)
    sw      x31, 4(x8)

    RVTEST_IO_ASSERT_GPR_EQ(x8, x30, 0xFFFFF666)
    RVTEST_IO_ASSERT_GPR_EQ(x8, x31, 0x000055F5)

    RVTEST_IO_WRITE_STR(x31, "# Test part A3  - Complete\n");

    # ---------------------------------------------------------------------------------------------
    RVTEST_IO_WRITE_STR(x31, "# Test part A4 - test base address - 2048\n");

    # Addresses for test data and results
    la      x10, test_A4_data + 2048
    la      x11, test_A4_res

    # Test
    lh      x12, 0xFFFFF800(x10)
    lh      x13, 0xFFFFF802(x10)

    # Store results
    sw      x12, 0(x11)
    sw      x13, 4(x11)

    RVTEST_IO_ASSERT_GPR_EQ(x11, x12, 0xFFFF88F8)
    RVTEST_IO_ASSERT_GPR_EQ(x11, x13, 0xFFFFF777)

    RVTEST_IO_WRITE_STR(x31, "# Test part A4  - Complete\n");

    # ---------------------------------------------------------------------------------------------
    RVTEST_IO_WRITE_STR(x31, "# Test part A5 - test base address + 2047\n");

    # Addresses for test data and results
    la      x13, test_A5_data - 2045
    la      x14, test_A5_res

    # Test
    lh      x15, 0x7FD(x13)
    lh      x16, 0x7FF(x13)

    # Store results
    sw      x15, 0(x14)
    sw      x16, 4(x14)

    RVTEST_IO_ASSERT_GPR_EQ(x14, x15, 0x00000AAA)
    RVTEST_IO_ASSERT_GPR_EQ(x14, x16, 0xFFFF9909)

    RVTEST_IO_WRITE_STR(x31, "# Test part A5  - Complete\n");

    # ---------------------------------------------------------------------------------------------
    RVTEST_IO_WRITE_STR(x31, "# Test part B - test base address + -4,-2, 0, 2, 4, 6\n");

    # Addresses for test data and results
    la      x16, test_B_data
    la      x17, test_B_res

    # Test
    lh      x18, -4(x16)
    lh      x19, -2(x16)
    lh      x20, 0(x16)
    lh      x21, 2(x16)
    lh      x22, 4(x16)
    lh      x23, 6(x16)

    # Store results
    sw      x18, 0(x17)
    sw      x19, 4(x17)
    sw      x20, 8(x17)
    sw      x21, 12(x17)
    sw      x22, 16(x17)
    sw      x23, 20(x17)

    RVTEST_IO_ASSERT_GPR_EQ(x17, x18, 0xFFFFCC0C)
    RVTEST_IO_ASSERT_GPR_EQ(x17, x19, 0x00000BBB)
    RVTEST_IO_ASSERT_GPR_EQ(x17, x20, 0x00000EEE)
    RVTEST_IO_ASSERT_GPR_EQ(x17, x21, 0xFFFFDD0D)
    RVTEST_IO_ASSERT_GPR_EQ(x17, x22, 0x000000F0)
    RVTEST_IO_ASSERT_GPR_EQ(x17, x23, 0x00000FFF)

    RVTEST_IO_WRITE_STR(x31, "# Test part B  - Complete\n");

    # ---------------------------------------------------------------------------------------------
    RVTEST_IO_WRITE_STR(x31, "# Test part C - test load to x0\n");

    # Addresses for test data and results
    la      x21, test_C_data
    la      x22, test_C_res

    # Test
    lh      x0, 0(x21)

    # Store results
    sw      x0, 0(x22)

    RVTEST_IO_ASSERT_GPR_EQ(x22, x0, 0x00000000)

    RVTEST_IO_WRITE_STR(x31, "# Test part C  - Complete\n");

    # ---------------------------------------------------------------------------------------------
    RVTEST_IO_WRITE_STR(x31, "# Test part D - test for forwarding\n");

    # Addresses for test data and results
    la      x21, test_D_data
    la      x22, test_D_res

    # Test
    lw      x23, 0(x21)
    lh      x24, 0(x23)
    mv      x25, x24

    # Store results
    sw      x25, 0(x22)

    RVTEST_IO_ASSERT_GPR_EQ(x22, x25, 0xFFFFDEF0)

    RVTEST_IO_WRITE_STR(x31, "# Test part D  - Complete\n");

    # ---------------------------------------------------------------------------------------------
    RVTEST_IO_WRITE_STR(x31, "# Test part E1 - test store with same base and destination address\n");

    # Addresses for test data and results
    la      x25, test_E1_data
    la      x26, test_E1_res

    # Test
    lh      x25, 0(x25)

    # Store results
    sw      x25, 0(x26)

    RVTEST_IO_ASSERT_GPR_EQ(x26, x25, 0x00003210)

    RVTEST_IO_WRITE_STR(x31, "# Test part E  - Complete\n");

    # ---------------------------------------------------------------------------------------------
    RVTEST_IO_WRITE_STR(x31, "# Test part E2 - test store with same base and destination address, base with offset\n");

    # Addresses for test data and results
    la      x27, test_E2_data + 1
    la      x28, test_E2_res

    # Test
    lh      x27, -1(x27)

    # Store results
    sw      x27, 0(x28)

    RVTEST_IO_ASSERT_GPR_EQ(x28, x27, 0xFFFFBA98)

    RVTEST_IO_WRITE_STR(x31, "# Test part A1  - Complete\n");

    RVTEST_IO_WRITE_STR(x31, "# Test End\n")

 # ---------------------------------------------------------------------------------------------
    # HALT
    RV_COMPLIANCE_HALT

RV_COMPLIANCE_CODE_END

# Input data section.
    .align 4

test_A1_data:
    .word 0x11F1F222
test_A2_data:
    .word 0xF33344F4
test_A3_data:
    .word 0x55F5F666
test_A4_data:
    .word 0xF77788F8
test_A5_data:
    .word 0x99090AAA
    .word 0x0BBBCC0C
test_B_data:
    .word 0xDD0D0EEE
    .word 0x0FFF00F0
test_C_data:
    .word 0x12345678
test_D_data:
    .word test_D_data2_label
test_D_data2_label:
    .word 0x9ABCDEF0

test_E1_data:
    .word 0x76543210
test_E2_data:
    .word 0xFEDCBA98


# Output data section.
RV_COMPLIANCE_DATA_BEGIN
    .align 4

test_A1_res:
    .fill 2, 4, -1
test_A2_res:
    .fill 2, 4, -1
test_A3_res:
    .fill 2, 4, -1
test_A4_res:
    .fill 2, 4, -1
test_A5_res:
    .fill 2, 4, -1
test_B_res:
    .fill 6, 4, -1
test_C_res:
    .fill 1, 4, -1
test_D_res:
    .fill 1, 4, -1
test_E1_res:
    .fill 1, 4, -1
test_E2_res:
    .fill 1, 4, -1

RV_COMPLIANCE_DATA_END
