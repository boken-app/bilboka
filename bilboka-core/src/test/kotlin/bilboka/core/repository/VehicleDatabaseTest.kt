package bilboka.core.repository

import bilboka.core.config.BilbokaCoreConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ContextConfiguration

@DataJpaTest
@ContextConfiguration(classes = [BilbokaCoreConfig::class])
class VehicleDatabaseTest {
    @Autowired
    lateinit var entityManager: TestEntityManager

    @Autowired
    lateinit var vehicleRepository: VehicleRepository

    @Test
    fun repositoryLoads() {

    }

}
