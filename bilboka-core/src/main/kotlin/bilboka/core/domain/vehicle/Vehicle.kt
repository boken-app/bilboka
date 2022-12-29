package bilboka.core.domain.vehicle

import bilboka.core.book.service.normaliserTegnkombinasjon
import bilboka.core.domain.book.Record
import bilboka.core.domain.book.RecordType
import bilboka.core.domain.book.Records
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime

object Vehicles : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()

    // TODO nicknames
    val tegnkombinasjonNormalisert = varchar("tegnkombinasjon_normalisert", 15).nullable()
    val odometerUnit = enumerationByName("odo_unit", 20, OdometerUnit::class).nullable()
    val fuelType = enumerationByName("fuel_type", 15, FuelType::class).nullable()
}

class Vehicle(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Vehicle>(Vehicles)

    var name by Vehicles.name

    //val nicknames: Set<String> = setOf(),
    var tegnkombinasjonNormalisert by Vehicles.tegnkombinasjonNormalisert
    var odometerUnit by Vehicles.odometerUnit
    var fuelType by Vehicles.fuelType
    val records by Record referrersOn Records.vehicle

    fun addFuel(
        odometer: Int?,
        amount: Double?,
        costNOK: Double?,
        isFull: Boolean = false,
        source: String,
        dateTime: LocalDateTime = LocalDateTime.now()
    ): Record {
        val thisVehicle = this
        return transaction {
            Record.new {
                this.dateTime = dateTime
                this.type = RecordType.FUEL
                this.odometer = odometer
                this.vehicle = thisVehicle
                this.amount = amount
                this.costNOK = costNOK
                this.isFullTank = isFull
                this.source = source
            }
        }
    }

    fun lastRecord(): Record? {
        return transaction { records.lastOrNull() }
    }

    fun lastRecord(type: RecordType): Record? {
        return transaction { records.lastOrNull { record -> record.type == type } }
    }

    fun fuelType(): FuelType {
        return fuelType ?: throw IllegalStateException("Kjøretøy mangler fueltype")
    }

    fun isCalled(calledName: String): Boolean {
        return calledName.lowercase() == name.lowercase()
                //  || nicknames.map { it.lowercase() }.contains(calledName.lowercase())
                || hasTegnkombinasjon(calledName)
    }

    fun hasTegnkombinasjon(tegnkombinasjon: String): Boolean {
        return tegnkombinasjon.normaliserTegnkombinasjon() == tegnkombinasjonNormalisert
    }

}
