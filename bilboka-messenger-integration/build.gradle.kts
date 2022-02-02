import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("jvm")
//    application

    id("bilboka.plugin")

    id("org.springframework.boot") apply false // TODO vet ikke om dette er nødvendig
}

group = "ivaralek"
version = "0.0.1-SNAPSHOT"


// TODO rydde opp her bl.a.
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.github.jkcclemens:khttp:-SNAPSHOT")

    runtimeOnly("com.h2database:h2") // ?

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.mockito:mockito-core:3.3.3")
    testImplementation("io.mockk:mockk:1.10.6") // Feilet ved nyere versjon
    testImplementation("com.ninja-squad:springmockk:3.0.1")
    testImplementation("com.squareup.okhttp3:okhttp:4.0.1")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.0.1")
}

//application {
//    mainClass.set("bilboka.BilbokaApplicationKt")
//}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
