# auto-batching-buffer-2

WORK IN PROGRESS: Barely reviewed, and AI sloppy, but useful for what I need.

Auto-batching for concurrent writers across a latency boundary, without explicit flush calls or timers.


## Overview

This project models a shared remote resource (e.g., a database connection) that can only handle one flush at a time.
Multiple concurrent writers call `write()` with no coordination. The buffer automatically batches writes by exploiting
natural backpressure:

- If no flush is in progress, the writer triggers a flush immediately.
- If a flush is in progress, writers enqueue and return.
- When the flush completes, any queued elements flush as a batch.

Batching emerges from contention and latency, not from timers or explicit `flush()` calls.


## Algorithm & Thread Safety

The core challenge in auto-batching is avoiding race conditions. A naive implementation might look like this:
1. Writer adds to queue.
2. Writer tries to acquire lock.
3. If successful, drain queue and flush.

**The Race Condition (The "Stranded Item" problem):**
If a writer adds an item to the queue *after* the flusher has finished draining but *before* the flusher releases the lock, the writer fails to acquire the lock (so it does nothing), and the flusher exits (thinking it's done). The item remains stranded in the queue until the *next* write happens.

**The Solution: Atomic WIP Counter**
This project uses an `AtomicInteger` Work-In-Progress (WIP) counter to ensure no signals are lost. This determines who is responsible for scheduling the flush loop.

**Writer Logic:**
```java
queue.add(record);
// 0 -> 1 transition means we must start the loop.
// 1 -> 2+ transitions mean a loop is already running, so we just leave.
if (wip.getAndIncrement() == 0) {
    scheduleFlushLoop();
}
```

**Flusher Logic:**
The flusher doesn't just run once; it loops until it has consumed all "signals" that arrived while it was busy.
```java
int missed = 1;
while (true) {
    drainAndFlush();

    // Atomically satisfy 'missed' number of signals.
    // If result > 0, more work arrived while we were flushing!
    missed = wip.addAndGet(-missed);
    if (missed == 0) {
        return; // Safe to exit: queue is empty AND no new signals exist.
    }
}
```
This guarantees that if a writer adds work while a flush is in progress, the flusher observes the incremented counter before exiting and loops back to drain the queue again.


## Cost model

Each flush costs:

$$
  ext{cost} = \text{fixed latency} + (\text{elements} \times \text{per-element cost})
$$

Batching amortizes the fixed latency cost across many elements.


## Sample results

These are representative results from the included demos (60 total records):

- Eager flush: 60 flushes, ~3.13s
- Auto-batching: 5 flushes, ~0.70s

This shows the fixed latency cost being amortized across batches.


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
