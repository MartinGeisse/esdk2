#!/usr/bin/env php
<?php

system('rm -rf build');
system('mkdir build');
system('cp src/*.txt build');

system('~/riscv-toolchain/bin/riscv32-unknown-linux-gnu-gcc -c -o build/common-output.o src/common/output.S');

foreach (scandir('src') as $filename) {
	if (substr($filename, -2) === '.S') {
		$basename = substr($filename, 0, strlen($filename) - 2);
		system('~/riscv-toolchain/bin/riscv32-unknown-linux-gnu-gcc -c -o build/' . $basename . '.o src/' . $basename . '.S');
        system('~/riscv-toolchain/bin/riscv32-unknown-linux-gnu-ld -N -Ttext 0 -o build/' . $basename . '.elf -e 0 build/' . $basename . '.o build/common-output.o');
        system('~/riscv-toolchain/bin/riscv32-unknown-linux-gnu-objcopy -j .text -I elf32-littleriscv -O binary build/' . $basename . '.elf build/' . $basename . '.bin');
	}
}
