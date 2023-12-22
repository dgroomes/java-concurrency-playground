package dgroomes;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

/**
 * THIS WAS PARTIALLY IMPLEMENTED but I didn't finish it, and it won't work, especially because I've removed the WireMock
 * integration in this repository. This code needs to be re-written or deleted.
 * <p>
 * Composing instances of CompletableFuture.
 */
public class ComposingMain {

    public static final String MOCK_API_ORIGIN = "http://localhost:8070/";
    public static final HttpLogLevel HTTP_CLIENT_LOG_LEVEL = HttpLogLevel.ERROR;

    public static void main(String[] args) {
        setHttpClientLogLevel(HTTP_CLIENT_LOG_LEVEL);
        HttpClient client = HttpClient.newHttpClient();
        new App(client).execute();
    }

    public enum HttpLogLevel {ERROR, INFO, DEBUG, TRACE}

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
     * Execute a composition of HTTP requests to the Mock API. These requests telescope into each other and have error
     * handling.
     */
    static class App {

        private final HttpClient client;

        public App(HttpClient client) {
            this.client = client;
        }

        private void execute() {
            var start = Instant.now();

            var m1Completable = requestContainingGeography("Minneapolis")
                    .thenApply(geo -> requestContainingGeography(geo).join())
                    .thenApply(geo -> requestContainingGeography(geo).join());
            var m2Completable = requestMessage("A", 1);
            var m3Completable = requestMessage("B", 2);

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
         * @param urlPath the URL path
         * @return a CompletableFuture that represents the request/response. The completion result will be a String
         * containing the response body.
         */
        private CompletableFuture<String> request(String urlPath) {
            var builder = HttpRequest.newBuilder().uri(URI.create(MOCK_API_ORIGIN).resolve(urlPath));
            var future = client.sendAsync(builder.build(), HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body);
            future.thenAccept(body -> System.out.printf("Got response: %s\n", body));
            return future;
        }

        /**
         * Build a request for "/message"
         *
         * @param name  the name
         * @param delay the delay
         */
        private CompletableFuture<String> requestMessage(String name, int delay) {
            var encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
            return request(String.format("/message?name=%s&delay=%s", encodedName, delay));
        }

        /**
         * Build a request for "/containing-geography"
         *
         * @param geography the geography
         */
        private CompletableFuture<String> requestContainingGeography(String geography) {
            var encodedGeo = URLEncoder.encode(geography, StandardCharsets.UTF_8);
            return request(String.format("/containing-geography/%s", encodedGeo));
        }
    }
}
