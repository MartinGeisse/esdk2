
# Problem

How to interface beween RTL models and higher-level models. The exact problem and the preferred solution
is different for the two signal directions.

# Signal direction: RTL-to-Highlevel

This might seem obvious, but it isn't. The RTL code produces signals which can be asked for their
value at any time. By sampling those signals at the correct time, the high-level code can capture
values from RTL code.

What is the correct time? RTL principles say: between clock edges, because the signals are
stable then. Don't sample at clock edges because the signals change then, and you may get
invalid values which are neither the previous nor the next value.

There are three ways to do this correctly:
* By having the sampling module conform to RTL interfaces, by extending RtlClockedItem. This is recommended for
re-usable modules because it does not require any glue code. The module should sample its inputs during stable clock
levels and update its outputs at clock edges, just like any other RTL module.
* By scheduling events for the clock edges that also sample the signals, and do so realiably either
before or after the clock edge. This is a quick and easy solution for custom modules, but requires glue code that
notifies the module before or after clock edges.
* By scheduling events to sample the signals which always happen at times different than clock edges. In RTL terms,
this works similar to a two-phase clock, with one phase driving the high-level module and the other phase driving the
normal RTL part.

# Signal direction: Highlevel-to-RTL

To transfer data from a high-level module to RTL, the high-level code exposes its state through the RtlBitSignal and
RtlVectorSignal interfaces. It only has to ensure that changes to the signal values do not happen "at clock edges".
There are two ways to do this:
* The same events that drive the RTL clock also calls methods that change the outputs of the high-level module, and
does so reliably either before or after the clock edge.
* The output-changing methods are called by events that are scheduled at different times than those that drive the
clock edges.

What is *not* allowed is to drive output-changing methods and clock edges in different events scheduled at the same
time, because then the order in which these events are processed is undefined.

# Other Notes


## 1

Building such an interface is hard. So we need a set of standard classes to handle it properly.
These should correspond to standard synthesizable interfaces to avoid special coding just
for simulation. An obvious example is bus masters and slaves, e.g. standard implementations to
connect a high-level model to an RTL model of a CPU on a Wishbone bus; connecting a
high-level CPU model (or even threaded item) to an RTL model of a bus slave device, with or
without a bus model in between; connecting and RTL (highlevel) producer to a highlevel (RTL)
consumer via a FIFO queue, and so on.

The Picoblaze gets special treatment here because it is very popular and uses its own custom
bus specification. So we provide an implementation for this RTL-to-highlevel interface even
though it is very specialized.

## 2

Bridging between RTL and non-RTL is even more painful than I first thought. RTL has strict requirements with
respect to its computeNextState() / updateState() cycle. Non-RTL wants to do both at the same time. This is
fundamentally incompatible, making "bridge" components such as buses and FIFOs even more important. Writing these
bridge components is HARD HARD HARD. Nothing for beginners. I wonder if there is any way to keep people from writing
such bridges in a wrong way.

For the Picoblaze, two different implementations make sense (RtlPicoblaze, InstructionLevelPicoblaze). Unlike I first
thought, this is not because of simulation performance, but because their external interfaces are fundamentally
different. Unfortunately this also means that there can't be a half-RTL, half-highlevel Picoblaze. To do this, one
has to take the RtlPicoblaze and then bridge to highlevel for either the program, or ports, or reset signals.

Internally, PicoblazeState has to support both models, but that's not too hard. It just means splitting methods even
further. Easiest way is probably to make that class abstract and use subclasses in the RtlPicoblaze and
InstructionLevelPicoblaze that implement the specified methods to react to different cycle events such as
* read instruction in a non-mutating way (maybe use an instruction setter instead -- might be simpler and give
    better performance)
* read from port address X in a non-mutating way
* commit read from port address X in a mutating way but without getting data
* commit write in a mutating way
* (getting port address and write data -- this is already supported by decoding the instruction)

## 3

An even better way would be to rise from RTL to one level higher. Don't work with individual clock cycles; work
with data packets, events and transactions. This bundles lower-level data together with a corresponding lower-level
clock enable.

Transaction schemes:
* push: A pushes data to B when a supplies the data and the clock enable.
* pull: B pulls data from A when A supplies the data but B supplies the clock enable.
* push with handshake: A pushes data to B with handshake when A supplies the data and a request flag and B provides
    an accept flag. An accept cannot occur without a request.
* pull with handshake: B pulls data from A with handshake when A supplies the data, B supplies a request flag and
    A provides an accept flag. An accept cannot occur without a request.
* mutual agreement transfer: combines push/pull with handshake by saying that both control signals can occur
    independently, and a transfer only occurs when both are active.
* request-response: A provides request data and a request flag, and B provides response data and a response flag.

Protocols:
Each of these transaction schemes can be implemented using different actual low-level protocols.

Protocol transformers:
Makes transaction endpoints compatible that are using different protocols.

Switches:
When multiple possible endpoints A1..N and B1..N are connected to a switch, the data or additional control signals
are used at runtime to select specific endpoints Ai and Bj to perform a transaction.

Examples:
* A typical bus between CPU and peripherals uses request-response and a switch
* A simple FIFO queue that "spills" data when full uses the push scheme at the input and the pull scheme at the output
* A FIFO with spill prevention uses push with handshake; a FIFO with underflow prevention uses pull with handshake
* A simple general-purpose I/O register uses the push scheme to write data and the pull scheme to read data
* A stallable pipeline uses mutual agreement transfer between the stages

Is this useful? Yes. Is it useful alone? No. Neither the transaction logic nor the endpoints can be synthesized based
on this highlevel description -- they both have to go down to RTL for synthesis. So what is described where is not
really a replacement for RTL; it is a high-level modeling scheme that provides possible interface points between
RTL models and highlevel models.

This also makes it a bit more obvious why a half-RTL, half-highlevel Picoblaze is so hard to do: The Picoblaze is a
bad interface point, and certainly not one of the good interface points described above.
