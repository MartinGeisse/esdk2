
# Why a Bus?

The need for a bus comes from using a CPU. Without that CPU, a "soup of devices" would
communicate via point-to-point wires.

For a "discrete chip" CPU, the need for a bus comes mainly from reducing the pin count,
multiplexing data to/from different devices over the same pins. This is not relevant for
an FPGA -- even though some modules should watch their fan-out, the pressure to do so is
much less, and the cost to do so may be much higher.

# Why a Bus in an FPGA?

In an FPGA, a CPU would use a bus because
* the same module initiates a transfer each time (the CPU, making it the master). Since
the control lines are the same for each transfer and, except for chip-select, for each
addressed device, it makes sense to treat them as the same signals.
* the address comes from the same sources (the CPU'S program counter or the CPU's
register file) in all transfers. Since no other parallel transfers can occur, these
address lines can be routed to all slaves directly. Like the control lines, even though
they are different wires with different destinations, treating them as a unit simplifies
the design.
* the write-source and read-destination are always the register file or the instruction
register, no matter the device they came from. Here, an opportunity shows: The list of
devices that can provide an instruction can usually be limited to very few, such as
"a built-in instruction memory or the memory controller, but never an I/O device". With
an I-cache, the same opportunity shows on the bus side of that cache. With an I/D
shared second level cache, however, it is lost.
* the write-destination is always one of the devices, and the other devices do not take
part in bus transfers during that time. This allows to route the write data directly
from the register file (or instruction register, if immediate write-data is allowed)
to all devices in parallel.
* the read-source is a mux from all devices that can be read from. This is necessary
anyway since the read-destination is the same for all reads.

A bus is the easiest way to manage the design.

# No Buses in the High-Level Model

In the abstract high-level model, the CPU's behavior represents the CPU and its program.
The device's behavior represents the slave devices, possibly without even separating
them properly. In such a model, the CPU object would talk to whatever device object it
wants without the need for a bus. This level validates system behavior.

The first refinement might add cost to such communication, e.g. the time needed to
perform a transfer and the code size needed to implement it. Queues or bulk transfers
can be added in response. There might be contention points that require parallelism.
Later refinement might suggest a multi-master bus to reduce design size, which might
*create* contention points. This level validates fundamental performance assumptions.

Further down, the real bus starts to appear. The CPU no longer talks to devices, but to
the bus. Devices respond to the bus. Devices must now be properly separated. The
bus may still use abstract address and data types, so there is no bus map yet. A single
transfer at this level may be broken down to multiple transfers later. This step
implements the design in a very coarse way.

Finally, at implementation, the abstract bus must be mapped to a real bus specification
such as Wishbone. A bus map must be built. Software stubs can be generated for this,
or with re-usable components, software libraries used. The bus logic itself can eb
generated. This step is the last one before RTL -- all communication must be RTL-like,
only the internal implementation may be left unspecified to validate the design again
before implementing it.
