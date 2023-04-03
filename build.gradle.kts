plugins {
    kotlin("jvm")
    kotlin("plugin.spring") version "1.7.10" apply false

    id("org.springframework.boot") version "2.6.3" apply false

    id("com.github.johnrengelman.shadow") version "8.1.0"
}

kotlin {
    jvmToolchain(17)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

allprojects {
    apply(plugin = "java-library")

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
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
    // Heroku
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(from = file("$rootDir/gradle/heroku/stage.gradle"))
}
