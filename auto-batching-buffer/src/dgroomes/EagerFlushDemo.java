package dgroomes;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Baseline demo: Concurrent writers with eager (immediate) flushing.
 *
 * Each write immediately flushes to the database, one at a time. This demonstrates
 * the worst-case throughput: every single write pays the full network latency overhead.
 *
 * The database can only handle one flush at a time (simulating a connection or transaction lock),
 * so writers experience head-of-line blocking.
 */
public class EagerFlushDemo {
    private static final Logger log = Logger.getAnonymousLogger();

    private static final int NUM_WRITERS = 5;
    private static final int RECORDS_PER_WRITER = 4;
    private static final Duration FIXED_LATENCY = Duration.ofMillis(50);
    private static final Duration PER_ELEMENT_COST = Duration.ofMillis(10);

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT.%1$tL %4$s [%3$s] %5$s%6$s%n");

        log.info("=== Eager Flush Demo (Baseline) ===");
        log.info("This demonstrates POOR THROUGHPUT with eager flushing.");
        log.info("Each write immediately flushes, paying full latency overhead every time.");
        log.info("");
        log.info("Writers: %d, Records per writer: %d".formatted(NUM_WRITERS, RECORDS_PER_WRITER));
        log.info("Fixed latency: %dms, Per-element cost: %dms".formatted(
                FIXED_LATENCY.toMillis(), PER_ELEMENT_COST.toMillis()));
        log.info("");

        var database = new SimulatedDatabase(FIXED_LATENCY, PER_ELEMENT_COST);
        var eagerBuffer = new EagerFlushBuffer(database);

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
                            eagerBuffer.write(record);
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

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);

        log.info("");
        log.info("=== Results ===");
        log.info("Total records written: %d".formatted(database.totalRecordsWritten()));
        log.info("Total flush operations: %d".formatted(database.totalFlushOperations()));
        log.info("Total execution time: %s".formatted(elapsed));
        log.info("");
        log.info("Expected time ≈ %d records × (%dms + %dms) = %dms".formatted(
                NUM_WRITERS * RECORDS_PER_WRITER,
                FIXED_LATENCY.toMillis(),
                PER_ELEMENT_COST.toMillis(),
                NUM_WRITERS * RECORDS_PER_WRITER * (FIXED_LATENCY.toMillis() + PER_ELEMENT_COST.toMillis())));
        log.info("Every write paid the full latency overhead!");
    }

    static class SimulatedDatabase {
        private final Duration fixedLatency;
        private final Duration perElementCost;
        private final AtomicInteger totalRecordsWritten = new AtomicInteger();
        private final AtomicInteger totalFlushOperations = new AtomicInteger();
        private final Semaphore lock = new Semaphore(1);
        private static final Logger log = Logger.getAnonymousLogger();

        SimulatedDatabase(Duration fixedLatency, Duration perElementCost) {
            this.fixedLatency = fixedLatency;
            this.perElementCost = perElementCost;
        }

        void write(List<String> records) throws InterruptedException {
            lock.acquire();
            try {
                long totalMs = fixedLatency.toMillis() + (records.size() * perElementCost.toMillis());
                log.info("  [DB] Flushing %d record(s)... (cost: %dms)".formatted(records.size(), totalMs));
                Thread.sleep(totalMs);
                totalRecordsWritten.addAndGet(records.size());
                totalFlushOperations.incrementAndGet();
                log.info("  [DB] Flush complete.");
            } finally {
                lock.release();
            }
        }

        int totalRecordsWritten() { return totalRecordsWritten.get(); }
        int totalFlushOperations() { return totalFlushOperations.get(); }
    }

    /**
     * An eager-flush buffer that immediately flushes every write.
     * This is the baseline showing poor throughput.
     */
    static class EagerFlushBuffer {
        private final SimulatedDatabase database;

        EagerFlushBuffer(SimulatedDatabase database) {
            this.database = database;
        }

        void write(String record) throws InterruptedException {
            database.write(List.of(record));
        }
    }
}
