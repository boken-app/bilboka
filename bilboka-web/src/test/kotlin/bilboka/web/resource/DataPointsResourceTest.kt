package bilboka.web.resource

import bilboka.core.book.domain.BookEntry
import bilboka.core.vehicle.VehicleService
import bilboka.web.resource.Mocker.mockedBookEntries
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
class DataPointsResourceTest : H2Test() {

    private lateinit var mockMvc: MockMvc

    @MockK
    private lateinit var vehicleService: VehicleService

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(DataPointsResource(vehicleService))
            .build()
    }

    @Test
    fun `sample should return list of data points`() {
        mockMvc.perform(get("/vehicles/1/datapoints/sample"))
            .andExpect(status().isOk)
    }

    @Test
    fun `sampleById should return not found when ID is invalid`() {
        mockMvc.perform(get("/vehicles/non-existent-id/datapoints/sample"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `should return some items when vehicle has entries`() {
        every { vehicleService.getVehicleById(1) } returns mockk(relaxed = true) {
            every { bookEntries } returns mockedBookEntries(3)
        }

        mockMvc.perform(get("/vehicles/1/datapoints"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(3))
    }

    @Test
    fun `should return empty list when no entries`() {
        every { vehicleService.getVehicleById(1) } returns mockk(relaxed = true) {
            every { bookEntries } returns mockk(relaxed = true) {
                every { iterator() } returns emptyList<BookEntry>().iterator()
            }
        }

        mockMvc.perform(get("/vehicles/1/datapoints"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    fun `should return empty list when just one entry without date`() {
        val mockedBookEntries = mockedBookEntries(1)
        with(mockedBookEntries.first()) {
            every { dateTime } returns null
        }
        every { vehicleService.getVehicleById(1) } returns mockk(relaxed = true) {
            every { bookEntries } returns mockedBookEntries
        }

        mockMvc.perform(get("/vehicles/1/datapoints"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }
}
