package bilboka.messagebot;

import bilboka.core.book.Book
import bilboka.core.user.UserService
import bilboka.core.user.domain.RegistrationKey
import bilboka.core.vehicle.VehicleService
import bilboka.core.vehicle.domain.FuelType
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.stereotype.Component
import java.util.function.Predicate
import java.util.function.Predicate.isEqual

@SpringBootTest(classes = [MessageBot::class, TestMessenger::class, Book::class, VehicleService::class, UserService::class])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MessageBotIT : H2Test() {

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

    @Test
    fun sendAddFuelRequest() {
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 34567 30l 300kr",
            reply = "Registrert tanking av en testbil ved 34567 km: 30 liter for 300 kr, 10 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestDifferentCase() {
        processMessagaAndAssertReply(
            message = "fylt en testbil 5555 30.2 L 302.0 Kr",
            reply = "Registrert tanking av en testbil ved 5555 km: 30,2 liter for 302 kr, 10 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestDifferentCaseWithComma() {
        processMessagaAndAssertReply(
            message = "Hei drivstoff XC 70 1234 km 30,44 l 608,80 kr.. :D",
            reply = "Registrert tanking av xc 70 ved 1234 km: 30,44 liter for 608,8 kr, 20 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestNickname() {
        processMessagaAndAssertReply(
            message = "Hei drivstoff crosser 1234 km 30.44 l 608.80 kr",
            reply = "Registrert tanking av xc 70 ved 1234 km: 30,44 liter for 608,8 kr, 20 kr/l"
        )
    }

    @Test
    fun sendAddFuelRequestUnknownCar() {
        processMessagaAndAssertReply(
            message = "Drivstoff tullebil 34567 30l 300kr",
            reply = "Kjenner ikke til bil tullebil"
        )
    }

    @Test
    fun canSayHei() {
        processMessagaAndAssertReply(
            message = "Hei",
            reply = "Hei"
        )
    }

    @Test
    fun sendAddFuelRequestInvalidUser_pretendsToNotUnderstand() {
        processMessagaAndAssertReply(
            message = "Drivstoff en testbil 34567 30l 300kr",
            reply = FALLBACK_MESSAGE,
            sender = "5678"
        )
    }

    @Test
    fun sendGetLastFueling() {
        processMessagaAndAssertReply(
            message = "Siste xc70",
            reply = { it.contains("Siste tanking av xc 70: 30,44 liter for 608,8 kr (20 kr/l)") }
        )
    }

    @Test
    fun sendGetLastFuelingInvalidUser_pretendsToNotUnderstand() {
        processMessagaAndAssertReply(
            message = "Siste xc70",
            reply = FALLBACK_MESSAGE,
            sender = "5678"
        )
    }

    @Test
    fun sendRegisterRequestRegisteredUser_saysAlreadyRegisteredAndIsReadyForOtherStuff() {
        processMessagaAndAssertReply(
            message = "registrer",
            reply = { it.contains("Du er allerede registrert") },
        )
        processMessagaAndAssertReply(
            message = "hei",
            reply = "Hei"
        )
    }

    @Test
    fun sendRegisterRequestUnregisteredUser_canRegister() {
        processMessagaAndAssertReply(
            message = "registrer",
            reply = { it.contains("Klar for registrering! Skriv din hemmelige kode") },
            sender = "3333"
        )
        processMessagaAndAssertReply(
            message = keyForNewUser,
            reply = { it.contains("Du er registrert!") },
            sender = "3333"
        )
        processMessagaAndAssertReply(
            message = "hei",
            reply = "Hei",
            sender = "3333"
        )
    }

    @Test
    @Disabled // TODO dette må fikses på generell basis
    fun usersCanRunRegisteringIndependently() {
        processMessagaAndAssertReply(
            message = "registrer",
            sender = "238845",
            reply = "Klar for registrering! Skriv din hemmelige kode."
        )
        processMessagaAndAssertReply(
            message = "registrer",
            sender = "838845",
            reply = "Klar for registrering! Skriv din hemmelige kode."
        )
        processMessagaAndAssertReply(
            message = "hei",
            reply = "Hei"
        )
    }

    @Test
    fun brukerinfo() {
        processMessagaAndAssertReply(
            message = "brukerinfo",
            reply = "Du er registrert! \n" +
                    "Brukernavn: tester_user"
        )
    }

    private fun processMessagaAndAssertReply(message: String, reply: String, sender: String = validSender) {
        processMessagaAndAssertReply(message, isEqual(reply), "replies \"$reply\"", sender)
    }

    private fun processMessagaAndAssertReply(
        message: String,
        reply: Predicate<String>,
        matcherDescriptor: String = "",
        sender: String = validSender
    ) {
        messageBot.processMessage(message, sender)

        assertThat(testMessenger.messageSent).matches(reply, matcherDescriptor)
        assertThat(testMessenger.recipient).isEqualTo(sender)
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
