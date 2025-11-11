package bilboka.core.book

import bilboka.core.H2Test
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.book.domain.EventType
import bilboka.core.mockedBookEntries
import bilboka.core.mockedEntry
import bilboka.core.report.ReportGenerator
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.OdometerUnit
import bilboka.core.vehicle.domain.Vehicle
import bilboka.integration.autosys.dto.Kjoretoydata
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
class BookTest : H2Test() {

    @MockK
    lateinit var vehicleService: VehicleService

    @MockK
    lateinit var reportGenerator: ReportGenerator

    @InjectMockKs
    lateinit var book: Book

    @Test
    fun pkkIsUpdated_doesNothing() {
        val sistGodkjent = LocalDateTime.now().minusDays(10)
        every { vehicleService.getAutosysKjoretoydata(any<Vehicle>()) } returns mockk<Kjoretoydata>().apply {
            every { periodiskKjoretoyKontroll?.sistGodkjent } returns sistGodkjent.toLocalDate()
        }

        val vehicle = mockk<Vehicle>().apply {
            val entry = mockedEntry {
                every { dateTime } returns sistGodkjent
            }
            every { bookEntries } returns mockedBookEntries(entry)
            every { lastPKK() } returns entry
        }

        val refreshed = book.refreshPKK(vehicle, 123, null, "test")

        assertThat(refreshed).isNull()
    }

    @Nested
    inner class ShouldUpdate {
        lateinit var testVehicle: Vehicle
        lateinit var pkkEntry: BookEntry
        val sistRegistrertGodkjent = LocalDateTime.now().minusYears(2)
        val sistGodkjent = LocalDateTime.now().minusDays(10)

        @BeforeEach
        fun setupData() {
            every { vehicleService.getAutosysKjoretoydata(any<Vehicle>()) } returns mockk<Kjoretoydata>().apply {
                every { periodiskKjoretoyKontroll?.sistGodkjent } returns sistGodkjent.toLocalDate()
            }

            testVehicle = transaction {
                Vehicle.new {
                    this.name = "test"
                    this.tegnkombinasjonVisning = "AB 1234"
                    this.odometerUnit = OdometerUnit.KILOMETERS
                }
            }
            pkkEntry = transaction {
                BookEntry.new {
                    this.dateTime = sistRegistrertGodkjent
                    this.type = EntryType.EVENT
                    this.event = EventType.EU_KONTROLL_OK
                    this.odometer = 2000
                    this.vehicle = testVehicle
                    this.source = "test"
                }
            }
        }

        @Test
        fun pkkIsNotUpdated_updates() {
            val refreshed = book.refreshPKK(testVehicle, 3000, null, "test")

            assertThat(refreshed?.dateTime?.toLocalDate()).isEqualTo(sistGodkjent.toLocalDate())
            assertThat(refreshed?.odometer).isEqualTo(3000)
            assertThat(testVehicle.lastPKK()?.dateTime).isEqualTo(refreshed?.dateTime)
        }

        @Test
        fun pkkIsNotEnteredBefore_updates() {
            transaction {
                pkkEntry.delete()
            }
            val refreshed = book.refreshPKK(testVehicle, 3000, null, "test")

            assertThat(refreshed?.dateTime?.toLocalDate()).isEqualTo(sistGodkjent.toLocalDate())
            assertThat(refreshed?.odometer).isEqualTo(3000)
            assertThat(testVehicle.lastPKK()?.dateTime).isEqualTo(refreshed?.dateTime)
        }

        @Test
        fun pkkIsNotUpdated_failsWhenNonsenseOdo() {
            assertThrows<BookEntryChronologyException> {
                book.refreshPKK(testVehicle, 1000, null, "test")
            }
        }
    }

}
