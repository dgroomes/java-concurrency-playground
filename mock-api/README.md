# mock-api

Run an instance of the excellent HTTP mock server WireMock <http://wiremock.org/docs/>.

### Instructions

* Use Java 14
* From the repository root, build and run the server with `./gradlew :mock-api:run`

### Notes

The WireMock server will load mapping files from the `mappings/` directory which contains WireMock mappings files 
defined in JSON.