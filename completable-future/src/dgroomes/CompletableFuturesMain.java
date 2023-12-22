package dgroomes;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static java.lang.System.out;

/**
 * Showcasing the effect of concurrent programming and the programming model of {@link CompletableFuture}.
 */
public class CompletableFuturesMain {

    public static void main(String[] args) {
        out.println("Executing the program with an implementation that uses synchronously executed simulated work. In other words, *no concurrency*.");
        new App(false).execute();

        out.println("Executing the program with an implementation that kicks off simulated work concurrently. Notice how it executes quicker than before.");
        new App(true).execute();
    }
}

/**
 * A toy class that simulates doing some long-lasting work on the order of seconds. This class helps us explore how we
 * code around methods that return {@link CompletableFuture} by using concurrent programming constructs in Java.
 */
class Worker {

    enum Amount {

        SMALL(1, "Performed a small amount of work (1 second)"),
        MEDIUM(2, "Performed a medium amount of work (2 seconds)"),
        LARGE(4, "Performed a large amount of work (4 seconds)");

        private final Duration latency;
        private final String description;

        Amount(int latencySeconds, String description) {
            this.latency = Duration.ofSeconds(latencySeconds);
            this.description = description;
        }
    }

    /**
     * Execute some work. This method simulates work but really just sleeps for a while.
     */
    CompletableFuture<String> doWork(Amount amount) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(amount.latency);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            out.println(amount.description);
            return amount.description;
        });
    }
}

/**
 * Executes a bunch of simulated work.
 * <p>
 * It is configurable to execute the work either synchronously (one after the other) or concurrently.
 */
class App {

    private final Worker worker = new Worker();

    private final boolean concurrent;

    public App(boolean concurrent) {
        this.concurrent = concurrent;
    }

    void execute() {
        var start = Instant.now();

        var m1Completable = kickOffWork(Worker.Amount.SMALL);
        var m2Completable = kickOffWork(Worker.Amount.MEDIUM);
        var m3Completable = kickOffWork(Worker.Amount.LARGE);

        m1Completable.join();
        m2Completable.join();
        m3Completable.join();
        var finish = Instant.now();
        var duration = Duration.between(start, finish);
        out.printf("Finished. Execution time: %s\n", duration);
    }

    /**
     * Kick off work on the {@link Worker}. If the {@link #concurrent} flag is set, then the work will just be kicked
     * off and the method will return immediately so that more work can be kicked off right away. Otherwise, the method
     * will block until the work is complete.
     */
    private CompletableFuture<String> kickOffWork(Worker.Amount amount) {
        var future = worker.doWork(amount);
        if (concurrent) return future;

        future.join();
        return future;
    }
}
