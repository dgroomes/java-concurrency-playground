# timer

This project shows how the `java.util.Timer` Java class works.

---

See the JavaDoc for `Timer` at <https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/Timer.html>.

Warning: while `Timer` is not deprecated it is mostly obsoleted by the `java.util.concurrent.ScheduledThreadPoolExecutor`.
Here is an excerpt from the `Timer` JavaDoc about this fact:

> Java 5.0 introduced the java.util.concurrent package and one of the concurrency utilities therein is the ScheduledThreadPoolExecutor which is a thread pool for repeatedly executing tasks at a given rate or delay. It is effectively a more versatile replacement for the Timer/TimerTask combination, as it allows multiple service threads, accepts various time units, and doesn't require subclassing TimerTask (just implement Runnable). Configuring ScheduledThreadPoolExecutor with one thread makes it equivalent to Timer.

Consider that when implementing timing-related code. 

### Instructions

1. Use Java 14
1. From the repository root, run the program with: `java --enable-preview --source 14 timer/src/main/java/dgroomes/TimerMain.java`
