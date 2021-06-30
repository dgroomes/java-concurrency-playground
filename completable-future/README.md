# completable-future

This sub-project executes HTTP requests to the [Mock API](../mock-api) using asynchronous requests to showcase the power
of `CompletableFuture` and the built-in [Java HTTP client](https://openjdk.java.net/groups/net/httpclient/intro.html) 
that was introduced in Java 11.

### Instructions

1. Use Java 16
1. Start the Mock API
    * Follow the instructions in the README at [../mock-api/](../mock-api/)
1. Run the program:
   * NOTE: execute this from the repository root, not the sub-project directory!
   * `java completable-future/src/main/java/dgroomes/CompletableFuturesMain.java`
   * You should see output like this:
     ```
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
1. Alternatively, run the second program called `ComposingMain`:
   * This project goes into more depth. Run it with the following command.
   * `java completable-future/src/main/java/dgroomes/ComposingMain.java`

### Reference 

* [*CompletableFuture for Asynchronous Programming in Java 8*](https://community.oracle.com/docs/DOC-995305)
