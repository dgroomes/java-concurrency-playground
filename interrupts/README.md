# interrupts

Learning about interrupts <https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html>.


## Instructions

Follow these instructions to run the demo program:

1. Use Java 21
2. Run the program:
   * ```shell
     java src/dgroomes/InterruptsRunner.java
     ```
   * Altogether, it will look like this:
     ```text
     $ java src/dgroomes/InterruptsRunner.java
     2022-09-04 12:31:23 INFO dgroomes.InterruptsRunner lambda$main$0 Hello from a thread!
     2022-09-04 12:31:23 INFO dgroomes.InterruptsRunner lambda$main$0 Sleeping for five seconds
     2022-09-04 12:31:25 INFO dgroomes.InterruptsRunner main Interrupting the thread before it can finish
     2022-09-04 12:31:25 INFO dgroomes.InterruptsRunner main Waiting for the thread to die
     2022-09-04 12:31:25 WARNING dgroomes.InterruptsRunner lambda$main$0 I was interrupted
     2022-09-04 12:31:25 INFO dgroomes.InterruptsRunner main Done.
     ```


## Reference

* [Oracle Java Tutorials: *Interrupts*](https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html)
