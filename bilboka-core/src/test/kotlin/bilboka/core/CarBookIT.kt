package bilboka.core

import bilboka.core.book.domain.FuelRecord
import bilboka.core.book.repository.BookStorage
import bilboka.core.book.repository.InMemoryStorage
import bilboka.core.book.service.CarBookService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootTest(classes = [CarBookService::class, IntegrationTestConfig::class])
class CarBookIT {

    @Autowired
    lateinit var carBookService: CarBookService

    @Test
    fun bookExistsForXC70() {
        val book = carBookService.getBookForVehicle("xc70")
        assertThat(book).isNotNull
    }

    @Test
    fun addFuelForXC70_succeeds() {
        carBookService.addRecordForVehicle(
            FuelRecord(
                amount = 12.4,
                costNOK = 22.43
            ), "760"
        )
        assertThat(carBookService.getBookForVehicle("760")?.records).isNotEmpty
    }
}

@Configuration
class IntegrationTestConfig {
    @Bean
    fun bookStorage(): BookStorage {
        return InMemoryStorage()
    }
}
