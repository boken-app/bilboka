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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ExtendWith(MockKExtension::class)
class BookEntriesResourceTes : H2Test() { // TODO burde ikke trenge H2 her.

    private lateinit var mockMvc: MockMvc

    @MockK
    private lateinit var vehicleService: VehicleService

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(BookEntriesResource(vehicleService))
            .build()
    }

    @Test
    fun `sample should return list of data points`() {
        mockMvc.perform(get("/vehicles/1/entries/sample"))
            .andExpect(status().isOk)
    }

    @Test
    fun `sampleById should return not found when ID is invalid`() {
        mockMvc.perform(get("/vehicles/non-existent-id/entries/sample"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return some entries when vehicle has entries`() {
        every { vehicleService.getVehicleById(1) } returns mockk(relaxed = true) {
            every { bookEntries } returns Mocker.mockedBookEntries(3)
        }

        mockMvc.perform(get("/vehicles/1/entries"))
            .andExpect(status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(3))
    }
}
