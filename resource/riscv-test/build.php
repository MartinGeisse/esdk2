#!/usr/bin/env php
<?php

system('rm -rf build');
system('mkdir build');
system('cp src/*.txt build');

foreach (scandir('src') as $filename) {
	if (substr($filename, -2) === '.S') {
		$basename = substr($filename, 0, strlen($filename) - 2);
		system('riscv32-unknown-elf-gcc -c -o build/' . $basename . '.o src/' . $basename . '.S');
        system('riscv32-unknown-elf-ld -N -Ttext 0 -o build/' . $basename . '.elf -e 0 build/' . $basename . '.o');
        system('riscv32-unknown-elf-objcopy -j .text -I elf32-littleriscv -O binary build/' . $basename . '.elf build/' . $basename . '.bin');
	}
}
