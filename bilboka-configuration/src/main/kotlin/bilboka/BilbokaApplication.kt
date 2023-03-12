package bilboka

import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import java.net.URI

@SpringBootApplication
@EnableScheduling
class BilbokaApplication

private val logger = LoggerFactory.getLogger(BilbokaApplication::class.java)

fun main(args: Array<String>) {
    configureDatabase()
    runApplication<BilbokaApplication>(*args)
}

private fun configureDatabase(): Database {
    val dbUri = URI(System.getenv("DATABASE_URL"))

    val username: String = dbUri.userInfo.split(":")[0]
    val pwd: String = dbUri.userInfo.split(":")[1]
    val dbUrl =
        "jdbc:postgresql://" + dbUri.host + ':' + dbUri.port + dbUri.path.toString() + "?sslmode=require"

    logger.debug("Creating datasource config for database $dbUrl")

    return Database.connect(
        url = dbUrl,
        user = username,
        password = pwd
    )
}
