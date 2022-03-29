package tripservice.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shared.dto.PreferencesDto;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class TripServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @DisplayName("PUT preferences with user id and rewards should return 200 with providers")
  @Test
  void getTripDealsTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE), 1, 1, 2, 3);

    // WHEN
    mockMvc.perform(put("/getTripDeals?attractionId=00000000-0000-0000-0000-000000000001&rewardPoints=10")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(preferencesDto)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)));
  }

}
