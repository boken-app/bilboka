package bilboka.messagebot

import bilboka.core.book.Book
import bilboka.core.book.domain.Record
import bilboka.core.book.domain.RecordType
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

    protected fun fuelRecord(
        vehicle: Vehicle,
        dateTime: LocalDateTime = LocalDateTime.now(),
        odometer: Int,
        amount: Double?,
        costNOK: Double?,
        isFull: Boolean = false
    ): Record {
        val record = mockk<Record>()
        every { record.vehicle } returns vehicle
        every { record.dateTime } returns dateTime
        every { record.type } returns RecordType.FUEL
        every { record.odometer } returns odometer
        every { record.amount } returns amount
        every { record.costNOK } returns costNOK
        every { record.isFullTank } returns isFull
        every { record.isFullTank } returns isFull
        every { record.pricePerLiter() } answers { callOriginal() }
        return record
    }

}
