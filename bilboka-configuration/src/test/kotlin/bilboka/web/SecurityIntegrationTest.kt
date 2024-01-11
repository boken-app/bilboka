package bilboka.web

import bilboka.web.resource.VehicleResource
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(VehicleResource::class)
@ContextConfiguration(classes = [VehicleResourceTest.TestConfig::class])
@TestPropertySource(
    properties = [
        "bilboka.web.apiToken=bilbokaWebTestApiToken"
    ]
)
class SecurityIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test get vehicle unauthorized`() {
        mockMvc.perform(
            get("/vehicles/4")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    @Test
    fun `test get vehicle authorized`() {
        mockMvc.perform(
            get("/vehicles/4")
                .header("X-API-KEY", encodeEmail("some.test@mail.com"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Test
    fun `test get vehicles authorized`() {
        mockMvc.perform(
            get("/vehicles")
                .header("X-API-KEY", encodeEmail("some.test@mail.com"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `test get vehicle user not existing`() {
        mockMvc.perform(
            get("/vehicles")
                .header("X-API-KEY", encodeEmail("other.test@mail.com"))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }

    private fun encodeEmail(email: String): String {
        return String(Base64.getEncoder().encode(email.toByteArray()))
    }

    @Configuration
    @ComponentScan(basePackages = ["bilboka"])
    class TestConfig
}
