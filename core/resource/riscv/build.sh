rm -rf build
mkdir build
riscv32-unknown-elf-gcc -c -o build/main.o src/main.s
riscv32-unknown-elf-gcc -c -o build/second.o src/second.s
riscv32-unknown-elf-ld -N -Ttext 0 -o build/all.elf -e 0 build/*.o
riscv32-unknown-elf-objcopy -j .text -I elf32-littleriscv -O binary build/all.elf build/text.bin
riscv32-unknown-elf-objcopy -j .data -I elf32-littleriscv -O binary build/all.elf build/data.bin
