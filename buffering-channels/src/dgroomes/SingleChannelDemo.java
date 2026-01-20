package dgroomes;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Demonstrates the "head-of-line blocking" problem with a single-channel buffered write approach.
 *
 * This simulates writing records to a remote database over a high-latency network connection.
 * The buffer collects records, and when full, flushes them to the "database" (simulated with a sleep).
 * During the flush, the program blocks completely—it cannot buffer any more records.
 */
public class SingleChannelDemo {
    private static final Logger log = Logger.getAnonymousLogger();

    private static final int TOTAL_RECORDS = 20;
    private static final int BUFFER_SIZE = 5;
    private static final Duration NETWORK_LATENCY = Duration.ofMillis(500);
    private static final Duration RECORD_PROCESSING_TIME = Duration.ofMillis(50);

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

        log.info("=== Single-Channel Buffered Write Demo ===");
        log.info("This demonstrates HEAD-OF-LINE BLOCKING.");
        log.info("Total records: %d, Buffer size: %d, Network latency: %dms".formatted(
                TOTAL_RECORDS, BUFFER_SIZE, NETWORK_LATENCY.toMillis()));
        log.info("");

        var database = new SimulatedDatabase(NETWORK_LATENCY);
        var buffer = new SingleChannelBuffer(BUFFER_SIZE, database);

        Instant start = Instant.now();

        for (int i = 1; i <= TOTAL_RECORDS; i++) {
            Thread.sleep(RECORD_PROCESSING_TIME);
            log.info("Produced record #%d".formatted(i));
            buffer.write("record-" + i);
        }

        buffer.flush();

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);

        log.info("");
        log.info("=== Results ===");
        log.info("Total records written: %d".formatted(database.totalRecordsWritten()));
        log.info("Total flush operations: %d".formatted(database.totalFlushOperations()));
        log.info("Total execution time: %s".formatted(elapsed));
        log.info("");
        log.info("Notice how the program blocks during each flush, wasting time that could be spent buffering.");
    }

    static class SimulatedDatabase {
        private final Duration latency;
        private int totalRecordsWritten = 0;
        private int totalFlushOperations = 0;
        private static final Logger log = Logger.getAnonymousLogger();

        SimulatedDatabase(Duration latency) {
            this.latency = latency;
        }

        void write(List<String> records) throws InterruptedException {
            log.info("  [DB] Flushing %d records over the network...".formatted(records.size()));
            Thread.sleep(latency);
            totalRecordsWritten += records.size();
            totalFlushOperations++;
            log.info("  [DB] Flush complete.");
        }

        int totalRecordsWritten() { return totalRecordsWritten; }
        int totalFlushOperations() { return totalFlushOperations; }
    }

    static class SingleChannelBuffer {
        private final int capacity;
        private final SimulatedDatabase database;
        private final List<String> buffer = new ArrayList<>();
        private static final Logger log = Logger.getAnonymousLogger();

        SingleChannelBuffer(int capacity, SimulatedDatabase database) {
            this.capacity = capacity;
            this.database = database;
        }

        void write(String record) throws InterruptedException {
            buffer.add(record);
            if (buffer.size() >= capacity) {
                log.info("Buffer full, flushing... (BLOCKING - cannot accept more records!)");
                flush();
            }
        }

        void flush() throws InterruptedException {
            if (!buffer.isEmpty()) {
                database.write(new ArrayList<>(buffer));
                buffer.clear();
            }
        }
    }
}
