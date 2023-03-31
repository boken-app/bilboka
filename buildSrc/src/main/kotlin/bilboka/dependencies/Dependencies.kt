package bilboka.dependencies

object Versions {
    val spring = "2.6.3"

    val kotlin = "1.7.10"
    val jUnitJupiter = "5.8.2"
    val jUnitPlatform = "1.8.2"

    val slf4j = "1.7.30"
    val logback = "1.2.3"

    val exposed = "0.41.1"
    val okHttp = "4.10.0"

    val kTor = "1.6.0"
}

object Libs {
    val springbootDependencies = "org.springframework.boot:spring-boot-dependencies:${Versions.spring}"
    val springbootGradle = "org.springframework.boot:spring-boot-gradle-plugin:${Versions.spring}"
    val okHttp = "com.squareup.okhttp3:okhttp-bom:${Versions.okHttp}"

    val ktorServerNetty = "io.ktor:ktor-server-netty:${Versions.kTor}"
    val ktorHtmlBuilder = "io.ktor:ktor-html-builder:${Versions.kTor}"
}
