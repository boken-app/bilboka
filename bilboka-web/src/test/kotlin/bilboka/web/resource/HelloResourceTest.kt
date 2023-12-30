package bilboka.web.resource


import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.TEXT_PLAIN
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest(HelloResource::class)
@ContextConfiguration(classes = [HelloResourceTest.TestConfig::class])
class HelloResourceTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test get method`() {
        mockMvc.perform(
            get("/")
                .accept(TEXT_PLAIN)
        )
            .andExpect(status().isOk)
            .andExpect(content().string("Hei fra Bilboka!"))
    }

    @Configuration
    @ComponentScan(basePackages = ["bilboka"])
    class TestConfig

}
