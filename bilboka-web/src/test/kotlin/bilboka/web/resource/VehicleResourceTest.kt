package bilboka.web.resource

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class VehicleResourceTest {

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(VehicleResource())
            .build()
    }

    @Test
    fun `sample should return list of vehicles with correct URL`() {
        mockMvc.perform(get("/vehicles/sample"))
            .andExpect(status().isOk)
    }

    @Test
    fun `sampleById should return vehicle when ID is valid`() {
        mockMvc.perform(get("/vehicles/1/sample"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun `sampleById should return not found when ID is invalid`() {
        mockMvc.perform(get("/vehicles/non-existent-id/sample"))
            .andExpect(status().isNotFound)
    }

}
