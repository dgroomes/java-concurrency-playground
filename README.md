# java-concurrency-playground

Learning and exploring concurrency in Java.

## Sub-projects

Concurrency concepts (the "what") and constructs (APIs and language features) are illustrated in standalone 
sub-projects. 

### `loom/`

Learning about _Project Loom_ and the new "virtual" threads <https://wiki.openjdk.java.net/display/loom/Main>.

See [loom/README.md](loom/README.md).

### `interrupts/`

Learning about interrupts <https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html>

See [interrupts/README.md](interrupts/README.md).

###  `mock-api/`

Run an instance of the excellent HTTP mock server WireMock <http://wiremock.org/docs/>. This sub-project only serves as
a vehicle to exercise the concurrency constructs of the other sub-projects.

See [mock-api/README.md](mock-api/README.md).   

### `completable-future/`

Learning about `CompletableFuture` <https://docs.oracle.com/en/java/javase/14/docs/api/java.base/java/util/concurrent/CompletableFuture.html>
and related APIs.

See [completable-future/README.md](completable-future/README.md).