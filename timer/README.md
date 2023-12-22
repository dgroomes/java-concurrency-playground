# timer

This project shows how the `java.util.Timer` Java class works.

---

See the JavaDoc for `Timer` at <https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/Timer.html>.

Warning: while `Timer` is not deprecated it is mostly obsoleted by `java.util.concurrent.ScheduledThreadPoolExecutor`.
Here is an excerpt from the `Timer` JavaDoc about this fact:

> Java 5.0 introduced the java.util.concurrent package and one of the concurrency utilities therein is the ScheduledThreadPoolExecutor which is a thread pool for repeatedly executing tasks at a given rate or delay. It is effectively a more versatile replacement for the Timer/TimerTask combination, as it allows multiple service threads, accepts various time units, and doesn't require subclassing TimerTask (just implement Runnable). Configuring ScheduledThreadPoolExecutor with one thread makes it equivalent to Timer.

Consider that when implementing timing-related code.


## Instructions

Follow these instructions to run the programs:

1. Use Java 21
2. Try a combination of these invocations of the programs:
   * ```shell
     java src/dgroomes/TimerDaemonMain.java --non-daemon-thread
     ```
   * ```shell
     java src/dgroomes/TimerDaemonMain.java --daemon-thread
     ```
   * ```shell
     java src/dgroomes/TimerRepeatingTasksMain.java
     ```
   * (debug mode)
     ```shell
     java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 src/main/java/dgroomes/TimerRepeatingTasksMain.java
     ```
   * (record a Java Flight Recording (JFR))
     ```shell
     java -XX:+UnlockDiagnosticVMOptions -XX:+DebugNonSafepoints -XX:StartFlightRecording,dumponexit=true src/main/java/dgroomes/TimerRepeatingTasksMain.java
     ```
   * ```shell
     java src/dgroomes/TimerMultipleTasksMain.java
     ```
