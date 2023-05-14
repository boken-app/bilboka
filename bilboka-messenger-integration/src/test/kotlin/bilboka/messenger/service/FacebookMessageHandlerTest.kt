package bilboka.messenger.service

import bilboka.messagebot.MessageBot
import bilboka.messenger.FacebookMessenger
import bilboka.messenger.dto.*
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

    @Test
    fun handleMessageWithBothMessageAndPostback_prioritizesPostback() {
        justRun { messageBot.processMessage(any(), any()) }

        responderService.handleMessage(
            FacebookEntry(
                time = 3L,
                id = "1",
                messaging = listOf(
                    MessagingReceived(
                        sender = mapOf(Pair("id", senderID)),
                        timestamp = 2L,
                        message = MessageReceived(
                            text = testMessage
                        ),
                        postback = PostbackReceived(
                            "32542",
                            "title",
                            payload = "postback_payload"
                        )
                    )
                )
            )
        )

        verify(exactly = 1) { messageBot.processMessage("postback_payload", senderID) }
    }

    private fun messageWithText() = FacebookEntry(
        time = 3L,
        id = "1",
        messaging = listOf(
            MessagingReceived(
                sender = mapOf(Pair("id", senderID)),
                timestamp = 2L,
                message = MessageReceived(
                    text = testMessage
                )
            )
        )
    )

    private fun messageWithPostback() = FacebookEntry(
        time = 3L,
        id = "1",
        messaging = listOf(
            MessagingReceived(
                sender = mapOf(Pair("id", senderID)),
                timestamp = 2L,
                postback = PostbackReceived(
                    "32542",
                    "title",
                    testMessage
                )
            )
        )
    )

}