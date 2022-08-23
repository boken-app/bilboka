package bilboka.messagebot

import bilboka.core.Book
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import org.junit.jupiter.api.BeforeEach

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
        justRun { botMessenger.sendMessage(any(), any()) }
    }
}
