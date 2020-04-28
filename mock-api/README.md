# mock-api

Run an instance of the excellent HTTP mock server WireMock <http://wiremock.org/docs/>.

### Instructions

1. Use Java 14
1. From the repository root, build and run the server with `./gradlew :mock-api:run`

### Notes

The WireMock server will enable its 'files' and 'mappings' sources at the 'wiremock/' and 'wiremock/mappings' 
directories respectively. In other words, these directories contain the associated content files and the request mapping
definition JSON files that WireMock will use to define its behavior. Somewhat confusingly, the "mappings" directory is a
child in the "files" directory and the actual supporting files must always be in a child directory called "__files".