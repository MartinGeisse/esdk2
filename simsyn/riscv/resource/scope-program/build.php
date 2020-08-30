#!/usr/bin/env php
<?php

define('TOOL', '~/riscv-toolchain/bin/riscv32-unknown-elf-');

$objectFiles = array();

function buildFile($inputPath, $outputPath) {
    global $objectFiles;
    $objectFiles[] = $outputPath;
    system(TOOL . 'gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -c -o ' . $outputPath . ' ' . $inputPath);
}

function linkFiles() {
    global $objectFiles;
    $objectFilesList = implode(' ', $objectFiles);
    system(TOOL . 'ld -Map=build/program.map -A rv32im -N -Ttext 0 -o build/program.elf -e 0 ' . $objectFilesList);
}

function convertExecutable() {
    system(TOOL . 'objcopy -j .text -j .rodata -j .sdata -I elf32-littleriscv -O binary build/program.elf build/program.bin');
}

//
// --------------------------------------------------------------------------------------------------------------------
//

system('rm -rf build');
system('mkdir build');

$paths = array(
    'src/system/start.S' => 'build/start.o',
    'src/system/draw.c' => 'build/draw.o',
    'src/system/system.S' => 'build/system.o',
    'src/system/simdev.c' => 'build/simdev.o',
    'src/system/cpu.S' => 'build/cpu.o',
    'src/main.c' => 'build/main.o',
);
foreach ($paths as $inputPath => $outputPath) {
    buildFile($inputPath, $outputPath);
}
linkFiles();
convertExecutable();
