# java-concurrency-playground

Learning and exploring concurrency in Java. Using examples from <https://docs.oracle.com/javase/tutorial/essential/concurrency/index.html>

### `loom/`

Trying out _Project_ Loom and the new programming paradigm for Java using *user-mode threads*.

REQUIRES an experimental JDK built from the OpenJDK Project Loom branch <https://wiki.openjdk.java.net/display/loom/Main>.

Run it with `java loom/src/dgroomes/VirtualThreadsMain.java`

### `interrupts/`

Learning about interrupts <https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html>

Run with `java interrupts/src/dgroomes/InterruptsMain.java`