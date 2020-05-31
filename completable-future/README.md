# completable-future

This sub-project executes HTTP requests to the [Mock API](../mock-api) using asynchronous requests to showcase the power
of `CompletableFuture` and the built-in [Java HTTP client](https://openjdk.java.net/groups/net/httpclient/intro.html) 
that was introduced in Java 11.

### Instructions

1. Use Java 14
1. Start the Mock API
    * Follow the instructions at [../mock-api/README.md](../mock-api/README.md)
1. From the repository root, run the program with:
   
   `java --enable-preview --source 14 completable-future/src/main/java/dgroomes/CompletableFuturesMain.java`
   
You should see output like this:

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

There is a second program called `ComposingMain` that goes into more depth. Run it with:

`java --enable-preview --source 14 completable-future/src/dgroomes/ComposingMain.java`

### Reference 

* _CompletableFuture for Asynchronous Programming in Java 8_ <https://community.oracle.com/docs/DOC-995305>
