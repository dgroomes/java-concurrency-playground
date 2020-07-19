package dgroomes;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * REQUIRES an experimental JDK built from the OpenJDK Project Loom branch https://wiki.openjdk.java.net/display/loom/Main
 *
 * Reference my notes for building the JDK on macOS https://gist.github.com/dgroomes/3af073b71c2c34581a155af9daa86564
 *
 * Reference the official Virtual Threads example at https://wiki.openjdk.java.net/display/loom/Getting+started
 */
public class VirtualThreadsMain {

    private static final Logger log = Logger.getAnonymousLogger();

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        log.info("Hello! Can we implement something with Virtual Threads (OpenJDK Project Loom)?");

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

        try (ExecutorService executor = Executors.newVirtualThreadExecutor()) {
            executor.submitTasks(List.of(task1, task2));
            executor.shutdown();
            log.info("Wait for the tasks to execute to completion");
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }

        log.info("Done");
    }
}
