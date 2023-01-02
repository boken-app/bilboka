package bilboka

import bilboka.core.book.domain.Records
import bilboka.core.vehicle.domain.Vehicles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.net.URI

@SpringBootApplication
class BilbokaApplication

fun main(args: Array<String>) {
	// TODO flyway
	val dbUri = URI(System.getenv("DATABASE_URL"))

	val username: String = dbUri.userInfo.split(":")[0]
	val pwd: String = dbUri.userInfo.split(":")[1]
	val dbUrl =
		"jdbc:postgresql://" + dbUri.host + ':' + dbUri.port + dbUri.path.toString() + "?sslmode=require"

	val db = Database.connect(
		url = dbUrl,
		user = username,
		password = pwd
		//driver = "org.postgresql.Driver"
	)
	transaction(db) {
		SchemaUtils.create(Records, Vehicles)
		commit()
	}

	runApplication<BilbokaApplication>(*args)
}
