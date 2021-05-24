plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")

    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.flywaydb:flyway-core")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
