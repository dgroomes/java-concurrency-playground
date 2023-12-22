# completable-future

This subproject executes HTTP requests to the [Mock API](../mock-api) using asynchronous requests to showcase the power
of `CompletableFuture` and the built-in [Java HTTP client](https://openjdk.java.net/groups/net/httpclient/intro.html) 
that was introduced in Java 11.


## Instructions

Follow these instructions to run the demo program:

1. Use Java 21
2. Start the Mock API
    * Follow the instructions in the README at [../mock-api/](../mock-api/)
3. Run the program:
   * ```shell
     java src/main/java/dgroomes/CompletableFuturesMain.java
     ```
   * You should see output like this:
     ```txt
     Executing the program with an implementation that uses synchronously executed HTTP requests. In other words, *no concurrency*.
     Got response: Hello A! (delayed by 1 seconds)
     Got response: Hello B! (delayed by 2 seconds)
     Got response: Hello C! (delayed by 4 seconds)
     Finished. Execution time: PT7.185646S
     Executing the program with an implementation that uses concurrent HTTP requests. Notice how it executes quicker than before.
     Got response: Hello A! (delayed by 1 seconds)
     Got response: Hello B! (delayed by 2 seconds)
     Got response: Hello C! (delayed by 4 seconds)
     Finished. Execution time: PT4.032173S
     ```


## Reference 

* [*CompletableFuture for Asynchronous Programming in Java 8*](https://community.oracle.com/docs/DOC-995305)
