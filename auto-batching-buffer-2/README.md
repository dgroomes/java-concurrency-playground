# auto-batching-buffer-2

A second take on the auto-batching buffer pattern, with a longer-running demo and more flushes.


## Overview

This pattern is for concurrent writers pushing data across a latency boundary (e.g., network I/O). Instead of explicit
`flush()` calls or timers, batching emerges from backpressure:

- Only one flush runs at a time.
- While a flush is in progress, writers keep enqueueing.
- When the flush finishes, the queued elements flush as a batch.

The demos here are tuned to show **many flushes** so the story is clearer.


## Cost model

Each flush costs:

$\text{cost} = \text{fixed latency} + (\text{elements} \times \text{per-element cost})$

This makes batching highly beneficial.


## Instructions

1. Use Java 21
2. Run the baseline:
   * ```shell
     java src/dgroomes/EagerFlushDemo.java
     ```
3. Run the auto-batching version:
   * ```shell
     java src/dgroomes/AutoBatchingDemo.java
     ```

Compare total time and the number of flushes.
