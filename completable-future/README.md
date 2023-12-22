# completable-future

Simulate the execution of long-running work and show the power of `CompletableFuture` to execute that work concurrently.


## Instructions

Follow these instructions to run the demo program:

1. Use Java 21
2. Run the program:
   * ```shell
     java src/dgroomes/CompletableFuturesMain.java
     ```
   * You should see output like this:
     ```txt
     Executing the program with an implementation that uses synchronously executed simulated work. In other words, *no concurrency*.
     Performed a small amount of work (1 second)
     Performed a medium amount of work (2 seconds)
     Performed a large amount of work (4 seconds)
     Finished. Execution time: PT7.015796S
     Executing the program with an implementation that kicks off simulated work concurrently. Notice how it executes quicker than before.
     Performed a small amount of work (1 second)
     Performed a medium amount of work (2 seconds)
     Performed a large amount of work (4 seconds)
     Finished. Execution time: PT4.005475S
     ```


## Reference 

* [*CompletableFuture for Asynchronous Programming in Java 8*](https://community.oracle.com/docs/DOC-995305)
