package bilboka.plugin

import org.gradle.api.Project

internal fun Project.configurePlugins() {
    println("Enabling gradle maven publish plugin in project ${project.name}...")
    plugins.apply("org.gradle.maven-publish")

    println("Enabling Kotlin Spring plugin in project ${project.name}...")
    plugins.apply("org.jetbrains.kotlin.plugin.spring")

    println("Enabling Spring Boot plugin in project ${project.name}...")
    plugins.apply("org.springframework.boot")

    println("Enabling Spring Boot Dependency Management in project ${project.name}...")
    plugins.apply("io.spring.dependency-management")
}
