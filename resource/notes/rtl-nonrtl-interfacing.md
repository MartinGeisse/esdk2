
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
* By scheduling events to sample the signals which always happen at times different than clock edges
* By scheduling events for the clock edges that also sample the signals, and do so realiably either
before or after the clock edge
* By having the sampling module conform to RTL interfaces, by extending RtlClockedItem

TODO

# Signal direction: Highlevel-to-RTL

TODO
