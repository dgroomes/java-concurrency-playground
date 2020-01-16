# java-concurrency

Learning and exploring concurrency in Java. Using examples from <https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html>

### `fibers/` (should be renamed _loom_. _Fibers_ are out; _Virtual Threads_ are in since 11/2019)

Trying out _Project_ Loom and the new programming paradigm for Java using *user-mode threads*.

REQUIRES an experimental JDK built from the OpenJDK Project Loom branch <https://wiki.openjdk.java.net/display/loom/Main>.

Run it with `java fibers/src/dgroomes/FibersMain.java`

### `interrupts/`

Learning about interrupts <https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html>

Run with `java interrupts/src/dgroomes/InterruptsMain.java`