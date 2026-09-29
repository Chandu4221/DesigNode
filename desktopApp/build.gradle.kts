import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

group = "io.github.chandu4221.designode"
version = "0.1.0"

dependencies {
    // --- Domain (the hexagon core) ---
    implementation(project(":domain"))

    // --- Adapters ---
    implementation(project(":driving:ui"))
    implementation(project(":driven:persistence"))
    implementation(project(":driven:codegen"))

    implementation(compose.desktop.currentOs)
    // -- Coroutines --
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.swing)
    // -- Compose Tooling --
    implementation(libs.compose.uiToolingPreview)
}

kotlin {
    jvmToolchain(25)
}

compose.desktop {
    application {
        mainClass = "io.github.chandu4221.designode.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "DesigNode"
            packageVersion = "0.1.0"
        }
    }
}