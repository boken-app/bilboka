import org.gradle.ide.visualstudio.tasks.internal.RelativeFileNameTransformer.from

plugins {
    `kotlin-dsl`
    application
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

application {
    mainClass.set("ivaralek.bilboka.BilbokaApplication")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("gradle-plugin"))
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("org.jetbrains.kotlin:kotlin-allopen")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.5.2")

}

//apply(from(file("gradle/heroku/clean.gradle")))

subprojects {
    apply(from(file("$rootProject.projectDir/gradle/heroku/stage.gradle")))
}

tasks.shadowJar {
    isZip64 = true
}

// Alternativt

//tasks.withType<ShadowJar>() {
//    setZip64(true)  // new code
//    manifest {
//        attributes["Main-Class"] = "HelloKt"
//    }
//}