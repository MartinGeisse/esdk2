#!/usr/bin/env php
<?php

define('TOOL', '~/riscv-toolchain/bin/riscv32-unknown-elf-');
define('LIBC_FOLDER', '~/git-repos/riscv-gnu-toolchain/build-newlib/riscv32-unknown-elf/newlib');

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
    if ($extension != 'S') {
        system(TOOL . 'gcc -msmall-data-limit=100000000 -march=rv32im -mabi=ilp32 -fno-exceptions ' . $cppFlags .
            ' -Wall -S -O3 -fno-tree-loop-distribute-patterns -o ' . $outputPath . '.S ' . $inputPath);
    }
    system(TOOL . 'gcc -msmall-data-limit=100000000 -march=rv32im -mabi=ilp32 -fno-exceptions ' . $cppFlags .
        ' -Wall -c -O3 -fno-tree-loop-distribute-patterns -o ' . $outputPath . ' ' . $inputPath);
}

function linkFiles() {
    global $objectFiles;
    $objectFilesList = implode(' ', $objectFiles);
    // note: the -lc must come *after* the source files that call functions from it because LD is a "smart" linker...
    // See https://stackoverflow.com/questions/45135/why-does-the-order-in-which-libraries-are-linked-sometimes-cause-errors-in-gcc
    system(TOOL . 'ld -L ' . LIBC_FOLDER . ' -T src/linkerscript -Map=build/program.map -A rv32im -o build/program.elf ' . $objectFilesList . ' -lc');
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
    'src/start.S' => 'build/start.o',
    'src/divrem.c' => 'build/divrem.o',
    'src/draw.c' => 'build/draw.o',
    'src/term.c' => 'build/term.o',
    'src/profiling.c' => 'build/profiling.o',
    'src/main.c' => 'build/main.o',
    'src/chargen.c' => 'build/chargen.o',
    'src/simdev.c' => 'build/simdev.o',
    'src/random.c' => 'build/random.o',
    'src/libc-compat/memory.c' => 'build/libc-compat_memory.o',

    'src/sierpinsky/sierpinsky.c' => 'build/sierpinsky.o',

    'src/rocksndiamonds/libgame/hash.c' => 'build/rocksndiamonds_libgame_hash.o',
);
foreach ($paths as $inputPath => $outputPath) {
    buildFile($inputPath, $outputPath);
}
linkFiles();
convertExecutable();
