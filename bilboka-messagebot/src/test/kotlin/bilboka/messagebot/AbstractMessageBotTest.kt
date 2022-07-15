package bilboka.messagebot

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import org.junit.jupiter.api.BeforeEach

abstract class AbstractMessageBotTest {

    @MockK
    lateinit var carBookExecutor: CarBookExecutor

    @MockK
    lateinit var botMessenger: BotMessenger

    @InjectMockKs
    lateinit var messagebot: MessageBot

    internal val senderID = "1267"

    @BeforeEach
    fun setupMessenger() {
        justRun { botMessenger.sendMessage(any(), any()) }
    }
}
