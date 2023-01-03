package bilboka.messagebot

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.OdometerUnit
import bilboka.core.vehicle.domain.Vehicle
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeEach
import java.time.LocalDateTime

abstract class AbstractMessageBotTest {

    @MockK
    lateinit var book: Book

    @MockK
    lateinit var botMessenger: BotMessenger

    @InjectMockKs
    lateinit var messagebot: MessageBot

    internal val senderID = "1267"

    @BeforeEach
    fun setupMessenger() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        every { botMessenger.sourceName } returns "Testmessenger"
        justRun { botMessenger.sendMessage(any(), any()) }
    }

    protected fun verifySentMessage(message: String) {
        verify {
            botMessenger.sendMessage(
                message,
                senderID
            )
        }
    }

    protected fun vehicle(
        name: String,
        fuelType: FuelType,
        odometerUnit: OdometerUnit = OdometerUnit.KILOMETERS
    ): Vehicle {
        val vehicle = mockk<Vehicle>(relaxed = true)
        every { vehicle.name } returns name
        every { vehicle.fuelType } returns fuelType
        every { vehicle.odometerUnit } returns odometerUnit
        return vehicle
    }

    protected fun fuelEntry(
        vehicle: Vehicle,
        dateTime: LocalDateTime = LocalDateTime.now(),
        odometer: Int,
        amount: Double?,
        costNOK: Double?,
        isFull: Boolean = false
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
