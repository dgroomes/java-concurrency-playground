package dgroomes;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.SingleRootFileSource;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utilities for working with WireMock http://wiremock.org/docs/
 */
public class WireMockUtil {

    private static final Logger log = LoggerFactory.getLogger(WireMockUtil.class);

    /**
     * Read a WireMock stub mapping from a file
     *
     * @param filePath of the JSON file that defines a WireMock stub
     * @return a StubMapping
     */
    public StubMapping readStub(String filePath) throws IOException {
        log.debug("Reading WireMock stub '{}'", filePath);
        var file = new File(filePath);
        if (!file.exists()) {
            var message = String.format("Cannot read WireMock stub '%s'. This file does not exist.", file.getAbsolutePath());
            throw new IOException(message);
        }
        var path = file.toPath();
        var bytes = Files.readAllBytes(path);
        var content = new String(bytes, StandardCharsets.UTF_8);
        return StubMapping.buildFrom(content);
    }

    /**
     * Load WireMock stubs from the JSON mapping files in a directory
     *
     * @param directoryPath the path to the directory containing WireMock mapping definition files written in JSON
     */
    public static void loadStubsFromDirectory(WireMockServer wireMockServer, String directoryPath) throws IOException {
        log.debug("Loading WireMock stubs from directory '{}'", directoryPath);
        var path = Paths.get(directoryPath);
        var file = path.toFile();
        if (!file.exists()) {
            var msg = String.format("""
                    Could not load stubs because the directory was not found 
                      Provided path: %s
                      Absolute path: %s
                    """, directoryPath, path);
            throw new IOException(msg);
        }

        var fileSource = new SingleRootFileSource(file);
        var mappingsSource = new JsonFileMappingsSource(fileSource);
        wireMockServer.loadMappingsUsing(mappingsSource);
    }
}
