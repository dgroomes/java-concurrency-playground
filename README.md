# java-concurrency-playground

📚 Learning and exploring concurrency in Java.


## Standalone subprojects

This repository illustrates different concepts, patterns and examples via standalone subprojects. Each subproject is
completely independent of the others and do not depend on the root project. This _standalone subproject constraint_
forces the subprojects to be complete and maximizes the reader's chances of successfully running, understanding, and
re-using the code.

The subprojects include:


### `virtual-threads/`

A simple demonstration of virtual threads. 

See the README in [virtual-threads/](virtual-threads/).


### `interrupts/`

Learning about interrupts <https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html>.

See the README in [interrupts/](interrupts/).


### `completable-future/`

Simulate the execution of long-running work and show the power of `CompletableFuture` to execute that work concurrently.

See the README in [completable-future/](completable-future/).


### `timer/`

Learning about `java.util.Timer` <https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/Timer.html>.
and related APIs.

See the README in [timer/](timer/).


### `signals/`

Learning how signals, like `SIGINT`, are handled by a Java program.

See the README in [signals/](signals/).


## Wish List

General clean-ups, TODOs and things I wish to implement for this project:

* [x] DONE Rename `loom` to `virtual-threads`
* [x] DONE Configure `loom/virtual-threads` as a Gradle subproject
* [x] DONE Upgrade to Gradle 8.5
* [x] DONE Upgrade to Java 21
* [x] DONE Unnest the `src/main/java` dirs in the non-Gradle native projects. We're only using Gradle as a way for the IDE
  to understand the project structure, but I don't want to pay the price of having to have a `src/main/java` dir in these
  projects.
* [x] DONE Move `mock-api` to my `wiremock-playground` repo. I originally included it here as a way to explore completable futures
  with a workload I was familiar with in real work, but I can de-scope it now and instead create an in-process mock API
  using timers or something. Or better yet sleeps and virtual threads.
