package bilboka.core

import bilboka.core.domain.book.Records
import bilboka.core.domain.vehicle.Vehicles
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class H2Test {
    @BeforeAll
    fun setupDatabase() {
        val db = Database.connect("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")
        transaction(db) {
            SchemaUtils.create(Records, Vehicles)
            commit()
        }
    }
}
