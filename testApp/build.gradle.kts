plugins {
    kotlin("jvm")
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
    runtimeOnly(project(":excelImpl"))
    runtimeOnly(project(":csvImpl"))
    implementation(project(":calcImpl"))
    runtimeOnly(project(":pdfImpl"))
    runtimeOnly(project(":textImpl"))
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(project(":spec"))
}

application {
    mainClass.set("testApp.TestKt")
}

tasks.shadowJar {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
    mergeServiceFiles() // include meta-inf services files
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}