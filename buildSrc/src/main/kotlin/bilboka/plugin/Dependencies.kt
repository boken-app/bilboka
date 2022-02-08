package bilboka.plugin

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

object Versions {
    val kotlin = "1.6.10"
    val jUnitJupiter = "5.8.2"
    val jUnitPlatform = "1.8.2"

    val slf4j = "1.7.30"
    val logback = "1.2.3"

    val spring = "2.6.2"
}

val jUnitApi = "org.junit.jupiter:junit-jupiter-api:${Versions.jUnitJupiter}"
val jUnitEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.jUnitJupiter}"
val jUnitParams = "org.junit.jupiter:junit-jupiter-params:${Versions.jUnitJupiter}"
val jUnitPlatformCommons = "org.junit.platform:junit-platform-commons:${Versions.jUnitPlatform}"
val jUnitPlatformLauncher = "org.junit.platform:junit-platform-launcher:${Versions.jUnitPlatform}"

val kotlinStandardLibrary = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"

val springboot = "org.springframework.boot:spring-boot-dependencies:${Versions.spring}"
val springbootGradle = "org.springframework.boot:spring-boot-gradle-plugin:${Versions.spring}"

val slf4jApi = "org.slf4j:slf4j-api:${Versions.slf4j}"
val logbackClassic = "ch.qos.logback:logback-classic:${Versions.logback}"
val locbackCore = "ch.qos.logback:logback-core:${Versions.logback}"

//"compileOnly"("org.slf4j:slf4j-api:1.7.30")
//"compileOnly"("ch.qos.logback:logback-classic:1.2.3")
//"compileOnly"("ch.qos.logback:logback-core:1.2.3")

object Libs {
}

internal fun Project.configureDependencies() = dependencies {
    add("implementation", kotlinStandardLibrary)

    //   add("implementation", springbootGradle)

    add("api", platform(springboot))

    add("implementation", slf4jApi)
    add("implementation", logbackClassic)
    add("implementation", locbackCore)

    add("testImplementation", jUnitApi)
    add("testImplementation", jUnitParams)
    add("testRuntimeOnly", jUnitEngine)
    add("testImplementation", jUnitPlatformCommons)
    add("testImplementation", jUnitPlatformLauncher)
}
