package bilboka.messenger.resource

import bilboka.messagebot.BotMessenger
import bilboka.messagebot.MessageBot
import bilboka.messenger.consumer.MessengerSendAPIConsumer
import bilboka.messenger.dto.*
import bilboka.messenger.resource.MessengerWebhookConfig.WEBHOOK_URL
import bilboka.messenger.service.FacebookMessageHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.*
import org.apache.commons.codec.binary.Hex
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.Collections.emptyList
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

@RunWith(SpringRunner::class)
@WebMvcTest(
    MockMvc::class, properties = [
        "messenger.verify-token = detteerettesttoken",
        "messenger.appSecret = totallyLegitIPromise",
    ]
)
@ContextConfiguration(classes = [MessengerWebhookResourceIT.MessengerIntegrationConfig::class])
internal class MessengerWebhookResourceIT {

    // TODO Muligens lage skille på test og IT

    @MockkBean
    lateinit var messengerSendAPIConsumer: MessengerSendAPIConsumer

    @MockkBean // TODO Kan gi mening med en integrasjonstest som ikke mocker denne
    lateinit var messageBot: MessageBot

    @SpykBean
    lateinit var facebookMessageHandler: FacebookMessageHandler

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var botMessenger: BotMessenger

    @BeforeEach
    fun setUp() {
        justRun { messengerSendAPIConsumer.sendMessage(any()) }
        every { messageBot.processMessage(any(), any()) } answers { botMessenger.sendMessage("Et svar!", secondArg()) }
    }

    @AfterEach
    fun clear() {
        clearAllMocks()
    }

    @Nested
    inner class GetWebhookTests {

        @Test
        fun getRequestWithoutParams_returnsBadRequest() {
            mvc.perform(
                get("/$WEBHOOK_URL")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun getRequestMissingParams_returnsBadRequest() {
            mvc.perform(
                get("/$WEBHOOK_URL?hub.verify_token=detteerettesttoken&hub.mode=subscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun validGetRequest_returnsChallengeAccepted() {
            mvc.perform(
                get("/$WEBHOOK_URL?hub.verify_token=detteerettesttoken&hub.challenge=CHALLENGE_ACCEPTED&hub.mode=subscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(content().string("CHALLENGE_ACCEPTED"))
        }

        @Test
        fun validGetRequest_returnsSomeOtherChallenge() {
            mvc.perform(
                get("/$WEBHOOK_URL?hub.verify_token=detteerettesttoken&hub.challenge=rullekake&hub.mode=subscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(content().string("rullekake"))
        }

        @Test
        fun invalidToken_returns403() {
            mvc.perform(
                get("/$WEBHOOK_URL?hub.verify_token=blah&hub.challenge=CHALLENGE_ACCEPTED&hub.mode=subscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun invalidMode_returns403() {
            mvc.perform(
                get("/$WEBHOOK_URL?hub.verify_token=detteerettesttoken&hub.challenge=CHALLENGE_ACCEPTED&hub.mode=unsubscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(status().isForbidden)
        }
    }

    @Nested
    inner class PostWebhookTests {
        @Test
        fun postRequestEmptyListFromPageSubscription_returnsOk() {
            postAsJson(
                MessengerWebhookRequest(
                    requestObject = "page",
                    entry = emptyList()
                )
            )
                .andExpect(status().isOk).andExpect(okResponseContent())

            verify(exactly = 0) { messengerSendAPIConsumer.sendMessage(any()) }
        }

        @Test
        fun postRequestEmptyListNotFromPageSubscription_returnsNotFound() {
            postAsJson(
                MessengerWebhookRequest(
                    requestObject = "Blah",
                    entry = emptyList()
                )
            )
                .andExpect(status().isNotFound)

            verify(exactly = 0) { messengerSendAPIConsumer.sendMessage(any()) }
        }

        @Test
        fun postRequestSomeList_returnsOk() {
            postAsJson(
                MessengerWebhookRequest(
                    requestObject = "page", entry = listOf(
                        FacebookEntry(id = "123", time = 123L, messaging = emptyList())
                    )
                )
            )
                .andExpect(status().isOk).andExpect(okResponseContent())

            verify(exactly = 0) { messengerSendAPIConsumer.sendMessage(any()) }
        }

        @Test
        fun postRequestSomeListWithMessaging_returnsOk() {
            postAsJson(
                MessengerWebhookRequest(
                    requestObject = "page", entry = listOf(
                        FacebookEntry(
                            "123", 123L, listOf(
                                FacebookMessaging(
                                    1234L,
                                    null,
                                    personWithId("456"),
                                    personWithId("354"),
                                    FacebookMessage(
                                        text = "Test"
                                    ),
                                    null
                                )
                            )
                        )
                    )
                )
            )
                .andExpect(status().isOk).andExpect(okResponseContent())

            verify { messengerSendAPIConsumer.sendMessage(any()) }
        }

        @Test
        fun postRequestSomeListWithMessagingWithMuchContent_returnsOk() {
            postAsJson(
                MessengerWebhookRequest(
                    requestObject = "page", entry = listOf(
                        FacebookEntry(
                            "124", 123L, listOf(
                                FacebookMessaging(
                                    1234L,
                                    null,
                                    personWithId("456"),
                                    personWithId("354"),
                                    FacebookMessage(
                                        text = "Test",
                                        mid = "1"
                                    ),
                                    null
                                )
                            )
                        ),
                        FacebookEntry(
                            "125", 124L, listOf(
                                FacebookMessaging(
                                    1234L,
                                    null,
                                    personWithId("457"),
                                    personWithId("3565"),
                                    FacebookMessage(
                                        text = "TestMsg2",
                                        mid = "2"
                                    ),
                                    null
                                )
                            )
                        ),
                        FacebookEntry(
                            "125", 125L, listOf(
                                FacebookMessaging(
                                    1234L,
                                    null,
                                    personWithId("457"),
                                    personWithId("3565"),
                                    null,
                                    FacebookPostback(
                                        "1234L2",
                                        "TITLE",
                                        "yes"
                                    )
                                )
                            )
                        )
                    )
                )
            )
                .andExpect(status().isOk).andExpect(okResponseContent())

            verify(exactly = 3) { messengerSendAPIConsumer.sendMessage(any()) }
        }

        @Test
        fun postDuplicate_sendsOnlyOneReply() {
            postAsJson(
                MessengerWebhookRequest(
                    requestObject = "page", entry = listOf(
                        FacebookEntry(
                            "124", 123L, listOf(
                                FacebookMessaging(
                                    1234L,
                                    null,
                                    personWithId("456"),
                                    personWithId("354"),
                                    FacebookMessage(
                                        text = "Test",
                                        mid = "3"
                                    ),
                                    null
                                )
                            )
                        ),
                        FacebookEntry(
                            "124", 123L, listOf(
                                FacebookMessaging(
                                    1234L,
                                    null,
                                    personWithId("456"),
                                    personWithId("354"),
                                    FacebookMessage(
                                        text = "Test",
                                        mid = "3"
                                    ),
                                    null
                                )
                            )
                        ),
                    )
                )
            )
                .andExpect(status().isOk).andExpect(okResponseContent())

            verify(exactly = 1) { messengerSendAPIConsumer.sendMessage(any()) }
        }

        @Test
        fun postRequestSomeListWithPostback_returnsOk() {
            postAsJson(
                MessengerWebhookRequest(
                    requestObject = "page", entry = listOf(
                        FacebookEntry(
                            "127", 123L, listOf(
                                FacebookMessaging(
                                    1234L,
                                    null,
                                    personWithId("456"),
                                    personWithId("354"),
                                    null,
                                    FacebookPostback(
                                        "1234L2",
                                        "TITLE",
                                        "yes"
                                    )
                                )
                            )
                        )
                    )
                )
            )
                .andExpect(status().isOk).andExpect(okResponseContent())

            verify(exactly = 1) { messengerSendAPIConsumer.sendMessage(any()) }
        }

        @Test
        fun postRequestWithoutBody_returnsBadRequest() {
            mvc.perform(
                post("/$WEBHOOK_URL")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isBadRequest)

            verify { messengerSendAPIConsumer wasNot Called }
        }

        @Test
        fun postRequestWithoutSignatureHeader_returnsBadRequest() {
            mvc.perform(
                post("/$WEBHOOK_URL")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        asJsonString(
                            MessengerWebhookRequest(
                                requestObject = "page",
                                entry = emptyList()
                            )
                        )
                    )
            )
                .andExpect(status().isBadRequest)

            verify { messengerSendAPIConsumer wasNot Called }
        }

        @Test
        fun postRequestWithWrongSignatureHeader_returnsForbidden() {
            mvc.perform(
                post("/$WEBHOOK_URL")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-hub-signature-256", "totallyNotLegit")
                    .content(
                        asJsonString(
                            MessengerWebhookRequest(
                                requestObject = "page",
                                entry = emptyList()
                            )
                        )
                    )
            )
                .andExpect(status().isForbidden)

            verify { messengerSendAPIConsumer wasNot Called }
            verify { facebookMessageHandler wasNot Called }
        }

        private fun okResponseContent() = content().string("EVENT_RECEIVED")

        private fun postAsJson(request: MessengerWebhookRequest): ResultActions {
            val jsonString = asJsonString(request)
            return mvc.perform(
                post("/$WEBHOOK_URL")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-hub-signature-256", "sha256=${jsonString.hash("totallyLegitIPromise")}")
                    .content(jsonString)
            )
        }

        private fun personWithId(id: String): Map<String, String> {
            return mapOf(Pair("id", id))
        }
    }

    fun asJsonString(obj: Any): String {
        return try {
            ObjectMapper().writeValueAsString(obj)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun String.hash(key: String): String {
        val secretKeySpec = SecretKeySpec(key.toByteArray(), "HmacSHA256")
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(secretKeySpec)
        return Hex.encodeHexString(mac.doFinal(this.toByteArray()))
    }

    @Configuration
    @ComponentScan(basePackages = ["bilboka"])
    class MessengerIntegrationConfig

}
