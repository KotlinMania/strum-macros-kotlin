pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositories {
        exclusiveContent {
            forRepository { maven { url = uri(providers.gradleProperty("strumMacros.stagingRepo").get()) } }
            filter {
                includeModule("io.github.kotlinmania", "strum-macros-kotlin")
                includeModule("io.github.kotlinmania", "strum-macros-kotlin-android")
            }
        }
        google()
        mavenCentral()
    }
}
rootProject.name = "strum-macros-publication-consumer"
