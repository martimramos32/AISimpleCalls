plugins {
    kotlin("jvm") version "2.1.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.json:json:20231013")
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("ch.qos.logback:logback-classic:1.5.13")
    
    // Add jcl-over-slf4j to route Jakarta Commons Logging through SLF4J
    implementation("org.slf4j:jcl-over-slf4j:2.0.9")
    // Add jul-to-slf4j to route java.util.logging through SLF4J
    implementation("org.slf4j:jul-to-slf4j:2.0.9")

    // Add Gson support for JSON serialization
    implementation("com.google.code.gson:gson:2.10.1")

}

// Configure logging for Gradle tasks
tasks.withType<JavaExec> {
    // Suppress SLF4J initialization messages
    systemProperty("org.slf4j.simpleLogger.defaultLogLevel", "off")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(23)
}