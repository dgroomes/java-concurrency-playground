val wireMockVersion = "2.33.2" // WireMock releases: https://github.com/tomakehurst/wiremock/tags
val slf4jVersion = "1.7.36" // SLF4J releases: http://www.slf4j.org/news.html

dependencies {
    implementation(group = "com.github.tomakehurst", name = "wiremock-jre8", version = wireMockVersion)
    implementation(group = "org.slf4j", name = "slf4j-api", version = slf4jVersion)
    implementation(group = "org.slf4j", name = "slf4j-simple", version = slf4jVersion)
}

application {
    mainClass.set("dgroomes.MockServer")
}
