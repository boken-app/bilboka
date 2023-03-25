package bilboka.dependencies

object Versions {
    const val spring = "2.6.3"

    const val kotlin = "1.8.10"
    const val jUnitJupiter = "5.9.2"
    const val jUnitPlatform = "1.9.2"

    const val slf4j = "2.0.7"
    const val logback = "1.3.6"

    const val exposed = "0.41.1"
    const val okHttp = "4.10.0"

    const val kTor = "1.6.0"
}

object Libs {
    const val springbootDependencies = "org.springframework.boot:spring-boot-dependencies:${Versions.spring}"
    const val springbootGradle = "org.springframework.boot:spring-boot-gradle-plugin:${Versions.spring}"
    const val okHttp = "com.squareup.okhttp3:okhttp-bom:${Versions.okHttp}"

    const val ktorServerNetty = "io.ktor:ktor-server-netty:${Versions.kTor}"
    const val ktorHtmlBuilder = "io.ktor:ktor-html-builder:${Versions.kTor}"
}
