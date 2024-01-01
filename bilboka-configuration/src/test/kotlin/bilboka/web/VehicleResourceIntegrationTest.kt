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
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ExtendWith(SpringExtension::class)
@WebMvcTest(VehicleResource::class)
@ContextConfiguration(classes = [VehicleResourceTest.TestConfig::class])
class VehicleResourceTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `test get sample`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/vehicles/sample")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
        //  .andExpect(MockMvcResultMatchers.content().string("Hei fra Bilboka!"))
    }

    @Test
    fun `test get single sample`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/vehicles/2/sample")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `test get single sample not found`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/vehicles/4/sample")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().isNotFound)
    }

    @Configuration
    @ComponentScan(basePackages = ["bilboka"])
    class TestConfig
}
