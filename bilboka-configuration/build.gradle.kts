import bilboka.dependencies.Libs

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
	imports { mavenBom(Libs.springbootDependencies) }
}

dependencies {
	implementation(project(":bilboka-core"))
	implementation(project(":bilboka-messenger-integration"))
}

application {
	mainClass.set("bilboka.BilbokaApplicationKt")
}
