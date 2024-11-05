plugins {
    kotlin("jvm")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.poi:poi:5.2.3") // For .xls format
    implementation("org.apache.poi:poi-ooxml:5.2.3") // For .xlsx format
    implementation(project(":spec"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
}