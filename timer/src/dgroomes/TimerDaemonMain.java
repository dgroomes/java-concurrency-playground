package dgroomes;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Illustrating how `java.util.Timer` works with regard to the backing thread being a daemon thread or a non-daemon
 * thread.
 * <p>
 * See the JavaDoc {@link Timer}: https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Timer.html
 */
public class TimerDaemonMain {

    private static final Logger log = Logger.getLogger(TimerDaemonMain.class.getName());

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
        log.log(Level.INFO, "Hello! Let's learn about the java.util.Timer class. Specifically let's learn what " +
                "happens when its backing thread is a daemon thread or a non-daemon thread.");

        boolean useDaemonThread;
        if (args.length == 0) {
            log.info("No option provided. Defaulting to --non-daemon-thead");
            useDaemonThread = true;
        } else {
            var option = args[0];
            useDaemonThread = switch (option) {
                case "--daemon-thread" -> true;
                case "--non-daemon-thread" -> false;
                default -> throw new IllegalStateException("Provided option '%s' was not recognized".formatted(option));
            };
        }

        runTaskViaTimer(useDaemonThread);
    }

    /**
     * Run a task via a Timer.
     * <p>
     * The point of this method is to showcase how the Timer class works.
     * <p>
     * An additional subtle point about the Timer class is whether or not its backing thread is a daemon thread or not.
     *
     * @param daemon should the Timer's backing thread of execution be a daemon thread or not?
     */
    private static void runTaskViaTimer(final boolean daemon) {
        log.log(Level.INFO, "Creating a Timer. daemon={0}", daemon);
        var timer = new Timer(daemon);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                log.info("Time is up!");
            }
        }, TimeUnit.SECONDS.toMillis(3));
    }
}
