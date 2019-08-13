#!/usr/bin/env php
<?php

system('rm -rf build');
system('mkdir build');

system('riscv32-unknown-elf-gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -c -o build/start.o ../../../../majai/out/majai/selftest.S');
system('riscv32-unknown-elf-ld -Map=build/program.map -A rv32im -N -Ttext 0 -o build/program.elf -e 0 build/start.o');
system('riscv32-unknown-elf-objcopy -j .text -j .rodata -j .sdata -I elf32-littleriscv -O binary build/program.elf build/program.bin');
