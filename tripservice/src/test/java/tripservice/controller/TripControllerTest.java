package tripservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;
import tripservice.service.TripDealsService;
import tripservice.testutils.ProviderFactory;

@WebMvcTest
class TripControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TripDealsService tripDealsService;

  @Captor
  ArgumentCaptor<PreferencesDto> preferencesCaptor;

  @DisplayName("PUT preferences with user id and rewards should return 200 with providers")
  @Test
  void getTripDealsTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    UUID attractionId = UUID.randomUUID();
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE), 1, 1, 2, 3);
    List<ProviderDto> providers = ProviderFactory.getProvidersDto(attractionId);
    when(tripDealsService.getUserTripDeals(any(UUID.class), any(PreferencesDto.class), anyInt()))
        .thenReturn(providers);

    // WHEN
    mockMvc.perform(put("/getTripDeals?attractionId=" + attractionId + "&rewardPoints=10")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(preferencesDto)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)))
        .andExpect(jsonPath("$[0].name", is("Provider 0")))
        .andExpect(jsonPath("$[0].price", is(0d)))
        .andExpect(jsonPath("$[0].tripId", is(attractionId.toString())));
    verify(tripDealsService, times(1))
        .getUserTripDeals(any(UUID.class), any(PreferencesDto.class), anyInt());
  }

  @DisplayName("PUT invalid user preferences should return 422")
  @Test
  void getTripDealsWithInvalidPreferenceTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.valueOf(0), BigDecimal.valueOf(-1), -1, -1, -1, -1);

    // WHEN
    mockMvc.perform(
            put("/getTripDeals?attractionId=00000000-0000-0000-0000-000000000001&rewardPoints=10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(preferencesDto)))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.preferencesDto", is("Lower price point cannot be greater than High price point")))
        .andExpect(jsonPath("$.highPricePoint", is("High price point cannot be negative")))
        .andExpect(jsonPath("$.tripDuration", is("Trip Duration cannot be negative or equals to 0")))
        .andExpect(jsonPath("$.ticketQuantity", is("Ticket Quantity cannot be negative")))
        .andExpect(jsonPath("$.numberOfAdults", is("Number of Adults cannot be negative")))
        .andExpect(jsonPath("$.numberOfChildren", is("Number of Children cannot be negative")));
    verify(tripDealsService, times(0))
        .getUserTripDeals(any(UUID.class), any(PreferencesDto.class), anyInt());
  }

}
