package dgroomes;

/**
 * Learning about interrupts https://docs.oracle.com/javase/tutorial/essential/concurrency/interrupt.html
 * <p>
 * What happens to a thread when the Java process is killed (for example via a SIGINT signal)? Notice how I am not
 * using JUL here and instead System.out. I need to factor out any flushing lag (maybe there is no risk, but I am having
 * a hard time figuring this out, so I need to factor this out of the equation).
 */
public class InterruptsSigIntMain {

    public static void main(String[] args) throws InterruptedException {
        var thread = new Thread(() -> {
            System.out.println("Hello from a thread!");
            try {
                System.out.println("Sleeping for five seconds");
                Thread.sleep(5000);
                System.out.println("Done sleeping");
            } catch (InterruptedException e) {
                System.err.println("I was interrupted");
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered.");
//            try {
//                System.out.println("Joining the thread from the shutdown hook");
//                thread.join();
//            } catch (InterruptedException e) {
//                System.err.println("Interrupted while waiting for the thread to finishg");
//                e.printStackTrace();
//            }
        }));

        thread.start();
        thread.join();
        System.out.println("'Goodbye' from the main method");
    }
}
