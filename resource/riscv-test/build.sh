rm -rf build
mkdir build
cp src/*.txt build

riscv32-unknown-elf-gcc -c -o build/addi.o src/addi.s
riscv32-unknown-elf-ld -N -Ttext 0 -o build/addi.elf -e 0 build/addi.o
riscv32-unknown-elf-objcopy -j .text -I elf32-littleriscv -O binary build/addi.elf build/addi.bin
