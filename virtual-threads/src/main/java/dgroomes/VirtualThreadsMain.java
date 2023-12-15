package dgroomes;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;
import java.util.logging.Logger;

/*
 * See the README for more information.
 */
public class VirtualThreadsMain {
    private static final Logger log = Logger.getAnonymousLogger();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        log.info("Hello! Let's implement something with Virtual Threads (JEP 444).");

        Callable<String> task1 = () -> {
            for (int i = 0; i < 2; i++) {
                log.info("Hello!");
                Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));
            }
            return "task 1";
        };
        Callable<String> task2 = () -> {
            Thread.sleep(Duration.of(500, ChronoUnit.MILLIS));
            for (int i = 0; i < 2; i++) {
                log.info("\t\tHi!");
                Thread.sleep(Duration.of(1, ChronoUnit.SECONDS));
            }
            return "task 2";
        };

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            executor.submit(task1);
            executor.submit(task2);
            executor.shutdown();
            log.info("Wait for the tasks to execute to completion");
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        log.info("Done");
    }
}
