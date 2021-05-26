plugins {
    id("bilboka.java-conventions")

    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

// FIX KAPT incapacity to find the right variant
// https://youtrack.jetbrains.com/issue/KT-31641
val usage = Attribute.of("org.gradle.usage", Usage::class.java)

dependencies {
    attributesSchema {
        attribute(usage)
    }
}

configurations.all {
    afterEvaluate {
        if (isCanBeResolved && !attributes.contains(usage)) {
            attributes {
                attribute(usage, project.objects.named(Usage::class.java, "java-runtime"))
            }
        }
    }
}
// END FIX KAPT incapacity to find the right variant