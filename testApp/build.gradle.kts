plugins {
    kotlin("jvm") version "1.9.22" // Ensure Kotlin plugin is configured
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    testImplementation(kotlin("test"))

    // Runtime-only dependencies for libraries that `testApp` needs
    runtimeOnly(project(":excelImpl"))
    runtimeOnly(project(":csvImpl"))
    runtimeOnly(project(":pdfImpl"))
    runtimeOnly(project(":textImpl"))

    // Implementation dependencies for `testApp`
    implementation(project(":calcImpl"))
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(project(":spec"))
}

application {
    mainClass.set("testApp.TestKt")  // Set the main class for the command-line application
}

tasks.shadowJar {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    mergeServiceFiles()  // Includes meta-inf service files
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks.named<JavaExec>("runShadow") {
    standardInput = System.`in`
}

// Ensure the shadow JAR is built as part of the build process
tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()  // Enables the JUnit Platform for testing
}

kotlin {
    jvmToolchain(11)  // Target JDK 11
}
