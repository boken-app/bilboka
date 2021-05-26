import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("bilboka.spring-conventions")
	id("bilboka.kotlin-conventions")
}

group = "ivaralek"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencyManagement {
	imports { mavenBom("org.springframework.boot:spring-boot-dependencies:2.4.4") }
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.flywaydb:flyway-core")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	runtimeOnly("com.h2database:h2")

	implementation(project(":bilboka-core"))

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.getByName<BootJar>("bootJar") {
	enabled = false
}

