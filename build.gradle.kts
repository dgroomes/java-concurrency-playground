subprojects {
    // Do we really need to declare this java plugin here *and* at the top of this file?
    apply(plugin = "java")
    apply(plugin = "application")

    repositories {
        jcenter()
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_15
        targetCompatibility = JavaVersion.VERSION_15
    }

    tasks {
        /**
         * Enable Java language preview features (so we can "records")
         */
        withType(JavaCompile::class.java) {
            options.compilerArgs.addAll(arrayOf("--enable-preview"))
        }

        withType(Test::class.java) {
            jvmArgs = listOf("--enable-preview")
            useJUnitPlatform()
        }

        named<CreateStartScripts>("startScripts") {
            defaultJvmOpts = listOf("--enable-preview")
        }

        named<JavaExec>("run") {
            jvmArgs = listOf("--enable-preview")
        }
    }
}

project(":completable-future") {
    configure<ApplicationPluginConvention> {
        mainClassName = "dgroomes.CompletableFuturesMain"
    }
}

project(":interrupts") {
    configure<ApplicationPluginConvention> {
        mainClassName = "dgroomes.InterruptsMain"
    }
}
