plugins {
	kotlin("jvm")
	application

	id("bilboka.plugin")

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

//tasks.getByName<BootJar>("bootJar") {
//	enabled = false
//}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

application {
	mainClass.set("bilboka.BilbokaApplicationKt")
}

// Ser ut som det holder med den over.
//tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {
// //   setZip64(true)  // new code
//    manifest {
//        attributes["Main-Class"] = "bilboka.BilbokaApplicationKt"
//    }
//}