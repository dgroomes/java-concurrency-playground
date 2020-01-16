package dgroomes;

import java.util.logging.Logger;

/**
 * REQUIRES an experimental JDK built from the OpenJDK Project Loom branch https://wiki.openjdk.java.net/display/loom/Main
 */
public class FibersMain {

    private static Logger log = Logger.getAnonymousLogger();

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        log.info("Hello. Can we implement something with Fibers (OpenJDK Project Loom)?");

        var fiberScope = FiberScope.open();
        log.info("Scheduling a task as a Fiber");
        var fiber = fiberScope.schedule(() -> {
            log.info("Hello from inside a Fiber");
        });
        log.info("Task was scheduled");

        log.info("Wait for the Fiber to terminate");
        fiber.join();
        log.info("Done");
    }
}
