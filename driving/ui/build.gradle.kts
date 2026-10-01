plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

group = "io.github.chandu4221.designode"
version = "0.1.0"

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":catalog"))
    implementation(project(":driven:codegen"))

    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    implementation(compose.materialIconsExtended)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}