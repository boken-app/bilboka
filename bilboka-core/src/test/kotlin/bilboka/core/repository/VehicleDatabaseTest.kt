package bilboka.core.repository

import bilboka.core.domain.vehicle.FuelType
import bilboka.core.domain.vehicle.Vehicle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@Disabled
class VehicleDatabaseTest {
    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var vehicleRepository: VehicleRepository

    @Test
    fun canSaveAndGet() {
        vehicleRepository.save(
            Vehicle(
                name = "navn",
                tegnkombinasjonNormalisert = "AB3355",
                fuelType = FuelType.BENSIN,
            )
        )

        assertThat(vehicleRepository.getByName("navn")).isNotNull
    }

    @Test
    fun canSaveAndGetByNickname() {
        vehicleRepository.save(
            Vehicle(
                name = "navn",
                nicknames = setOf("et navn", "kallenavn"),
                tegnkombinasjonNormalisert = "AB3355",
                fuelType = FuelType.BENSIN,
            )
        )

        assertThat(vehicleRepository.findByNicknames("et navn")).isNotNull
    }

    @Test
    fun canSaveAndGetByNicknameFindsNothing() {
        vehicleRepository.save(
            Vehicle(
                name = "navn",
                nicknames = setOf("et navn", "kallenavn"),
                tegnkombinasjonNormalisert = "AB3355",
                fuelType = FuelType.BENSIN,
            )
        )

        assertThat(vehicleRepository.findByNicknames("etnavn")).isNull()
    }

    @Test
    fun canSaveAndGetByTegnkombinasjon() {
        vehicleRepository.save(
            Vehicle(
                name = "navn",
                nicknames = setOf("et navn", "kallenavn"),
                tegnkombinasjonNormalisert = "AB3355",
                fuelType = FuelType.BENSIN,
            )
        )

        assertThat(vehicleRepository.findByTegnkombinasjonNormalisert("AB3355")).isNotNull
    }

}
