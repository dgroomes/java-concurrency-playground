package dgroomes;

import java.util.logging.Logger;

/**
 * Learning about interrupts https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html
 */
public class InterruptsMain {

    private static Logger log = Logger.getAnonymousLogger();

    public static void main(String[] args) throws InterruptedException {
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
