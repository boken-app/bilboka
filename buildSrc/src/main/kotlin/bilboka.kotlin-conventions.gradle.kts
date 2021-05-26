import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("bilboka.java-conventions")

    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}
