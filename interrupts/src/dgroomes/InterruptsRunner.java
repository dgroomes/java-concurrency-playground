package dgroomes;

import java.util.logging.Logger;

/**
 * Please see the README for more information.
 */
public class InterruptsRunner {

    private static final Logger log = Logger.getAnonymousLogger();

    public static void main(String[] args) throws InterruptedException {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        var thread = new Thread(() -> {
            log.info("Hello from a thread!");
            try {
                log.info("Sleeping for five seconds");
                Thread.sleep(5000);
                log.info("Done sleeping");
            } catch (InterruptedException e) {
                log.warning("I was interrupted");
            }
        });

        thread.start();
        Thread.sleep(2000);
        log.info("Interrupting the thread before it can finish");
        thread.interrupt();
        log.info("Waiting for the thread to die");
        thread.join();
        log.info("Done.");
    }
}
