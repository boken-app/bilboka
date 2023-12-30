import bilboka.dependencies.Libs

plugins {
	kotlin("jvm")
	application

	id("bilboka.plugin")
//	id("org.flywaydb.flyway") version "9.8.1"
}

group = "bilboka"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencyManagement {
	imports { mavenBom(Libs.springbootDependencies) }
}

dependencies {
	implementation(project(":bilboka-messenger-integration"))
	implementation(project(":bilboka-autosys-integration"))
	implementation(project(":bilboka-web"))

	implementation("org.postgresql:postgresql:42.5.4")
	implementation("org.flywaydb:flyway-core")
}

application {
	mainClass.set("bilboka.BilbokaApplicationKt")
}
