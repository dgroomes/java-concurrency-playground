package dgroomes;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Demonstrates the auto-batching buffer pattern.
 *
 * Batching emerges naturally from backpressure: while one flush is in progress paying the
 * network latency, concurrent writers queue up. When the flush completes, all queued elements
 * flush together as a batch, amortizing the fixed latency cost.
 *
 * No explicit flush() calls needed. No timers. Just natural backpressure.
 */
public class AutoBatchingDemo {
    private static final Logger log = Logger.getAnonymousLogger();

    private static final int NUM_WRITERS = 5;
    private static final int RECORDS_PER_WRITER = 4;
    private static final Duration FIXED_LATENCY = Duration.ofMillis(50);
    private static final Duration PER_ELEMENT_COST = Duration.ofMillis(10);

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT.%1$tL %4$s [%3$s] %5$s%6$s%n");

        log.info("=== Auto-Batching Buffer Demo ===");
        log.info("This demonstrates EFFICIENT BATCHING via natural backpressure.");
        log.info("No explicit flush calls, no timers - batching happens automatically.");
        log.info("");
        log.info("Writers: %d, Records per writer: %d".formatted(NUM_WRITERS, RECORDS_PER_WRITER));
        log.info("Fixed latency: %dms, Per-element cost: %dms".formatted(
                FIXED_LATENCY.toMillis(), PER_ELEMENT_COST.toMillis()));
        log.info("");

        var database = new SimulatedDatabase(FIXED_LATENCY, PER_ELEMENT_COST);
        var autoBatchingBuffer = new AutoBatchingBuffer(database);

        Instant start = Instant.now();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var latch = new CountDownLatch(NUM_WRITERS);

            for (int w = 0; w < NUM_WRITERS; w++) {
                final int writerId = w;
                executor.submit(() -> {
                    try {
                        for (int r = 0; r < RECORDS_PER_WRITER; r++) {
                            String record = "w%d-r%d".formatted(writerId, r);
                            log.info("Writer %d: writing %s".formatted(writerId, record));
                            autoBatchingBuffer.write(record);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
        }

        autoBatchingBuffer.awaitCompletion();

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);

        log.info("");
        log.info("=== Results ===");
        log.info("Total records written: %d".formatted(database.totalRecordsWritten()));
        log.info("Total flush operations: %d".formatted(database.totalFlushOperations()));
        log.info("Total execution time: %s".formatted(elapsed));
        log.info("");
        log.info("Batching via backpressure amortized the fixed latency cost!");
        log.info("Compare to eager flush: %d × (%dms + %dms) = %dms".formatted(
                NUM_WRITERS * RECORDS_PER_WRITER,
                FIXED_LATENCY.toMillis(),
                PER_ELEMENT_COST.toMillis(),
                NUM_WRITERS * RECORDS_PER_WRITER * (FIXED_LATENCY.toMillis() + PER_ELEMENT_COST.toMillis())));
    }

    static class SimulatedDatabase {
        private final Duration fixedLatency;
        private final Duration perElementCost;
        private final AtomicInteger totalRecordsWritten = new AtomicInteger();
        private final AtomicInteger totalFlushOperations = new AtomicInteger();
        private static final Logger log = Logger.getAnonymousLogger();

        SimulatedDatabase(Duration fixedLatency, Duration perElementCost) {
            this.fixedLatency = fixedLatency;
            this.perElementCost = perElementCost;
        }

        void write(List<String> records) throws InterruptedException {
            long totalMs = fixedLatency.toMillis() + (records.size() * perElementCost.toMillis());
            log.info("  [DB] Flushing %d record(s)... (cost: %dms)".formatted(records.size(), totalMs));
            Thread.sleep(totalMs);
            totalRecordsWritten.addAndGet(records.size());
            totalFlushOperations.incrementAndGet();
            log.info("  [DB] Flush complete.");
        }

        int totalRecordsWritten() { return totalRecordsWritten.get(); }
        int totalFlushOperations() { return totalFlushOperations.get(); }
    }

    /**
     * An auto-batching buffer that achieves batching through natural backpressure.
     *
     * How it works:
     * - Writers add elements to a queue and try to acquire the flush semaphore
     * - If acquired: drain the queue and flush (while holding semaphore)
     * - If not acquired: element is queued; the current flusher will pick it up
     * - When a flush completes: if queue has elements, immediately flush again
     *
     * Batching emerges naturally: during the latency of a flush, concurrent writers
     * accumulate in the queue. The next flush handles them all as a batch.
     */
    static class AutoBatchingBuffer {
        private final SimulatedDatabase database;
        private final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        private final Semaphore flushSemaphore = new Semaphore(1);
        private final ExecutorService flushExecutor = Executors.newSingleThreadExecutor();
        private volatile Future<?> lastFlush = CompletableFuture.completedFuture(null);
        private static final Logger log = Logger.getAnonymousLogger();

        AutoBatchingBuffer(SimulatedDatabase database) {
            this.database = database;
        }

        void write(String record) throws InterruptedException {
            queue.add(record);
            tryFlush();
        }

        private void tryFlush() {
            if (flushSemaphore.tryAcquire()) {
                lastFlush = flushExecutor.submit(() -> {
                    try {
                        flushLoop();
                    } finally {
                        flushSemaphore.release();
                    }
                });
            }
        }

        private void flushLoop() {
            while (true) {
                List<String> batch = drainQueue();
                if (batch.isEmpty()) {
                    return;
                }

                log.info("Auto-batching: flushing batch of %d elements".formatted(batch.size()));
                try {
                    database.write(batch);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        private List<String> drainQueue() {
            List<String> batch = new ArrayList<>();
            String element;
            while ((element = queue.poll()) != null) {
                batch.add(element);
            }
            return batch;
        }

        void awaitCompletion() throws InterruptedException {
            try {
                lastFlush.get();
            } catch (ExecutionException e) {
                throw new RuntimeException("Flush failed", e);
            }
            flushExecutor.shutdown();
            flushExecutor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
