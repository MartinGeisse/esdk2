// See LICENSE for license details.

#include <stdint.h>
#include <string.h>
#include <stdio.h>

#include "riscv_test.h"

volatile uint64_t tohost;
volatile uint64_t fromhost;

static void do_tohost(uint64_t tohost_value)
{
  while (tohost)
    fromhost = 0;
  tohost = tohost_value;
}

static void cputchar(int x)
{
  do_tohost(0x0101000000000000 | (unsigned char)x);
}

static void cputstring(const char* s)
{
  while (*s)
    cputchar(*s++);
}

static void terminate(int code)
{
  do_tohost(code);
  while (1);
}

#define stringify1(x) #x
#define stringify(x) stringify1(x)
#define assert(x) do { \
  if (x) break; \
  cputstring("Assertion failed: " stringify(x) "\n"); \
  terminate(3); \
} while(0)

#define l1pt pt[0]
#define user_l2pt pt[1]
#if __riscv_xlen == 64
# define NPT 4
#define kernel_l2pt pt[2]
# define user_l3pt pt[3]
#else
# define NPT 2
# define user_l3pt user_l2pt
#endif
pte_t pt[NPT][PTES_PER_PT] __attribute__((aligned(PGSIZE)));
