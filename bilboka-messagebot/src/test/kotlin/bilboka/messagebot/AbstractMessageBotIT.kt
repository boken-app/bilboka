package bilboka.messagebot

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntries
import bilboka.core.report.ReportGenerator
import bilboka.core.user.UserService
import bilboka.core.user.domain.RegistrationKey
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.FuelType
import bilboka.integration.autosys.AutosysProperties
import bilboka.integration.autosys.consumer.AkfDatautleveringConsumer
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import java.util.function.Predicate

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [MessageBot::class, TestMessenger::class, Book::class, VehicleService::class, AkfDatautleveringConsumer::class, UserService::class, ReportGenerator::class, AutosysProperties::class])
abstract class AbstractMessageBotIT : H2Test() {

    @Autowired
    lateinit var testMessenger: TestMessenger

    @Autowired
    lateinit var messageBot: MessageBot

    @Autowired
    lateinit var vehicleService: VehicleService

    @Autowired
    lateinit var userService: UserService

    val validSender = "2345"
    val keyForNewUser = "some_new_key-lol"

    @BeforeAll
    fun setup() {
        vehicleService.addVehicle(
            name = "XC 70",
            nicknames = setOf("xc70", "crosser"),
            fuelType = FuelType.DIESEL,
            tegnkombinasjon = "KT65881"
        )
        vehicleService.addVehicle(
            name = "en testbil",
            fuelType = FuelType.BENSIN,
            tankVol = 100
        )
        vehicleService.addVehicle(
            name = "blå testbil",
            fuelType = FuelType.BENSIN
        )
        val key = "some_key-lol"
        val user = userService.addUser("tester_user")
        val userForReg = userService.addUser("tester_user_for_reg")
        transaction {
            RegistrationKey.new {
                this.user = user
                this.key = key
            }
            RegistrationKey.new {
                this.user = userForReg
                this.key = keyForNewUser
            }
        }
        userService.register("regtype", validSender, key)
    }

    @AfterEach
    fun resetMock() {
        testMessenger.reset()
    }

    @AfterEach
    fun resetState() {
        messageBot.reset()
        transaction {
            BookEntries.deleteAll()
        }
    }

    protected fun processMessagaAndAssertReply(message: String, reply: String, sender: String = validSender) {
        processMessagaAndAssertReply(message, Predicate.isEqual(reply), "replies \"$reply\"", sender)
    }

    protected fun processMessagaAndAssertReply(
        message: String,
        reply: Predicate<String>,
        matcherDescriptor: String = "",
        sender: String = validSender
    ) {
        messageBot.processMessage(message, sender)

        assertThat(testMessenger.messageSent).matches(reply, matcherDescriptor)
        assertThat(testMessenger.recipient).isEqualTo(sender)
    }

    protected fun skipFullTankQuestion() {
        processMessagaAndAssertReply("Ja", reply = { true })
    }
}

@Component
class TestMessenger : BotMessenger {
    override val sourceID: String
        get() = "test_messenger"

    var messageSent: String? = null
    var optionsAsked: String? = null
    var recipient: String? = null
    var fileSent: ByteArray? = null

    fun reset() {
        messageSent = null
        recipient = null
        optionsAsked = null
        fileSent = null
    }

    override fun sendMessage(message: String, recipientID: String) {
        messageSent = message
        recipient = recipientID
    }

    override fun sendOptions(message: String, options: List<Pair<String, String>>, recipientID: String) {
        optionsAsked = message
    }

    override fun sendPdf(file: ByteArray, fileName: String, recipientID: String) {
        fileSent = file
        recipient = recipientID
    }

}
