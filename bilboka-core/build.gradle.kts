import bilboka.dependencies.Libs
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")

    id("bilboka.plugin")
}

group = "ivaralek" // TODO bilboka
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports { mavenBom(Libs.springbootDependencies) }
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:${bilboka.dependencies.Versions.exposed}")
    implementation("org.jetbrains.exposed:exposed-dao:${bilboka.dependencies.Versions.exposed}")
    implementation("org.jetbrains.exposed:exposed-jdbc:${bilboka.dependencies.Versions.exposed}")
    implementation("org.jetbrains.exposed:exposed-java-time:${bilboka.dependencies.Versions.exposed}")

    //  implementation("org.springframework.boot:spring-boot-starter-thymeleaf:3.0.4")
    implementation("org.thymeleaf:thymeleaf:3.1.1.RELEASE")
//    implementation("org.thymeleaf:thymeleaf-spring5:3.1.1.RELEASE")
//    implementation("org.thymeleaf.extras:thymeleaf-extras-java8time:3.0.4.RELEASE")
    implementation("org.xhtmlrenderer:flying-saucer-pdf:9.1.22")

    runtimeOnly("com.h2database:h2")

    testImplementation("io.mockk:mockk:1.10.6") // Feilet ved nyere versjon
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
