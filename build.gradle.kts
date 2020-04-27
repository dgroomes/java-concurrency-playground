plugins {
    java
}

allprojects {
    // Do we really need to declare this java plugin here *and* at the top of this file?
    apply(plugin = "java")
    apply(plugin = "application")

    repositories {
        jcenter()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_14
        targetCompatibility = JavaVersion.VERSION_14
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

