package bilboka.core.book

import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.vehicle.domain.Vehicle
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

internal class FuelEntryTest {

    private val testbil = mockk<Vehicle>(relaxed = true)

    @Test
    fun pricePerLiterIsCorrect() {
        val fuelEntry = fuelEntry(
            vehicle = testbil,
            odometer = 250000,
            amount = 100.0,
            costNOK = 1000.0,
            isFull = true,
        )

        assertThat(fuelEntry.pricePerLiter()).isEqualTo(10.0)
        assertThat(fuelEntry.isFullTank).isTrue
    }

    @Test
    fun pricePerLiterIsNullWithNoAmount() {
        val fuelEntry = fuelEntry(
            vehicle = testbil,
            odometer = 250000,
            amount = null,
            costNOK = 1000.0,
            isFull = true,
        )

        assertThat(fuelEntry.pricePerLiter()).isNull()
    }

    @Test
    fun pricePerLiterIsNullWithNoCost() {
        val fuelEntry = fuelEntry(
            vehicle = testbil,
            odometer = 250000,
            amount = 100.0,
            costNOK = null,
            isFull = true,
        )

        assertThat(fuelEntry.pricePerLiter()).isNull()
    }

    @Test
    fun fieldsAreSet() {
        val date = LocalDateTime.now()
        val fuelEntry = fuelEntry(
            vehicle = testbil,
            dateTime = date,
            odometer = 1000000,
            amount = 2.0,
            costNOK = 3.0,
            isFull = true,
        )

        assertThat(fuelEntry.odometer).isEqualTo(1000000)
        assertThat(fuelEntry.amount).isEqualTo(2.0)
        assertThat(fuelEntry.costNOK).isEqualTo(3.0)
        assertThat(fuelEntry.isFullTank).isTrue()
        assertThat(fuelEntry.dateTime).isEqualTo(date)
        assertThat(fuelEntry.type).isEqualTo(EntryType.FUEL)
    }

    private fun fuelEntry(
        vehicle: Vehicle,
        dateTime: LocalDateTime = LocalDateTime.now(),
        odometer: Int,
        amount: Double?,
        costNOK: Double?,
        isFull: Boolean
    ): BookEntry {
        val entry = mockk<BookEntry>()
        every { entry.vehicle } returns vehicle
        every { entry.dateTime } returns dateTime
        every { entry.type } returns EntryType.FUEL
        every { entry.odometer } returns odometer
        every { entry.amount } returns amount
        every { entry.costNOK } returns costNOK
        every { entry.isFullTank } returns isFull
        every { entry.isFullTank } returns isFull
        every { entry.pricePerLiter() } answers { callOriginal() }
        return entry
    }
}
