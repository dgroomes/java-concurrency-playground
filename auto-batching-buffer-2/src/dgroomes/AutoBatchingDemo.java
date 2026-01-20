package dgroomes;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class AutoBatchingDemo {
    private static final Logger log = Logger.getAnonymousLogger();

    private static final int NUM_WRITERS = 6;
    private static final int RECORDS_PER_WRITER = 10;
    private static final int MAX_BATCH_SIZE = 12;
    private static final Duration FIXED_LATENCY = Duration.ofMillis(40);
    private static final Duration PER_ELEMENT_COST = Duration.ofMillis(8);

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT.%1$tL %4$s [%3$s] %5$s%6$s%n");

        log.info("=== Auto-Batching Buffer Demo ===");
        log.info("Writers: %d, Records per writer: %d".formatted(NUM_WRITERS, RECORDS_PER_WRITER));
        log.info("Max batch size: %d".formatted(MAX_BATCH_SIZE));
        log.info("Fixed latency: %dms, Per-element cost: %dms".formatted(
                FIXED_LATENCY.toMillis(), PER_ELEMENT_COST.toMillis()));
        log.info("");

        var database = new SimulatedDatabase(FIXED_LATENCY, PER_ELEMENT_COST);
        var autoBatchingBuffer = new AutoBatchingBuffer(database, MAX_BATCH_SIZE);

        Instant start = Instant.now();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var latch = new CountDownLatch(NUM_WRITERS);

            for (int w = 0; w < NUM_WRITERS; w++) {
                final int writerId = w;
                executor.submit(() -> {
                    try {
                        for (int r = 0; r < RECORDS_PER_WRITER; r++) {
                            String record = "w%d-r%d".formatted(writerId, r);
                            autoBatchingBuffer.write(record);
                        }
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
    }

    static class SimulatedDatabase {
        private final Duration fixedLatency;
        private final Duration perElementCost;
        private final AtomicInteger totalRecordsWritten = new AtomicInteger();
        private final AtomicInteger totalFlushOperations = new AtomicInteger();

        SimulatedDatabase(Duration fixedLatency, Duration perElementCost) {
            this.fixedLatency = fixedLatency;
            this.perElementCost = perElementCost;
        }

        void write(List<String> records) throws InterruptedException {
            long totalMs = fixedLatency.toMillis() + (records.size() * perElementCost.toMillis());
            Thread.sleep(totalMs);
            totalRecordsWritten.addAndGet(records.size());
            totalFlushOperations.incrementAndGet();
        }

        int totalRecordsWritten() { return totalRecordsWritten.get(); }
        int totalFlushOperations() { return totalFlushOperations.get(); }
    }

    static class AutoBatchingBuffer {
        private final SimulatedDatabase database;
        private final int maxBatchSize;
        private final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        private final AtomicInteger wip = new AtomicInteger(0);
        private final ExecutorService flushExecutor = Executors.newSingleThreadExecutor();

        AutoBatchingBuffer(SimulatedDatabase database, int maxBatchSize) {
            this.database = database;
            this.maxBatchSize = maxBatchSize;
        }

        void write(String record) {
            queue.add(record);
            tryScheduleFlush();
        }

        private void tryScheduleFlush() {
            if (wip.getAndIncrement() == 0) {
                flushExecutor.submit(this::flushLoop);
            }
        }

        private void flushLoop() {
            int missed = 1;
            while (true) {
                // Drain queue and flush until empty
                drainAndFlush();

                // Check if more work arrived while we were flushing
                missed = wip.addAndGet(-missed);
                if (missed == 0) {
                    return;
                }
            }
        }

        private void drainAndFlush() {
            while (true) {
                List<String> batch = drainQueue();
                if (batch.isEmpty()) {
                    return;
                }
                try {
                    database.write(batch);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }

        private List<String> drainQueue() {
            List<String> batch = new ArrayList<>(maxBatchSize);
            for (int i = 0; i < maxBatchSize; i++) {
                String element = queue.poll();
                if (element == null) {
                    break;
                }
                batch.add(element);
            }
            return batch;
        }

        void awaitCompletion() throws InterruptedException {
            // Submit a task to the single-thread executor.
            // It will only run after any active flushLoop() completes.
            try {
                flushExecutor.submit(() -> {}).get();
            } catch (ExecutionException e) {
                throw new RuntimeException("Wait failed", e);
            }
            flushExecutor.shutdown();
            flushExecutor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }
}
