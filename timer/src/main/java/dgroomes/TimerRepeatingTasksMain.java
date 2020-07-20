package dgroomes;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Illustrating how `java.util.Timer` works with a repeating task.
 * <p>
 * See https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/Timer.html
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
            }
        }, 0, TimeUnit.SECONDS.toMillis(1));
    }
}
