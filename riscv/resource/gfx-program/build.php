#!/usr/bin/env php
<?php

define('TOOL', '~/riscv-toolchain/bin/riscv32-unknown-linux-gnu-');

$objectFiles = array();

function buildFile($inputPath, $outputPath) {
    global $objectFiles;
    $objectFiles[] = $outputPath;

    $dotPosition = strrpos($inputPath, '.');
    if ($dotPosition === FALSE) {
        die('no dot in input filename');
    }
    $extension = substr($inputPath, $dotPosition + 1);
    $allowedExtensions = array('c', 'cpp', 'S');
    if (!in_array($extension, $allowedExtensions)) {
        die('unknown input file extension: ' . $extension);
    }

    $cppFlags = ($extension == '.cpp' ? ' -fno-rtti ' : '');
    $optFlag = ' -O1 ';
    if ($extension != 'S') {
        system(TOOL . 'gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -fno-exceptions ' . $cppFlags .
            ' -Wall -S ' . $optFlag . ' -fno-tree-loop-distribute-patterns -o ' . $outputPath . '.S ' . $inputPath);
    }
    system(TOOL . 'gcc -msmall-data-limit=100000 -march=rv32im -mabi=ilp32 -fno-exceptions ' . $cppFlags .
        ' -Wall -c ' . $optFlag . ' -fno-tree-loop-distribute-patterns -o ' . $outputPath . ' ' . $inputPath);
}

function linkFiles() {
    global $objectFiles;
    $objectFilesList = implode(' ', $objectFiles);
    system(TOOL . 'ld -T src/linkerscript -Map=build/program.map -A rv32im -o build/program.elf ' . $objectFilesList);
}

function convertExecutable() {
    system(TOOL . 'objcopy -j .image -I elf32-littleriscv -O binary build/program.elf build/program.bin');
}

//
// --------------------------------------------------------------------------------------------------------------------
//

system('rm -rf build');
system('mkdir build');

$paths = array(

    'src/system/start.S' => 'build/start.o',
    'src/system/builtin.S' => 'build/builtin.o',
    'src/system/draw.c' => 'build/draw.o',
    'src/system/util.S' => 'build/util.o',
    'src/system/simdev.c' => 'build/simdev.o',
    'src/system/cpu.S' => 'build/cpu_s.o',
    'src/system/cpu.c' => 'build/cpu_c.o',
    'src/system/chargen.c' => 'build/chargen.o',
    'src/system/terminal.c' => 'build/terminal.o',
    'src/system/profiling.c' => 'build/profiling.o',

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
