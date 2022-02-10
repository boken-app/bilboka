buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"

    id("java-library")
    id("org.springframework.boot") version "2.6.3" apply false

    kotlin("jvm")
    kotlin("plugin.spring") version "1.6.10" apply false
}

allprojects {
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
        maven("https://jitpack.io") // khttp
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {
        manifest {
            attributes["Main-Class"] = "bilboka.BilbokaApplicationKt"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // FIX KAPT incapacity to find the right variant
    // https://youtrack.jetbrains.com/issue/KT-31641
    val usage = Attribute.of("org.gradle.usage", Usage::class.java)

    dependencies {
        attributesSchema {
            attribute(usage)
        }
    }

    configurations.all {
        afterEvaluate {
            if (isCanBeResolved) {
                attributes {
                    attribute(usage, project.objects.named(Usage::class.java, "java-runtime"))
                }
            }
        }
    }
    // END FIX KAPT incapacity to find the right variant
}

subprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = "com.github.johnrengelman.shadow") // Trengs for Heroku

    apply(from = file("$rootDir/gradle/heroku/stage.gradle"))
}
