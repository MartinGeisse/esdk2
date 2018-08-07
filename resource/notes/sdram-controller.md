
There are three different scenarios to handle: single-client, multi-client and refresh.

# Refresh

Refresh addresses can be generated inside the SDRAM chip, so the controller only needs a timer to
decide when to start and stop refresh phases. No addresses are handled here. Refresh must be taken
into account in arbitration: Once a row is opened, refresh has to wait until the client that opened
the row closes it again. The maximum allowed time until next refresh must not be less than the expected
time-to-close of the client that opened the current row.

Only very specific scenarios couple refresh to normal accesses.

# Multi-Client

This is interesting because the design is different for different interaction patterns between clients.
Two important cases come to my mind:

* Multiple cores that work on the same data. Here we have to detect access to the same memory for
correctness and also want to detect same-row access for improved performance. This is important in this
case because accesses to the same row are very likely. The whole row selection protocol can be built
around the fact that same-row detection is needed in any case.

* Independent cores. No same-row detection happens between clients; a client opens a row and accesses
memory until done. Arbitration is needed between clients which may also take the expected time-until-close
into account.

There is also the mixed case in which groups of clients access the same locations, but different groups
act independently. In such a case, same-row detection should happen per group, using an appropriate
row selection protocol. Between different groups, no same-row detection happens and the protocol is
structured accordingly.

For the GPU, we have independent cores (renderer vs. RAMDAC) or possibly a mixed scenario with
multiple related clients inside the renderer. The RAMDAC is independent in any case.

Solution: Separate same-row detection and the corresponding protocol from the main controller. This
supports all three scenarios.

# Single-Client

Here we can ignore both refresh and same-row detection. Instead, a protocol is needed to open and close
rows and to access cells. This will be similar to the original SDRAM protocol.

The docs say that BURST TERMINATE must not be used for burst read access with auto-precharge, and must
not be used for write access. The burst length gets configured in the mode register, so it cannot vary
per access. Auto-precharge gets enabled/disabled per access.

There seems to be little advantage in using auto-precharge. The timing isn't better than issuing an
explicit PRECHARGE command. There are many downsides though: Increased complexity since writes can't
use auto-precharge; all accesses have to use the same fixed burst length.

For an ACTIVE (= RAS) command, the row address only has to be stable for a single clock edge. For the
client protocol, this means that "the address does not have to be saved". However, in terms of signal
quality it is probably a good idea to register the address output to the SDRAM anyway. So we have
one clock edge that accepts a row open request form a client, acknowledges the request, loads the
address output registers and loads the command output registers with an ACTIVE command. Starting with
the next clock edge, the address is not needed by the SDRAM anymore. (Note about edges: WRT commands,
only the rising edge is important. The DDR protocol only affects data, not commands).

Client protocol: The "open" signal must be reset to 0 when the open request gets acknowledged, otherwise
it is interpreted as another open request to a different row (no same-row detection!) The "close"
signal is a different signal, not using the same signal. "open" and "close-open" are interpreted the
same: close the current row and open another one. PROBLEM: The "close" might be executed by the "open"
delayed because of a higher-priority concurrent request or because of refresh.

TODO: 
* should column addresses use the same address lines
* how do banks work with all this
  * opening multiple banks simultaneously, then accessing them interleaved saves a clock cycle per row
    but only if large amounts of data get read/written (i.e. four whole rows) or if the access pattern
    actually needs all banks.
  * all banks can be closed (precharged) at the same time.
  * bottom line: I don't need this for now. One bank at a time is enough for now.
