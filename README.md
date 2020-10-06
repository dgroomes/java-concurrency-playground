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

Learning about `CompletableFuture` <https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/concurrent/CompletableFuture.html>
and related APIs.

See [completable-future/README.md](completable-future/README.md).

### `timer/`

Learning about `java.util.Timer` <https://docs.oracle.com/en/java/javase/15/docs/api/java.base/java/util/Timer.html>.
and related APIs.

See [timer/README.md](timer/README.md).

## Help

You will have a hard time setting this project up correctly in your IDE (Intellij for example). Because these projects
use Java language "preview features", you do not get the out-of-the-box "press the green play button" experience you
may be used to in Intellij. You need to go into the Intellij run configuration and add `--enable-preview` to the VM
options, and then try to run it again.

Similarly, the `loom/` sub-project is especially advanced to use because it requires an experimental build of OpenJDK.
So, for the sake of the usability of the whole project, I've commented out the `:loom` sub-project from being imported
in the root Gradle project.
