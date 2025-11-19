package bilboka.web.resource

import bilboka.core.vehicle.VehicleService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
class VehicleResourceTest : H2Test() { // TODO burde ikke trenge H2 her.

    private lateinit var mockMvc: MockMvc

    @MockK
    private lateinit var vehicleService: VehicleService

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(VehicleResource(vehicleService))
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
    fun `should return empty list`() {
        every { vehicleService.getVehicles() } returns emptyList()
        mockMvc.perform(get("/vehicles"))
            .andExpect(status().isOk)
    }

    @Test
    fun `should return 400 if letters in id`() {
        mockMvc.perform(get("/vehicles/something"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return vehicle when ID is valid`() {
        every { vehicleService.getVehicleById(1) } returns mockk(relaxed = true) {
            every { id } returns mockk(relaxed = true) {
                every { value } returns 1
            }
        }
        every { vehicleService.getAutosysKjoretoydata(any<String>()) } returns mockk(relaxed = true)
        mockMvc.perform(get("/vehicles/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun `sampleById should return not found when ID is invalid`() {
        mockMvc.perform(get("/vehicles/non-existent-id/sample"))
            .andExpect(status().isNotFound)
    }

}
