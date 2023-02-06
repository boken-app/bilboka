package bilboka.messagebot

import bilboka.core.book.Book
import bilboka.core.book.domain.BookEntries
import bilboka.core.user.UserService
import bilboka.core.user.domain.RegistrationKey
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.FuelType
import org.assertj.core.api.Assertions
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
@SpringBootTest(classes = [MessageBot::class, TestMessenger::class, Book::class, VehicleService::class, UserService::class])
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

        Assertions.assertThat(testMessenger.messageSent).matches(reply, matcherDescriptor)
        Assertions.assertThat(testMessenger.recipient).isEqualTo(sender)
    }
}

@Component
class TestMessenger : BotMessenger {
    override val sourceID: String
        get() = "test_messenger"

    var messageSent: String? = null
    var recipient: String? = null

    fun reset() {
        messageSent = null
        recipient = null
    }

    override fun sendMessage(message: String, recipientID: String) {
        messageSent = message
        recipient = recipientID
    }

    override fun sendPostback(options: List<String>, recipientID: String) {
        TODO("Not yet implemented")
    }

}
