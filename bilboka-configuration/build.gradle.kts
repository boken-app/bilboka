import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "2.4.4"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.31"
	kotlin("plugin.spring") version "1.4.31"

	// Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
	// id("org.jetbrains.kotlin.jvm") version "1.4.20"

	// Apply the java-library plugin for API and implementation separation.
	`java-library`

}

group = "ivaralek"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
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
	testImplementation("org.springframework.boot:spring-boot-starter-test")


	implementation(project(":bilboka-core"))
	testImplementation(project(":bilboka-core")) // TODO?


	// Use the Kotlin test library.
	testImplementation("org.jetbrains.kotlin:kotlin-test")

	// Use the Kotlin JUnit integration.
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
	testImplementation("org.jetbrains.kotlin:kotlin-test-annotations-common")
	testImplementation("org.jetbrains.kotlin:kotlin-test-common")
}


//
//
//allprojects {
//
//    dependencyManagement {
//		imports { mavenBom("org.springframework.boot:spring-boot-dependencies:2.4.4") }
//	}
//    apply {
//        plugin("org.springframework.boot")
//        plugin("io.spring.dependency-management")
//    }
//
//
//
//}


tasks.getByName<BootJar>("bootJar") {
	enabled = false
}

tasks.getByName<Test>("test") {
	testLogging {

		showExceptions = true
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
		showCauses = true
		showStackTraces = true

		showStandardStreams = false
	}
}

