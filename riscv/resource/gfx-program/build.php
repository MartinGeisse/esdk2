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
    $objectFilesList = implode(' ', $objectFilesList);
    system(TOOL . 'ld -Map=build/program.map -A rv32im -N -Ttext 0 -o build/program.elf -e 0 ' . objectFilesList);
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
    'src/system/draw.o' => 'build/draw.c',
    'src/system/system.o' => 'build/system.S',
    'src/system/simdev.o' => 'build/simdev.c',
    'src/system/cpu.o' => 'build/cpu.S',
    'src/main.o' => 'build/main.c',
    'src/vec3dtest.o' => 'build/vec3dtest.cpp',
    'src/engine.o' => 'build/engine.cpp',
);
foreach ($paths as $inputPath => $outputPath) {
    buildFile($inputPath, $outputPath);
}
linkFiles();
convertExecutable();
