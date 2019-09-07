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
                log.info("Sleeping for a second");
                Thread.sleep(1000);
                log.info("Done sleeping");
            } catch (InterruptedException e) {
                log.warning("I was interrupted");
            }
        });

        thread.start();
        Thread.sleep(500);
        log.info("Interrupting the thread");
        thread.interrupt();
        log.info("Waiting for the thread to die");
        thread.join();
        log.info("Done.");
    }
}
