
#ifndef __BOOTLOADER_EXPORTED_PROFILING_H__
#define __BOOTLOADER_EXPORTED_PROFILING_H__

extern void (*profReset)();
extern void (*profLog)(const char *label);
extern void (*profDisplay)();

#endif
