plugins {
    // Apply the application plugin to add support for building a CLI application in Java.
    application
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    implementation("com.mysql:mysql-connector-j:9.5.0")
    implementation("org.apache.commons:commons-compress:1.28.0")
    implementation("org.apache.commons:commons-text:1.10.0")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

application {
    // Define the main class for the application.
    mainClass.set("edu.rit.ibd.a1.LoadBusinesses")
}
