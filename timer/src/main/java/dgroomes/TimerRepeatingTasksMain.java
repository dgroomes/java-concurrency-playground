package dgroomes;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Illustrating how `java.util.Timer` works with a repeating task. What happens when the program is shutdown? What
 * happens to the currently executing task? When I run this, and do a 'Ctrl + C', I can see my shutdown hook get
 * executed (NOTE: I have to use 'System.out.println' and not the 'java.util.logging' Logger in the shutdown hook
 * because (I think) it is so late in the lifecycle of the app that new log statements to JUL aren't guaranteed to get
 * written (and I couldn't figure out how to flush the logger...).
 * <p>
 * See https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/Timer.html
 */
public class TimerRepeatingTasksMain {

    private static final Logger log = Logger.getLogger(TimerRepeatingTasksMain.class.getName());

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        log.log(Level.INFO, "Hello! Let's learn about the java.util.Timer class and repeating tasks");

        var timer = new Timer(false);

        var iteration = new AtomicInteger(0);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                var iter = iteration.getAndIncrement();
                log.log(Level.INFO, "Hello from timer! Iteration={0}", iter);
                try {
                    log.info("Sleeping...");
                    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
                    System.out.println("Done sleeping");
                } catch (InterruptedException e) {
                    System.err.println("Task was interrupted.");
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.SECONDS.toMillis(1));

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered. Should we stop the timer (via the 'cancel' method)? Or does the " +
                    "runtime do that automatically?");
            System.out.printf("State of the timer: %s%n", timer);
            timer.cancel();
            timer.purge();
        }));
    }
}
