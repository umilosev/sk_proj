plugins {
    kotlin("jvm") version "1.9.22"
}

group = "com.example"
version = "1.0-SNAPSHOT"


allprojects{
    repositories{
        mavenCentral()
        mavenLocal()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}