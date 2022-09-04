# signals

Learning how signals, like `SIGINT`, are handled by a Java program.


## Description

I want to de-mystify `Ctrl + C`, the `kill` command, and signals like `SIGINT`, `SIGTERM` and `SIGKILL`. I can do that
in the context of a Java program. Likewise, I can learn the APIs offered by the Java standard library that handle these
signals (spoiler alert: the feature set is quite limited).

Signals aren't a concurrency construct, so why is this project in the `java-concurrency-playground`? I think that this
playground repository is appropriate to learn about signals because concurrent code is needed to handle the shutdown
logic. In the example program implemented by this project, I used concurrent code to illustrate a use-case for signals.
Still, I might consider moving this subproject somewhere else.


## Instructions

Follow these instructions to run the demo program and send signals to interactively:

1. Use Java 17
2. Start the program:
   ```shell
   java src/main/java/dgroomes/SignalsRunner.java
   ```
3. Interrupt the program
   * Send an interrupt signal from the commandline with the `Ctrl + C` keyboard shortcut.
   * The `Ctrl + C` keyboard shortcut sends a `SIGINT` signal to the program. This is an operating system feature. `SIGINT`
     is short for "signal" and "interrupt".
4. Watch the program gracefully terminate
   * Altogether, it will look something like this:
     ```text
     $ java src/main/java/dgroomes/SignalsRunner.java
     [12:35:30.64 - main] The `main` method has completed, but the program will continue running because of the scheduled executor.
     [12:35:30.64 - pool-2-thread-1] Doing unit-of-work '1'. This will take a little while...
     [12:35:31.63 - pool-2-thread-2] Doing unit-of-work '2'. This will take a little while...
     [12:35:32.63 - pool-2-thread-3] Doing unit-of-work '3'. This will take a little while...
     [12:35:33.62 - pool-2-thread-4] Doing unit-of-work '4'. This will take a little while...
     [12:35:34.63 - pool-2-thread-5] Doing unit-of-work '5'. This will take a little while...
     [12:35:35.63 - pool-2-thread-6] Doing unit-of-work '6'. This will take a little while...
     [12:35:35.65 - pool-2-thread-1] Unit-of-work '1' is complete.
     [12:35:36.63 - pool-2-thread-7] Doing unit-of-work '7'. This will take a little while...
     [12:35:36.64 - pool-2-thread-2] Unit-of-work '2' is complete.
     ^C[12:35:37.08 - Thread-0]
     
     ***Shutdown hook triggered***
     
     [12:35:37.08 - Thread-0] Shutting down the executor services gracefully by waiting for their completion.
     [12:35:37.63 - pool-2-thread-3] Unit-of-work '3' is complete.
     [12:35:38.63 - pool-2-thread-4] Unit-of-work '4' is complete.
     [12:35:39.63 - pool-2-thread-5] Unit-of-work '5' is complete.
     [12:35:40.63 - pool-2-thread-6] Unit-of-work '6' is complete.
     [12:35:41.63 - pool-2-thread-7] Unit-of-work '7' is complete.
     [12:35:41.63 - Thread-0] The executors have completed.
     ```
   * Notice how the units-of-work were allowed to complete. They were not forcefully terminated.
5. Try the `kill` command
   * Try running the program and sending an interrupt signal using `kill -2 <pid>` instead of using `Ctrl + C`. This will
     have the same effect, because `2` represents the `SIGINT` signal. Check the manual pages to learn more about the `kill`
     command (`man kill`). Also, find the process ID (referred to as `<pid>`) of the Java program using the `jps`
     command which is distributed with the JDK.
   * Now try `kill -9 <pid>`. It will send a `SIGKILL` signal which forcefully terminates the program which means the
     units-of-work will not be allowed to complete. (How does `SIGKILL` work? I don't think it actually sends a signal
     to the Java process, I think macOS just kills the process, right?).


## Reference

* [Wikipedia article: *Signals (IPC)*](https://en.wikipedia.org/wiki/Signal_(IPC))
    * > Signals are standardized messages sent to a running program to trigger specific behavior, such as quitting or error handling.
    * We almost always use the keyboard shortcut `Ctrl + C` to kill the currently running process in the shell (or does the
      terminal emulator do it?). It sends a `SIGINT` signal to the process (like a Java process).
