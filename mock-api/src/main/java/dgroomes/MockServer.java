package dgroomes;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Run a WireMock server
 */
public class MockServer {

    private static final Logger log = LoggerFactory.getLogger(MockServer.class);
    private static final int PORT_NUMBER = 8070;
    public static final String WIREMOCK_FILES_DIRECTORY = "wiremock";
    public static final String WIREMOCK_MAPPINGS_DIRECTORY = "wiremock/mappings";

    public static void main(String[] args) throws IOException {
        var options = new WireMockConfiguration().port(PORT_NUMBER);
        WireMockUtil.enableFilesAndMappingsDirs(options, WIREMOCK_FILES_DIRECTORY, WIREMOCK_MAPPINGS_DIRECTORY);
        var wireMockServer = new WireMockServer(options);
        wireMockServer.start();
        log.info("Server started and 'files and mappings' directories enabled. Ready to accept traffic! Try GET " +
                "http://localhost:{}/message", PORT_NUMBER);
    }
}
