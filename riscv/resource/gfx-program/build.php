#!/usr/bin/env php
<?php

define('TOOL', '~/riscv-toolchain/bin/riscv32-unknown-linux-gnu-');

$objectFiles = array();

function buildFile($inputPath, $outputPath) {
    global $objectFiles;
    $objectFiles[] = $outputPath;

    if (strpos($inputPath, '.cpp') === FALSE) {
        $cppFlags = '';
    } else {
        $cppFlags = ' -fno-rtti ';
    }

    system(TOOL . 'gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -fno-exceptions ' . $cppFlags .
        ' -Wall -c -o ' . $outputPath . ' ' . $inputPath);
}

function linkFiles() {
    global $objectFiles;
    $objectFilesList = implode(' ', $objectFiles);
    system(TOOL . 'ld -Map=build/program.map -A rv32im -N -Ttext=0x80200000 -o build/program.elf -e entryPoint ' . $objectFilesList);
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
    'src/system/util.S' => 'build/util.o',
    'src/system/simdev.c' => 'build/simdev.o',
    'src/system/cpu.S' => 'build/cpu_s.o',
    'src/system/cpu.c' => 'build/cpu_c.o',
    'src/system/chargen.c' => 'build/chargen.o',
    'src/system/terminal.c' => 'build/terminal.o',

    'src/engine/Fixed.cpp' => 'build/Fixed.o',
    'src/engine/engine.cpp' => 'build/engine.o',

    'src/main.c' => 'build/main.o',
    'src/demo.cpp' => 'build/demo.o',
    'src/level.cpp' => 'build/level.o',

);
foreach ($paths as $inputPath => $outputPath) {
    buildFile($inputPath, $outputPath);
}
linkFiles();
convertExecutable();
