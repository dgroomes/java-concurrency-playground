package dgroomes;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * Showcasing all of:
 * 1) the effect of concurrent programming
 * 2) the programming model of CompletableFuture
 * 3) the built-in Java HTTP client that was introduced in Java 11 https://openjdk.java.net/groups/net/httpclient/intro.html
 */
public class CompletableFuturesMain {

    public static final String MOCK_API_ORIGIN = "http://localhost:8070/";
    public static final HttpLogLevel HTTP_CLIENT_LOG_LEVEL = HttpLogLevel.ERROR;

    public static void main(String[] args) {
        setHttpClientLogLevel(HTTP_CLIENT_LOG_LEVEL);
        HttpClient client = HttpClient.newHttpClient();
        System.out.println("Executing the program with an implementation that uses synchronously executed HTTP " +
                "requests. In other words, *no concurrency*.");
        new App(client, false).execute();
        System.out.println("Executing the program with an implementation that uses concurrent HTTP requests. Notice how " +
                "it executes quicker than before.");
        new App(client, true).execute();
    }

    enum HttpLogLevel {ERROR, INFO, DEBUG, TRACE}

    /**
     * Enable logs in the built-in Java HTTP client.
     */
    private static void setHttpClientLogLevel(HttpLogLevel level) {
        String value = switch (level) {
            case ERROR -> "errors";
            case INFO -> "errors,requests";
            case DEBUG -> "errors,requests,headers,content";
            case TRACE -> "errors,requests,headers,content,frames[:control:data:window:all],ssl,trace,channel";
        };

        System.setProperty("jdk.httpclient.HttpClient.log", value);
    }

    /**
     * Executes some HTTP requests to the Mock API.
     * <p>
     * It is is configurable to execute the API requests either synchronously (one after the other) or concurrently
     */
    static class App {

        private final HttpClient client;

        private final boolean concurrent;

        public App(HttpClient client, boolean concurrent) {
            this.client = client;
            this.concurrent = concurrent;
        }

        private void execute() {
            var synchronous = !concurrent;

            var start = Instant.now();

            var m1Completable = request("message?name=A&delay=1", synchronous);
            var m2Completable = request("message?name=B&delay=2", synchronous);
            var m3Completable = request("message?name=C&delay=4", synchronous);

            m1Completable.join();
            m2Completable.join();
            m3Completable.join();
            var finish = Instant.now();
            var duration = Duration.between(start, finish);
            System.out.printf("Finished. Execution time: %s\n", duration);
        }

        /**
         * Build an HttpRequest to the mock API
         *
         * @param urlPath     the URL path
         * @param synchronous if true then then execute the request immediately (i.e. synchronously) else execute it asynchronously.
         * @return a CompletableFuture that represents the request/response. The completion result will be a String
         * containing the response body.
         */
        private CompletableFuture<String> request(String urlPath, boolean synchronous) {
            var builder = HttpRequest.newBuilder().uri(URI.create(MOCK_API_ORIGIN).resolve(urlPath));
            var future = client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body);
            future.thenAccept(body -> System.out.printf("Got response: %s\n", body));
            if (synchronous) {
                future.join();
            }
            return future;
        }
    }
}
