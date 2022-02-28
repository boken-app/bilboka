import bilboka.dependencies.Libs
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")

    id("bilboka.plugin")
}

group = "ivaralek"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation(Libs.kHttp)

    testImplementation("org.mockito:mockito-core:3.3.3")
    testImplementation("io.mockk:mockk:1.10.6") // Feilet ved nyere versjon
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("com.squareup.okhttp3:okhttp:4.0.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.0.1")
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
