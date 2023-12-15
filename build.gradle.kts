subprojects {
    // Do we really need to declare this java plugin here *and* at the top of this file?
    apply(plugin = "java")
    apply(plugin = "application")

    repositories {
        mavenCentral()
    }

    tasks {

        withType(Test::class.java) {
            useJUnitPlatform()
        }
    }
}

project(":completable-future") {
    configure<JavaApplication> {
        mainClass.set("dgroomes.CompletableFuturesMain")
    }
}

project(":interrupts") {
    configure<JavaApplication> {
        mainClass.set("dgroomes.InterruptsMain")
    }
}
