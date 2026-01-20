# auto-batching-buffer

Exploring an auto-batching buffer that achieves efficient batching through natural backpressure, without explicit flush
calls or timers.


## Overview

This project explores a pattern for concurrent writers pushing data across a latency boundary (e.g., network writes to a
database). The key insight: **batching emerges naturally from backpressure** when multiple writers contend for a shared
resource.

Traditional buffering requires either:
- **Explicit flush calls** - callers must coordinate to call `flush()` at the end
- **Timer-based flushing** - flush every N milliseconds (adds latency, complexity)
- **Size-based flushing** - flush when buffer reaches N elements (requires final flush coordination)

The auto-batching buffer needs none of these. Writers simply call `write()` and the buffer handles everything.


## How It Works

The buffer uses a semaphore to ensure only one flush operation happens at a time:

1. **Writer calls `write()`** - element goes into a queue
2. **If no flush is in progress** - immediately start flushing the queue
3. **If flush is in progress** - the write returns; the element waits in the queue
4. **When flush completes** - if queue has elements, immediately start another flush

The magic: while one flush pays the network latency, concurrent writers accumulate in the queue. When the flush
completes, all queued elements flush together as a batch. **Batching emerges from the natural backpressure of I/O
latency.**

```
Writer 1: write(A) ──► [flush A] ─────────────────────► done
Writer 2: write(B) ──► [queued] ─────────────────────►──► [flush B,C,D together] ──► done
Writer 3: write(C) ──► [queued] ─────────────────────►──►
Writer 4: write(D) ──► [queued] ─────────────────────►──►
                       ▲                              ▲
                       │                              │
                   flush in progress              flush completes,
                   B,C,D queue up                 batch flush starts
```


## Cost Model

The simulated database has:
- **Fixed latency**: 50ms per flush operation (network round-trip overhead)
- **Per-element cost**: 10ms per element in the batch

| Approach | 5 elements | Cost calculation |
|----------|------------|------------------|
| One-at-a-time | 5 × (50 + 10) = **300ms** | Each write pays full overhead |
| Batched (5 together) | 50 + (5 × 10) = **100ms** | Overhead paid once |


## Instructions

1. Use Java 21
2. Run the eager-flush demo (baseline - poor throughput):
   * ```shell
     java src/dgroomes/EagerFlushDemo.java
     ```
3. Run the auto-batching demo (solution - batching via backpressure):
   * ```shell
     java src/dgroomes/AutoBatchingDemo.java
     ```

Compare the total execution times to see how natural backpressure enables efficient batching.
