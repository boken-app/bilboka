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
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation(project(":bilboka-core"))
	implementation(project(":bilboka-messenger-integration"))

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

application {
	mainClass.set("bilboka.BilbokaApplicationKt")
}
