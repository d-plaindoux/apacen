import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    kotlin("jvm") version "2.1.10"
    kotlin("plugin.serialization") version "2.1.10"
    id("org.openapi.generator") version "7.12.0"
}

group = "apacen"
version = "0.1"

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}

object Versions {
    const val kotest_junit5 = "5.9.1"
}

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
        // or, via Gradle Plugin Portal:
        // url "https://plugins.gradle.org/m2/"
    }
    dependencies {
        classpath("org.openapitools:openapi-generator-gradle-plugin:7.10.0")
    }
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.kotest", "kotest-runner-junit5", Versions.kotest_junit5)
}
