package bilboka.plugin

import bilboka.dependencies.Libs
import bilboka.dependencies.Versions
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect"
val springBootStarter = "org.springframework.boot:spring-boot-starter"
val springBootStarterTest = "org.springframework.boot:spring-boot-starter-test"

val jUnitApi = "org.junit.jupiter:junit-jupiter-api:${Versions.jUnitJupiter}"
val jUnitEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.jUnitJupiter}"
val jUnitParams = "org.junit.jupiter:junit-jupiter-params:${Versions.jUnitJupiter}"
val jUnitPlatformCommons = "org.junit.platform:junit-platform-commons:${Versions.jUnitPlatform}"
val jUnitPlatformLauncher = "org.junit.platform:junit-platform-launcher:${Versions.jUnitPlatform}"

val kotlinStandardLibrary = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"

val slf4jApi = "org.slf4j:slf4j-api:${Versions.slf4j}"
val logbackClassic = "ch.qos.logback:logback-classic:${Versions.logback}"
val locbackCore = "ch.qos.logback:logback-core:${Versions.logback}"

internal fun Project.configureDependencies() = dependencies {
    add("implementation", springBootStarter)
    add("implementation", kotlinStandardLibrary)
    add("implementation", kotlinReflect)

    add("implementation", Libs.springbootGradle)
    add("implementation", Libs.springbootDependencies)

    add("api", platform(Libs.springbootDependencies))

    add("implementation", slf4jApi)
    add("implementation", logbackClassic)
    add("implementation", locbackCore)

    add("testImplementation", springBootStarterTest)
    add("testImplementation", jUnitApi)
    add("testImplementation", jUnitParams)
    add("testRuntimeOnly", jUnitEngine)
    add("testImplementation", jUnitPlatformCommons)
    add("testImplementation", jUnitPlatformLauncher)
}
