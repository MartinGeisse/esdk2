.option norvc

    li x1, 1
    li x31, 100

# x1 is the prime candidate; x31 is the stop value
primeCandidateLoop:

    addi x1, x1, 1
    beq x1, x31, finish
    li x2, 1

# x2 is the divisor candidate
divisorCandidateLoop:

    addi x2, x2, 1
    beq x2, x1, foundPrime
    mv x3, x1

# x3 is the current remainder
divisionLoop:
    sub x3, x3, x2
    bgtz x3, divisionLoop
    beqz x3, primeCandidateLoop
    j divisorCandidateLoop

foundPrime:

    sw x1, -32(x0)
    j primeCandidateLoop

finish:

    sw x0, -36(x0)
