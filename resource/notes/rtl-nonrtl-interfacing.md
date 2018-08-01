
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
