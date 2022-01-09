plugins {
    id("bilboka.java-conventions")

    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback:logback-core:1.2.3")
}

tasks.compileKotlin {
    println("Configuring KotlinCompile  $name in project ${project.name}...")
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        //   jdkHome = javaToolchains.compilerFor(java.toolchain).get().metadata.installationPath.asFile.absolutePath
        jvmTarget = "11"
        //   languageVersion = "1.5"
        //   apiVersion = "1.5"
    }
}


//
//// FIX KAPT incapacity to find the right variant
//// https://youtrack.jetbrains.com/issue/KT-31641
//val usage = Attribute.of("org.gradle.usage", Usage::class.java)
//
//dependencies {
//    attributesSchema {
//        attribute(usage)
//    }
//
//    implementation(kotlin("stdlib"))
//    implementation(kotlin("reflect"))
//
//    compileOnly(kotlin("stdlib-jdk8"))
//}
//
//
//configurations.all {
//    afterEvaluate {
//        if (isCanBeResolved && !attributes.contains(usage)) {
//            attributes {
//                attribute(usage, project.objects.named(Usage::class.java, "java-runtime"))
//            }
//        }
//    }
//}
//// END FIX KAPT incapacity to find the right variant

//
//repositories {
//    // Fix for https://github.com/detekt/detekt/issues/3712
//    // TODO: https://github.com/mrclrchtr/gradle-kotlin-spring/issues/9 Remove it when the issue was closed
//    maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
//}
