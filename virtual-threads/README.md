# virtual-threads

A simple demonstration of virtual threads.


## Overview

This project shows a familiar usage of the `Thread`, `ExecutorService` and related classes but uses the new "virtual"
threads instead of traditional operating system threads (now referred to as *platform threads* in Java). Virtual threads
were finalized in Java 21 and are described by [JEP 444](https://openjdk.org/jeps/444).


## Instructions

1. Use Java 21
2. Run the program:
   * ```shell
     java src/main/java/dgroomes/VirtualThreadsMain.java
     ```
   * It will print something like the following.
   * ```text
     $ java src/main/java/dgroomes/VirtualThreadsMain.java
     2023-12-15 01:00:59 INFO dgroomes.VirtualThreadsMain main Hello! Hello! Let's implement something with Virtual Threads (JEP 444).
     2023-12-15 01:00:59 INFO dgroomes.VirtualThreadsMain main Wait for the tasks to execute to completion
     2023-12-15 01:00:59 INFO dgroomes.VirtualThreadsMain lambda$main$0 Hello!
     2023-12-15 01:01:00 INFO dgroomes.VirtualThreadsMain lambda$main$1              Hi!
     2023-12-15 01:01:00 INFO dgroomes.VirtualThreadsMain lambda$main$0 Hello!
     2023-12-15 01:01:01 INFO dgroomes.VirtualThreadsMain lambda$main$1              Hi!
     2023-12-15 01:01:02 INFO dgroomes.VirtualThreadsMain main Done
     ```
