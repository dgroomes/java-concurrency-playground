# java-concurrency-playground

📚 Learning and exploring concurrency in Java.


## Sub-projects

Concurrency concepts (the "what") and constructs (APIs and language features) are illustrated in standalone 
sub-projects. 


### `loom/`

Learning about _Project Loom_ and the new "virtual" threads <https://wiki.openjdk.java.net/display/loom/Main>.

See the README in [loom/](loom/).


### `interrupts/`

Learning about interrupts <https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html>

See the README in [interrupts/](interrupts/).


###  `mock-api/`

Run an instance of the excellent HTTP mock server WireMock <http://wiremock.org/docs/>. This sub-project only serves as
a vehicle to exercise the concurrency constructs of the other sub-projects.

See the README in [mock-api/](mock-api/).   


### `completable-future/`

Learning about `CompletableFuture` <https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/concurrent/CompletableFuture.html>
and related APIs.

See the README in [completable-future/](completable-future/).


### `timer/`

Learning about `java.util.Timer` <https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/Timer.html>.
and related APIs.

See the README in [timer/](timer/).


## Help

The `loom/` sub-project is especially advanced to use because it requires an experimental build of OpenJDK.
So, for the sake of the usability of the whole project, I've commented out the `:loom` subproject from being imported
in the root Gradle project (`settings.gradle.kts`).
