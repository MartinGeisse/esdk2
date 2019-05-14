#!/usr/bin/env php
<?php

system('rm -rf build');
system('mkdir build');

system('riscv32-unknown-elf-gcc -c -o build/hello.o src/hello.S');
system('riscv32-unknown-elf-gcc -c -o build/keycodes.o src/keycodes.S');
system('riscv32-unknown-elf-ld -N -Ttext 0 -o build/program.elf -e 0 build/hello.o');
system('riscv32-unknown-elf-objcopy -j .text -I elf32-littleriscv -O binary build/program.elf build/program.bin');

/*
foreach (scandir('src') as $filename) {
	if (substr($filename, -2) === '.S') {
		$basename = substr($filename, 0, strlen($filename) - 2);
		system('riscv32-unknown-elf-gcc -c -o build/' . $basename . '.o src/' . $basename . '.S');
        system('riscv32-unknown-elf-ld -N -Ttext 0 -o build/' . $basename . '.elf -e 0 build/' . $basename . '.o build/common-output.o');
        system('riscv32-unknown-elf-objcopy -j .text -I elf32-littleriscv -O binary build/' . $basename . '.elf build/' . $basename . '.bin');
	}
}
*/
