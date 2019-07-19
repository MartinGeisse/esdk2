#!/usr/bin/env php
<?php

system('rm -rf build');
system('mkdir build');

system('riscv32-unknown-elf-gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -c -o build/cpuInternal.o ../cpu-program/cpuInternal.S');
system('riscv32-unknown-elf-gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -c -o build/start.o src/start.S');
system('riscv32-unknown-elf-gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -c -o build/main.o src/main.c');
system('riscv32-unknown-elf-gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -c -o build/draw.o src/draw.c');
system('riscv32-unknown-elf-gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -c -o build/system.o src/system.S');
system('riscv32-unknown-elf-gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -c -o build/simdev.o src/simdev.c');
system('riscv32-unknown-elf-gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -c -o build/cpu.o src/cpu.S');
system('riscv32-unknown-elf-ld -Map=build/program.map -A rv32im -N -Ttext 0 -o build/program.elf -e 0 build/cpuInternal.o build/start.o build/main.o build/system.o build/draw.o build/simdev.o build/cpu.o');
system('riscv32-unknown-elf-objcopy -j .text -j .rodata -j .sdata -I elf32-littleriscv -O binary build/program.elf build/program.bin');
