# java-concurrency-playground

Learning and exploring concurrency in Java. Using examples from <https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html>

## Sub-projects

### `loom/`

Trying out _Project_ Loom and the new programming paradigm for Java using *user-mode threads*.

REQUIRES an experimental JDK built from the OpenJDK Project Loom branch <https://wiki.openjdk.java.net/display/loom/Main>.

Run it with `java loom/src/dgroomes/VirtualThreadsMain.java`

### `interrupts/`

Learning about interrupts <https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html>

Run with `java interrupts/src/dgroomes/InterruptsMain.java`

###  `mock-api/`

`mock-api` runs an instance of the excellent HTTP mock server WireMock <http://wiremock.org/docs/>. This project only 
serves as a vehicle to exercise the concurrency constructs of the other projects.

See [mock-api/README.md](mock-api/README.md).   

### `completable-future/`

Learning and experimenting with the `CompletableFuture` and related APIs.

See [completable-future/README.md](completable-future/README.md).

Reference: 

* _CompletableFuture for Asynchronous Programming in Java 8_ <https://community.oracle.com/docs/DOC-995305>