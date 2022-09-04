package dgroomes;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Please see the README for more information.
 * <p>
 * I've learned that the Java shutdown hook will run for SIGINT (kill -2) and SIGTERM (kill -15). I can't find this documented,
 * but I know by practice.
 */
public class SignalsRunner {

  public static void main(String[] args) {

    var scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    var workerExecutor = Executors.newFixedThreadPool(10);

    var unitOfWorkId = new AtomicInteger(0);
    Runnable unitOfWork = () -> {
      int id = unitOfWorkId.incrementAndGet();
      log("Doing unit-of-work '%d'. This will take a little while...".formatted(id));
      try {
        // Well, there's no work actually. We're just simulating work using a `Thread.sleep` statement.
        Thread.sleep(5_000);
      } catch (InterruptedException e) {
        log("The thread for unit-of-work '%d' was interrupted.".formatted(id));
      }
      log("Unit-of-work '%d' is complete.".formatted(id));
    };

    // Schedule units-of-work every second. This will accumulate actively running units-of-work up to a point.
    scheduledExecutor.scheduleAtFixedRate(() -> workerExecutor.submit(unitOfWork), 0, 1, TimeUnit.SECONDS);

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log("%n%n***Shutdown hook triggered***%n".formatted());
      try {
        log("Shutting down the executor services gracefully by waiting for their completion.");
        scheduledExecutor.shutdown();
        workerExecutor.shutdown();
        var success = scheduledExecutor.awaitTermination(100, TimeUnit.MILLISECONDS);
        success = success && workerExecutor.awaitTermination(6, TimeUnit.SECONDS);
        if (success) {
          log("The executors have completed.");
        } else {
          log("The executors did not complete in the allotted wait time.");
        }
      } catch (InterruptedException e) {
        log("The thread was interrupted during the shutdown hook.");
        e.printStackTrace();
      }
    }));

    log("The `main` method has completed, but the program will continue running because of the scheduled executor.");
  }

  static DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SS");

  /**
   * Note about shutdown hooks and logging:
   * <p>
   * Notice that this program is not using JUL (java.util.logging) and is instead using System.out. I've had trouble
   * using logging frameworks in lines of code that are very near the termination of the program. I think there is a
   * timing problem where a log statement (like `log.info("This is the end of the program")`) does not actually get
   * flushed to standard out before the program terminates. Logging frameworks will buffer the writing of log content
   * for efficiency. If the program terminates before logs are flushed, then you won't see log lines that you might
   * have expected.
   */
  private static void log(String message) {
    var now = TIME_FORMAT.format(LocalTime.now());
    var thread = Thread.currentThread().getName();
    System.out.printf("[%s - %s] %s%n", now, thread, message);
  }
}
