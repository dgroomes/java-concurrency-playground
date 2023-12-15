# mock-api

Run an instance of the excellent HTTP mock server WireMock <http://wiremock.org/docs/>.

## Instructions

Follow these instructions to build and run the mock HTTP web server:

1. Use Java 21
2. Build and run the server:
   * ```shell
     ../gradlew :mock-api:run
     ```


## Notes

The WireMock server will enable its *files* and *mappings* sources at the `wiremock/` and `wiremock/mappings` 
directories respectively. In other words, these directories contain the associated content files and the request mapping
definition JSON files that WireMock will use to define its behavior. Somewhat confusingly, the *mappings* directory is a
child of the *files* directory and the actual supporting files must always be in a child directory of the files directory
called `__files`. You will not have understood this sentence. Read the code carefully!
