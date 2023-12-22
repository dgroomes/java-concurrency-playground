package dgroomes;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Illustrating how `java.util.Timer` works with multiple tasks.
 * <p>
 * See the JavaDoc {@link Timer}: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Timer.html
 */
public class TimerMultipleTasksMain {

    private static final Logger log = Logger.getLogger(TimerMultipleTasksMain.class.getName());

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        log.log(Level.INFO, "Hello! Let's learn about the java.util.Timer class with multiple tasks. NOT YET IMPLEMENTED");

        var timer = new Timer(false);

        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                log.info("Task 1");
            }
        };

        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                log.info("Task 2");
                timer.cancel();
                log.info("The timer was cancelled via a call to the 'cancel()' method. This guarantees that this is " +
                        "the final task to execute. The timer's execution thread should terminate gracefully.");
            }
        };

        TimerTask task3 = new TimerTask() {
            @Override
            public void run() {
                log.info("Task 3. I will never get executed if the timer was canceled :(");
            }
        };


        timer.schedule(task1, 1000);
        timer.schedule(task2, 2000);
        timer.schedule(task3, 3000);

        log.info("Scheduled all of the threads, watch what happens next!");
    }
}
