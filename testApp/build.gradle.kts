plugins {
    kotlin("jvm")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    runtimeOnly(project(":excelImpl"))
    runtimeOnly(project(":csvImpl"))
    runtimeOnly(project(":pdfImpl"))
    runtimeOnly(project(":textImpl"))
    implementation(project(":spec"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}