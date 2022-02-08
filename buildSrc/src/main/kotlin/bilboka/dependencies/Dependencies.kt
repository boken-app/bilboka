package bilboka.dependencies

object Versions {
    val kHttp = "-SNAPSHOT"
    val kTor = "1.6.0"
}

object Libs {
    val kHttp = "com.github.jkcclemens:khttp:${Versions.kHttp}"
    val ktorServerNetty = "io.ktor:ktor-server-netty:${Versions.kTor}"
    val ktorHtmlBuilder = "io.ktor:ktor-html-builder:${Versions.kTor}"
}
