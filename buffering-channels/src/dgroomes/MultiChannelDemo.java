package dgroomes;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Demonstrates the multi-channel buffering solution to improve throughput over high-latency connections.
 *
 * The key insight: use two channels (buffers) so that while one is flushing over the network,
 * the other can continue accepting writes. The program only blocks when BOTH channels are busy.
 *
 * This overlaps the buffering and I/O phases to improve overall throughput.
 */
public class MultiChannelDemo {
    private static final Logger log = Logger.getAnonymousLogger();

    private static final int TOTAL_RECORDS = 20;
    private static final int BUFFER_SIZE = 5;
    private static final Duration NETWORK_LATENCY = Duration.ofMillis(500);
    private static final Duration RECORD_PROCESSING_TIME = Duration.ofMillis(50);

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");

        log.info("=== Multi-Channel Buffered Write Demo ===");
        log.info("This demonstrates OVERLAPPED I/O with two channels.");
        log.info("Total records: %d, Buffer size: %d, Network latency: %dms".formatted(
                TOTAL_RECORDS, BUFFER_SIZE, NETWORK_LATENCY.toMillis()));
        log.info("");

        var database = new SimulatedDatabase(NETWORK_LATENCY);
        var multiBuffer = new MultiChannelBuffer(2, BUFFER_SIZE, database);

        Instant start = Instant.now();

        for (int i = 1; i <= TOTAL_RECORDS; i++) {
            Thread.sleep(RECORD_PROCESSING_TIME);
            log.info("Produced record #%d".formatted(i));
            multiBuffer.write("record-" + i);
        }

        multiBuffer.close();

        Instant end = Instant.now();
        Duration elapsed = Duration.between(start, end);

        log.info("");
        log.info("=== Results ===");
        log.info("Total records written: %d".formatted(database.totalRecordsWritten()));
        log.info("Total flush operations: %d".formatted(database.totalFlushOperations()));
        log.info("Total execution time: %s".formatted(elapsed));
        log.info("");
        log.info("Compare this to the single-channel demo to see the throughput improvement!");
    }

    static class SimulatedDatabase {
        private final Duration latency;
        private int totalRecordsWritten = 0;
        private int totalFlushOperations = 0;
        private static final Logger log = Logger.getAnonymousLogger();

        SimulatedDatabase(Duration latency) {
            this.latency = latency;
        }

        synchronized void write(List<String> records) throws InterruptedException {
            log.info("  [DB] Flushing %d records over the network...".formatted(records.size()));
            Thread.sleep(latency);
            totalRecordsWritten += records.size();
            totalFlushOperations++;
            log.info("  [DB] Flush complete.");
        }

        synchronized int totalRecordsWritten() { return totalRecordsWritten; }
        synchronized int totalFlushOperations() { return totalFlushOperations; }
    }

    /**
     * A thread-safe multi-channel buffer that maintains multiple "channels" (buffers).
     * When the active channel fills up and starts flushing, writes switch to the next available channel.
     * The program only blocks when all channels are busy flushing.
     *
     * Thread-safety: All public methods are synchronized to allow safe concurrent writes from
     * multiple threads. The lock is held only during the fast buffer operations; the slow network
     * I/O happens asynchronously outside the lock.
     */
    static class MultiChannelBuffer {
        private final int bufferCapacity;
        private final SimulatedDatabase database;
        private final ExecutorService flushExecutor;
        private final Channel[] channels;
        private int activeChannelIndex = 0;
        private boolean closed = false;
        private static final Logger log = Logger.getAnonymousLogger();

        MultiChannelBuffer(int numChannels, int bufferCapacity, SimulatedDatabase database) {
            this.bufferCapacity = bufferCapacity;
            this.database = database;
            this.flushExecutor = Executors.newVirtualThreadPerTaskExecutor();
            this.channels = new Channel[numChannels];
            for (int i = 0; i < numChannels; i++) {
                channels[i] = new Channel(i);
            }
        }

        synchronized void write(String record) throws InterruptedException {
            if (closed) {
                throw new IllegalStateException("Buffer is closed");
            }

            Channel active = channels[activeChannelIndex];

            active.buffer.add(record);

            if (active.buffer.size() >= bufferCapacity) {
                log.info("Channel %d buffer full, initiating async flush...".formatted(activeChannelIndex));

                List<String> toFlush = new ArrayList<>(active.buffer);
                active.buffer.clear();

                active.pendingFlush = flushExecutor.submit(() -> {
                    database.write(toFlush);
                    return null;
                });

                int nextChannel = (activeChannelIndex + 1) % channels.length;
                Channel next = channels[nextChannel];

                if (next.pendingFlush != null) {
                    log.info("Channel %d is still flushing, waiting... (BLOCKING)".formatted(nextChannel));
                    try {
                        next.pendingFlush.get();
                    } catch (ExecutionException e) {
                        throw new RuntimeException("Flush failed", e);
                    }
                    next.pendingFlush = null;
                    log.info("Channel %d is now available.".formatted(nextChannel));
                } else {
                    log.info("Switching to channel %d (no blocking needed!)".formatted(nextChannel));
                }

                activeChannelIndex = nextChannel;
            }
        }

        synchronized void close() throws InterruptedException {
            if (closed) {
                return;
            }
            closed = true;

            Channel active = channels[activeChannelIndex];
            if (!active.buffer.isEmpty()) {
                log.info("Final flush of %d remaining records on channel %d...".formatted(
                        active.buffer.size(), activeChannelIndex));
                try {
                    database.write(new ArrayList<>(active.buffer));
                    active.buffer.clear();
                } catch (InterruptedException e) {
                    throw e;
                }
            }

            for (int i = 0; i < channels.length; i++) {
                if (channels[i].pendingFlush != null) {
                    try {
                        channels[i].pendingFlush.get();
                    } catch (ExecutionException e) {
                        throw new RuntimeException("Flush failed", e);
                    }
                }
            }

            flushExecutor.shutdown();
            flushExecutor.awaitTermination(10, TimeUnit.SECONDS);
        }

        static class Channel {
            final int id;
            final List<String> buffer = new ArrayList<>();
            Future<Void> pendingFlush = null;

            Channel(int id) {
                this.id = id;
            }
        }
    }
}
