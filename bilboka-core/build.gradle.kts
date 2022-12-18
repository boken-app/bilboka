import bilboka.dependencies.Libs
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	kotlin("jvm")
	kotlin("plugin.jpa")

	id("bilboka.plugin")
}

group = "ivaralek"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencyManagement {
	imports { mavenBom(Libs.springbootDependencies) }
}

dependencies {
//	implementation("org.flywaydb:flyway-core")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("org.postgresql:postgresql")

	dependencies {
		implementation("org.jetbrains.exposed:exposed-core:${bilboka.dependencies.Versions.exposed}")
		implementation("org.jetbrains.exposed:exposed-dao:${bilboka.dependencies.Versions.exposed}")
		implementation("org.jetbrains.exposed:exposed-jdbc:${bilboka.dependencies.Versions.exposed}")
		implementation("org.jetbrains.exposed:exposed-java-time:${bilboka.dependencies.Versions.exposed}")
	}

	implementation("org.postgresql:postgresql:42.3.1") // TODO Database burde automatisk funke med Heroku

	runtimeOnly("com.h2database:h2")
}

tasks.getByName<BootJar>("bootJar") {
	enabled = false
}

tasks.getByName<Jar>("jar") {
	enabled = true
}
