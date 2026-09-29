plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "io.github.chandu4221.designode"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(25)
}

tasks.test {
    useJUnitPlatform()
}