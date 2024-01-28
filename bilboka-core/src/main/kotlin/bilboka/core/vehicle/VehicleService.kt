package bilboka.core.vehicle

import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.OdometerUnit
import bilboka.core.vehicle.domain.Vehicle
import bilboka.integration.autosys.consumer.AkfDatautleveringConsumer
import bilboka.integration.autosys.dto.Kjoretoydata
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service

@Service
class VehicleService(
    private val akfDatautleveringConsumer: AkfDatautleveringConsumer
) {

    fun addVehicle(
        name: String,
        nicknames: Set<String> = setOf(),
        fuelType: FuelType,
        tegnkombinasjon: String? = null,
        tankVol: Int? = null
    ): Vehicle {
        return transaction {
            Vehicle.new {
                this.name = name.lowercase()
                this.nicknames = nicknames
                this.fuelType = fuelType
                this.tegnkombinasjonVisning = tegnkombinasjon
                this.odometerUnit = OdometerUnit.KILOMETERS
                this.tankVolume = tankVol
            }
        }
    }

    fun getVehicles(): List<Vehicle> {
        return transaction {
            Vehicle.all().toList()
        }
    }

    fun getVehicle(vehicleName: String): Vehicle {
        return transaction {
            findVehicle(vehicleName)
                ?: throw VehicleNotFoundException(
                    "Fant ikke bil $vehicleName",
                    vehicleName
                )
        }
    }

    fun getVehicleById(id: Int): Vehicle {
        return transaction {
            Vehicle[id]
        }
    }

    fun findVehicle(vehicleName: String): Vehicle? {
        return vehicleName.takeIf { it.isNotEmpty() }?.let {
            transaction {
                Vehicle.all().singleOrNull { vehicle -> vehicle.isCalled(it) }
            }
        }
    }

    fun getAutosysKjoretoydata(vehicleName: String): Kjoretoydata {
        return getVehicle(vehicleName).run {
            akfDatautleveringConsumer.hentKjoretoydata(
                this.tegnkombinasjonNormalisert()
                    ?: throw VehicleMissingDataException("Mangler registreringsnummer for oppslag mot autosys")
            ).kjoretoydataListe.first()
        }
    }

}
