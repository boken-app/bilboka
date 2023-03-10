package bilboka.messenger.service

import bilboka.messagebot.MessageBot
import bilboka.messenger.FacebookMessenger
import bilboka.messenger.dto.FacebookEntry
import bilboka.messenger.dto.FacebookMessage
import bilboka.messenger.dto.FacebookMessaging
import bilboka.messenger.dto.FacebookPostback
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class FacebookMessageHandlerTest {

    @MockK
    lateinit var facebookMessenger: FacebookMessenger

    @MockK
    lateinit var messageBot: MessageBot

    @InjectMockKs
    var responderService = FacebookMessageHandler()

    private val senderID = "123"
    private val testMessage = "Hei test!"

    @Test
    fun handleMessageWithText() {
        justRun { messageBot.processMessage(any(), any()) }

        responderService.handleMessage(
            this.messageWithText()
        )

        verify { messageBot.processMessage(testMessage, senderID) }
    }

    @Test
    fun handleMessageWithPostback() {
        justRun { messageBot.processMessage(any(), any()) }

        responderService.handleMessage(
            this.messageWithPostback()
        )

        verify { messageBot.processMessage(testMessage, senderID) }
    }

    private fun messageWithText() = FacebookEntry(
        time = 3L,
        id = "1",
        messaging = listOf(
            FacebookMessaging(
                sender = mapOf(Pair("id", senderID)),
                timestamp = 2L,
                message = FacebookMessage(
                    text = testMessage
                )
            )
        )
    )

    private fun messageWithPostback() = FacebookEntry(
        time = 3L,
        id = "1",
        messaging = listOf(
            FacebookMessaging(
                sender = mapOf(Pair("id", senderID)),
                timestamp = 2L,
                postback = FacebookPostback(
                    "32542",
                    "title",
                    testMessage
                )
            )
        )
    )

}