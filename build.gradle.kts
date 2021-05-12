import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	id("org.springframework.boot") version "2.4.4"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.31"
	kotlin("plugin.spring") version "1.4.31"
}

allprojects {

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


}

subprojects {


	apply {
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
	}

	dependencyManagement {
		imports { mavenBom("org.springframework.boot:spring-boot-dependencies:2.4.4") }
	}

}

tasks.getByName<BootJar>("bootJar") {
	enabled = false
}
