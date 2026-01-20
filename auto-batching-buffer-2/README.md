# auto-batching-buffer-2

Auto-batching for concurrent writers across a latency boundary, without explicit flush calls or timers.


## Overview

This project models a shared remote resource (e.g., a database connection) that can only handle one flush at a time.
Multiple concurrent writers call `write()` with no coordination. The buffer automatically batches writes by exploiting
natural backpressure:

- If no flush is in progress, the writer triggers a flush immediately.
- If a flush is in progress, writers enqueue and return.
- When the flush completes, any queued elements flush as a batch.

Batching emerges from contention and latency, not from timers or explicit `flush()` calls.


## Cost model

Each flush costs:

$$
	ext{cost} = \text{fixed latency} + (\text{elements} \times \text{per-element cost})
$$

Batching amortizes the fixed latency cost across many elements.


## Instructions

1. Use Java 21
2. Run the eager-flush baseline:
   * ```shell
     java src/dgroomes/EagerFlushDemo.java
     ```
3. Run the auto-batching buffer:
   * ```shell
     java src/dgroomes/AutoBatchingDemo.java
     ```

Compare the execution time and the number of flushes to see the effect of automatic batching.
