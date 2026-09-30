plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "io.github.chandu4221.designode"
version = "0.1.0"

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(project(":domain"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}