plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.wiremock)
    implementation(libs.slf4j.api)
    runtimeOnly(libs.slf4j.simple)
}

application {
    mainClass.set("dgroomes.MockServer")
}
