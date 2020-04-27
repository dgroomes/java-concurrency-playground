package dgroomes;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Run a WireMock server
 */
public class MockServer {

    private static final Logger log = LoggerFactory.getLogger(MockServer.class);
    private static final int PORT_NUMBER = 8070;

    public static void main(String[] args) throws IOException {
        var options = new WireMockConfiguration()
                .port(PORT_NUMBER)
                .extensions(new ResponseTemplateTransformer(false));
        var wireMockServer = new WireMockServer(options);
        wireMockServer.start();
        WireMockUtil.loadStubsFromDirectory(wireMockServer, "wiremock-stubs");
        log.info("Server started and stubs loaded. Ready to accept traffic! Try GET http://localhost:{}/message", PORT_NUMBER);
    }
}
