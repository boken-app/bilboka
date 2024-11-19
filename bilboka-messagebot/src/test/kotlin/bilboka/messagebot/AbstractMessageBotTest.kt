package bilboka.messagebot

import bilboka.core.TripService
import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntry
import bilboka.core.book.domain.EntryType
import bilboka.core.user.UserAlreadyRegisteredException
import bilboka.core.user.UserService
import bilboka.core.user.domain.User
import bilboka.core.vehicle.VehicleNotFoundException
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.FuelType
import bilboka.core.vehicle.domain.OdometerUnit
import bilboka.core.vehicle.domain.Vehicle
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.jetbrains.exposed.sql.Database
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDateTime

@ExtendWith(MockKExtension::class)
abstract class AbstractMessageBotTest {

    @MockK
    lateinit var book: Book

    @MockK
    lateinit var botMessenger: BotMessenger

    @MockK
    lateinit var vehicleService: VehicleService

    @MockK
    lateinit var userService: UserService

    @MockK
    lateinit var tripService: TripService

    @InjectMockKs
    lateinit var messagebot: MessageBot

    internal val messengerSourceID = "Test_msgr"
    internal val unregisteredSenderID = "1237"
    internal val registeredSenderID = "1267"
    internal val registeredUser: User = mockk(relaxed = true)

    @BeforeEach
    fun setupMessenger() {
        Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
        every { botMessenger.sourceID } returns messengerSourceID
        justRun { botMessenger.sendMessage(any(), any()) }
        justRun { botMessenger.sendOptions(any(), any(), any()) }
        setupUser()
        messagebot.reset()
    }

    private fun setupUser() {
        every { userService.findUserByRegistration(any(), any()) } returns null
        every { userService.findUserByRegistration(messengerSourceID, registeredSenderID) } returns registeredUser
        every {
            userService.register(
                messengerSourceID,
                registeredSenderID,
                any()
            )
        } throws UserAlreadyRegisteredException("Allerede Registrert!")
    }

    protected fun mockVehicle(name: String): Vehicle {
        val vehicle = vehicle(name = name, fuelType = FuelType.DIESEL)
        every { vehicleService.getVehicle(any()) } throws VehicleNotFoundException("Fy!", name)
        every { vehicleService.findVehicle(any()) } returns null
        every { vehicleService.getVehicle(name) } returns vehicle
        every { vehicleService.findVehicle(name) } returns vehicle
        return vehicle
    }

    protected fun verifySentMessage(message: String, senderID: String = registeredSenderID) {
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
