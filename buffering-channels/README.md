# buffering-channels

WORK IN PROGRESS (Opus 4.5 first cut)

Exploring buffered writes with multiple "channels" to improve throughput over high-latency network connections.


## Overview

This project explores a pattern for improving write throughput when sending data to a remote database (or any I/O
destination with high latency). The classic `BufferedWriter` pattern collects elements until a buffer is full, then
flushes—but during the flush, the program blocks and can't do anything else. This is called *head-of-line blocking*.

The idea: use multiple "channels" (buffers) so that while one channel is flushing over the network, another channel can
continue accepting writes. This overlaps the buffering and I/O phases to improve overall throughput.


## The Problem

Consider a program that writes many records to a remote database:

```
for each record:
    buffer.write(record)
    if buffer is full:
        buffer.flush()  // BLOCKS for network round-trip!
buffer.flush()  // final flush
```

With a single buffer, the program alternates between:
1. **Buffering phase**: Fast, in-memory work collecting records
2. **Flush phase**: Slow, blocked waiting for network I/O

The flush phase wastes time because the program has more records ready to buffer, but it must wait.


## The Solution

With two channels:

```
Channel 1: [buffer...] [FLUSHING] [buffer...] [FLUSHING] ...
Channel 2: [idle]      [buffer...] [FLUSHING] [buffer...] ...
                       ^-- buffering continues while Channel 1 flushes
```

When Channel 1's buffer is full and starts flushing, writes immediately switch to Channel 2. The program only blocks
when *both* channels are flushing (or awaiting the final flush).


## Instructions

1. Use Java 21
2. Run the single-channel demo (shows the problem):
   * ```shell
     java src/dgroomes/SingleChannelDemo.java
     ```
3. Run the multi-channel demo (shows the solution):
   * ```shell
     java src/dgroomes/MultiChannelDemo.java
     ```

Compare the total execution times to see the throughput improvement.
