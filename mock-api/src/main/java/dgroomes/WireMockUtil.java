package dgroomes;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.common.SingleRootFileSource;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.standalone.JsonFileMappingsSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Utilities for working with WireMock http://wiremock.org/docs/
 */
public class WireMockUtil {

    private static final Logger log = LoggerFactory.getLogger(WireMockUtil.class);

    /**
     * Enable the "files" and "mappings" sources that WireMock will use for its associated files and mappings
     * definitions
     *
     * @param options               the subject WireMockConfiguration
     * @param filesDirectoryPath    the path to the directory containing WireMock mapping definition files written in JSON
     * @param mappingsDirectoryPath the path to the directory containing WireMock mapping definition files written in JSON
     */
    public static void enableFilesAndMappingsDirs(WireMockConfiguration options, String filesDirectoryPath, String mappingsDirectoryPath) throws IOException {
        log.debug("Enabling the files and mappings directories with '{}' and '{}' respectively", filesDirectoryPath,
                mappingsDirectoryPath);
        var filesSource = fileSource(filesDirectoryPath, "files");
        options.fileSource(filesSource);
        var mappingsSource = fileSource(mappingsDirectoryPath, "mappings");
        options.mappingSource(new JsonFileMappingsSource(mappingsSource));
    }

    /**
     * Verify the directory exists at the path provided and return a wrapping FileSource object
     *
     * @param dirPath the directory to validate exists
     * @param type    either "mappings" or "files"
     * @return a FileSource that wraps the provided directory
     */
    private static FileSource fileSource(String dirPath, String type) throws IOException {
        var path = Paths.get(dirPath);
        var file = path.toFile();
        if (!file.exists()) {
            var msg = String.format("""
                    Can not enable the %s directory because the directory was not found 
                      Provided path: %s
                      Absolute path: %s
                    """, type, dirPath, path.toAbsolutePath());
            throw new IOException(msg);
        }
        return new SingleRootFileSource(file);
    }
}
