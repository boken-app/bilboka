package bilboka.core

import bilboka.core.book.domain.Records
import bilboka.core.vehicle.domain.Vehicles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.context.annotation.Configuration

@Configuration
class DatabaseConfig {

    // TODO postgres og flyway
    init {
        val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(db) {
            SchemaUtils.create(Records, Vehicles)
            commit()
        }
    }

}
