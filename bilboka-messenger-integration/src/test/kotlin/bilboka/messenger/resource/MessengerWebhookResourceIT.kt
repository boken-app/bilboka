package bilboka.messenger.resource

import bilboka.core.book.repository.BookStorage
import bilboka.core.book.repository.InMemoryStorage
import bilboka.messenger.consumer.MessengerWebhookConsumer
import bilboka.messenger.dto.*
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.clearAllMocks
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Bean
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

@RunWith(SpringRunner::class)
@WebMvcTest(MockMvc::class, properties = ["messenger.verify-token = detteerettesttoken"])
@ContextConfiguration(classes = [MessengerWebhookResourceIT.MessengerIntegrationConfig::class])
internal class MessengerWebhookResourceIT {

    // TODO Muligens lage skille på test og IT

    @MockkBean
    lateinit var messengerWebhookConsumer: MessengerWebhookConsumer

    @Autowired
    lateinit var mvc: MockMvc

    @BeforeEach
    fun setUp() {
        justRun { messengerWebhookConsumer.sendMessage(any()) }
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
                get("/webhook")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun getRequestMissingParams_returnsBadRequest() {
            mvc.perform(
                get("/webhook?hub.verify_token=detteerettesttoken&hub.mode=subscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(status().isBadRequest)
        }

        @Test
        fun validGetRequest_returnsChallengeAccepted() {
            mvc.perform(
                get("/webhook?hub.verify_token=detteerettesttoken&hub.challenge=CHALLENGE_ACCEPTED&hub.mode=subscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(content().string("CHALLENGE_ACCEPTED"))
        }

        @Test
        fun validGetRequest_returnsSomeOtherChallenge() {
            mvc.perform(
                get("/webhook?hub.verify_token=detteerettesttoken&hub.challenge=rullekake&hub.mode=subscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(content().string("rullekake"))
        }

        @Test
        fun invalidToken_returns403() {
            mvc.perform(
                get("/webhook?hub.verify_token=blah&hub.challenge=CHALLENGE_ACCEPTED&hub.mode=subscribe")
                    .contentType(MediaType.TEXT_HTML)
            )
                .andExpect(status().isForbidden)
        }

        @Test
        fun invalidMode_returns403() {
            mvc.perform(
                get("/webhook?hub.verify_token=detteerettesttoken&hub.challenge=CHALLENGE_ACCEPTED&hub.mode=unsubscribe")
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

            verify(exactly = 0) { messengerWebhookConsumer.sendMessage(any()) }
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

            verify(exactly = 0) { messengerWebhookConsumer.sendMessage(any()) }
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

            verify(exactly = 0) { messengerWebhookConsumer.sendMessage(any()) }
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

            verify { messengerWebhookConsumer.sendMessage(any()) }
        }

        @Test
        fun postRequestSomeListWithMessagingWithMuchContent_returnsOk() {
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
                        ),
                        FacebookEntry(
                            "124", 124L, listOf(
                                FacebookMessaging(
                                    1234L,
                                    null,
                                    personWithId("457"),
                                    personWithId("3565"),
                                    FacebookMessage(
                                        text = "TestMsg2"
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
                                        1234L,
                                        personWithId("457"),
                                        personWithId("3565"),
                                        "yes"
                                    )
                                )
                            )
                        )
                    )
                )
            )
                .andExpect(status().isOk).andExpect(okResponseContent())

            verify(exactly = 3) { messengerWebhookConsumer.sendMessage(any()) }
        }

        @Test
        fun postRequestSomeListWithPostback_returnsOk() {
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
                                    null,
                                    FacebookPostback(
                                        1234L,
                                        personWithId("456"),
                                        personWithId("354"),
                                        "no"
                                    )
                                )
                            )
                        )
                    )
                )
            )
                .andExpect(status().isOk).andExpect(okResponseContent())

            verify(exactly = 1) { messengerWebhookConsumer.sendMessage(any()) }
        }

        @Test
        fun postRequestWithoutBody_returnsBadRequest() {
            mvc.perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isBadRequest)

            verify(exactly = 0) { messengerWebhookConsumer.sendMessage(any()) }
        }

        private fun okResponseContent() = content().string("EVENT_RECEIVED")

        private fun postAsJson(request: MessengerWebhookRequest): ResultActions {
            return mvc.perform(
                post("/webhook")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        asJsonString(
                            request
                        )
                    )
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

    @Configuration
    @ComponentScan(basePackages = ["bilboka"])
    class MessengerIntegrationConfig {

        @Bean
        fun bookStorage(): BookStorage {
            return InMemoryStorage()
        }
    }

}
