val wireMockVersion = "2.26.3"
val slf4jVersion = "1.7.30"

dependencies {
    implementation(group = "com.github.tomakehurst", name = "wiremock-jre8", version = wireMockVersion)
    implementation(group = "org.slf4j", name = "slf4j-api", version = slf4jVersion)
    implementation(group = "org.slf4j", name = "slf4j-simple", version = slf4jVersion)
}

application {
    mainClass.set("dgroomes.MockServer")
}
