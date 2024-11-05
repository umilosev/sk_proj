plugins {
    kotlin("jvm")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":spec"))
    runtimeOnly(project(":excelImpl"))
    runtimeOnly(project(":csvImpl"))
    runtimeOnly(project(":pdfImpl"))
    runtimeOnly(project(":textImpl"))
    implementation("org.apache.logging.log4j:log4j-core:2.24.1")
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}