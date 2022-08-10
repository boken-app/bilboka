package bilboka.core.repository

import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import java.util.*

class InMemoryStorage : VehicleRepository {

    val vehicles: HashSet<Vehicle> = HashSet()

    init {
        val bil1 = Vehicle(
            name = "XC 70",
            nicknames = setOf("XC 70", "XC70", "XC-70"),
            tegnkombinasjonNormalisert = "KT65881",
            fuelType = FuelType.DIESEL
        )
        save(bil1)
        val bil2 = Vehicle(
            name = "760",
            tegnkombinasjonNormalisert = "DF43250",
            fuelType = FuelType.DIESEL
        )
        save(bil2)
    }

    override fun <S : Vehicle?> save(vehicle: S): S {
        vehicles.add(vehicle!!)
        return vehicle
    }

    final override fun getByName(name: String): Vehicle? {
        return vehicles.find { vehicle -> vehicle.isCalled(name) }
    }

    override fun <S : Vehicle?> saveAll(entities: MutableIterable<S>): MutableIterable<S> {
        TODO("Not yet implemented")
    }

    override fun findAll(): MutableIterable<Vehicle> {
        TODO("Not yet implemented")
    }

    override fun findAllById(ids: MutableIterable<Long>): MutableIterable<Vehicle> {
        TODO("Not yet implemented")
    }

    override fun count(): Long {
        TODO("Not yet implemented")
    }

    override fun delete(entity: Vehicle) {
        TODO("Not yet implemented")
    }

    override fun deleteAllById(ids: MutableIterable<Long>) {
        TODO("Not yet implemented")
    }

    override fun deleteAll(entities: MutableIterable<Vehicle>) {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: Long) {
        TODO("Not yet implemented")
    }

    override fun existsById(id: Long): Boolean {
        TODO("Not yet implemented")
    }

    override fun findById(id: Long): Optional<Vehicle> {
        TODO("Not yet implemented")
    }
}
