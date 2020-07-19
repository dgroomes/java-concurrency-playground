package dgroomes;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Illustrating how `java.util.Timer` works with multiple tasks.
 * <p>
 * See https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/Timer.html
 */
public class TimerMultipleTasksMain {

    private static final Logger log = Logger.getAnonymousLogger();

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        log.log(Level.INFO, "Hello! Let's learn about the java.util.Timer class with multiple tasks. NOT YET IMPLEMENTED");
    }
}
