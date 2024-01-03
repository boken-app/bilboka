package bilboka.web.resource

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class BookEntriesResourceTest {

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(BookEntriesResource())
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

}
