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
    kotlin("plugin.spring") version "1.3.50" apply false
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

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
            incremental = false
        }
    }

    // TODO Usikker på om man trenger noe med denne for å kopiere jars til et sted.
//    tasks.withType<Jar> {
//        manifest {
//            attributes["Main-Class"] = "bilboka.BilbokaApplicationKt" // Tror Heroku trenger denne
//        }
//
//        // This line of code recursively collects and copies all of a project"s files
//        // and adds them to the JAR itself. One can extend this task, to skip certain
//        // files or particular types at will
//        from(configurations.compileClasspath.get()
//            .onEach { println("add from dependencies: ${it.name}") }
//            .map { if (it.isDirectory()) it else zipTree(it) })
//        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//
////    val sourcesMain = sourceSets.main.get()
////    sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
////    from(sourcesMain.output)
//    }

    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {
        //   setZip64(true)  // new code
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

    apply {
        plugin("io.spring.dependency-management")
    }


    apply(plugin = "com.github.johnrengelman.shadow")
    apply(from = file("$rootDir/gradle/heroku/stage.gradle"))
}

dependencies {
    implementation(project(":bilboka-configuration"))
    implementation("org.springframework.boot:spring-boot-starter-web")
}


