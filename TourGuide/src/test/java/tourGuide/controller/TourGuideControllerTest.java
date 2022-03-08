package tourGuide.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jsoniter.output.JsonStream;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tourGuide.dto.LocationDto;
import tourGuide.dto.NearbyAttractionsDto;
import tourGuide.dto.ProviderDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.service.TourGuideService;
import tourGuide.utils.EntitiesTestFactory;

@WebMvcTest
@ActiveProfiles("test")
class TourGuideControllerTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private TourGuideController tourGuideController;

  @MockBean
  private TourGuideService tourGuideService;

  @Captor
  private ArgumentCaptor<UserPreferencesDto> argumentCaptor;

  @DisplayName("GET user trip deals should return 200 with list of provider Dto")
  @Test
  void getTripDealsTest() throws Exception {
    // GIVEN
    List<ProviderDto> providerDtos = EntitiesTestFactory.getProvidersDto(UUID.randomUUID());
    when(tourGuideService.getTripDeals(anyString())).thenReturn(providerDtos);

    // WHEN
    mockMvc.perform(get("/getTripDeals?userName=jon"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)));
    verify(tourGuideService, times(1)).getTripDeals("jon");
  }

  @DisplayName("GET not found user trip deals should return 404")
  @Test
  void getTripDealsNotFoundTest() throws Exception {
    // GIVEN
    when(tourGuideService.getTripDeals(anyString())).thenThrow(new UserNotFoundException("User not found"));

    // WHEN
    mockMvc.perform(get("/getTripDeals?userName=nonExistent"))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("User not found")));
    verify(tourGuideService, times(1)).getTripDeals("nonExistent");
  }

  @DisplayName("GET user preferences should return 200 with user preferences Dto")
  @Test
  void getUserPreferencesTest() throws Exception {
    // GIVEN
    UserPreferencesDto userPreferencesDto = new UserPreferencesDto(0, Integer.MAX_VALUE,1,1,2,3);
    when(tourGuideService.getUserPreferences(anyString())).thenReturn(userPreferencesDto);

    // WHEN
    mockMvc.perform(get("/getUserPreferences?userName=jon"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tripDuration", is(1)))
        .andExpect(jsonPath("$.numberOfAdults", is(2)))
        .andExpect(jsonPath("$.numberOfChildren", is(3)));
    verify(tourGuideService, times(1)).getUserPreferences("jon");
  }

  @DisplayName("GET not found user preferences should return 404")
  @Test
  void getUserPreferencesNotFoundTest() throws Exception {
    // GIVEN
    when(tourGuideService.getUserPreferences(anyString())).thenThrow(new UserNotFoundException("User not found"));

    // WHEN
    mockMvc.perform(get("/getUserPreferences?userName=nonExistent"))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("User not found")));
    verify(tourGuideService, times(1)).getUserPreferences("nonExistent");
  }

  @DisplayName("PUT user preferences should return 200 with user preferences Dto")
  @Test
  void setUserPreferencesTest() throws Exception {
    // GIVEN
    UserPreferencesDto userPreferencesDto = new UserPreferencesDto(0, Integer.MAX_VALUE,1,1,2,3);
    when(tourGuideService.setUserPreferences(anyString(), any(UserPreferencesDto.class))).thenReturn(userPreferencesDto);

    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=jon")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonStream.serialize(userPreferencesDto)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tripDuration", is(1)))
        .andExpect(jsonPath("$.numberOfAdults", is(2)))
        .andExpect(jsonPath("$.numberOfChildren", is(3)));
    verify(tourGuideService, times(1)).setUserPreferences(anyString(), argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).isEqualToComparingFieldByField(userPreferencesDto);
  }

  @DisplayName("PUT not found user preferences should return 404")
  @Test
  void setUserPreferencesNotFoundTest() throws Exception {
    // GIVEN
    UserPreferencesDto userPreferencesDto = new UserPreferencesDto(0, Integer.MAX_VALUE,1,1,2,3);
    when(tourGuideService.setUserPreferences(anyString(), any(UserPreferencesDto.class))).thenThrow(new UserNotFoundException("User not found"));

    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=nonExistent")
        .contentType(MediaType.APPLICATION_JSON)
        .content(JsonStream.serialize(userPreferencesDto)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("User not found")));
    verify(tourGuideService, times(1)).setUserPreferences(anyString(), any(UserPreferencesDto.class) );
  }

  @DisplayName("PUT invalid user preferences should return 422")
  @Test
  void setUserPreferencesWhenInvalidTest() throws Exception {
    // GIVEN
    UserPreferencesDto userPreferencesDto = new UserPreferencesDto(0,-1,-1,-1,-1,-1);

    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=j")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonStream.serialize(userPreferencesDto)))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.userPreferencesDto", is("Lower price point cannot be greater than High price point")))
        .andExpect(jsonPath("$.highPricePoint", is("High price point cannot be negative")))
        .andExpect(jsonPath("$.tripDuration", is("Trip Duration cannot be negative or equals to 0")))
        .andExpect(jsonPath("$.ticketQuantity", is("Ticket Quantity cannot be negative")))
        .andExpect(jsonPath("$.numberOfAdults", is("Number of Adults cannot be negative")))
        .andExpect(jsonPath("$.numberOfChildren", is("Number of Children cannot be negative")));
    verify(tourGuideService, times(0)).setUserPreferences(anyString(), any(UserPreferencesDto.class) );
  }

  @DisplayName("GET nearby attractions should return 200 with nearby attractions Dto")
  @Test
  void getNearbyAttractionsTest() throws Exception {
    // GIVEN
    NearbyAttractionsDto nearbyAttractionsDto = new NearbyAttractionsDto(
        new LocationDto(45,-45),
        EntitiesTestFactory.getAttractionsDto()
    );
    when(tourGuideService.getNearByAttractions(anyString())).thenReturn(nearbyAttractionsDto);

    // WHEN
    mockMvc.perform(put("/getNearbyAttractions?userName=jon"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userLocation.longitude", is(45)))
        .andExpect(jsonPath("$.userLocation.latitude", is(-45)))
        .andExpect(jsonPath("$.attractions", hasSize(5)));
    verify(tourGuideService, times(1)).getNearByAttractions("jon");
  }

  @DisplayName("GET not found nearby attractions should return 404")
  @Test
  void getNearbyAttractionsNotFoundTest() throws Exception {
    // GIVEN
    when(tourGuideService.getNearByAttractions(anyString())).thenThrow(new UserNotFoundException("User not found"));

    // WHEN
    mockMvc.perform(put("/getNearbyAttractions?userName=nonExistent"))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("User not found")));
    verify(tourGuideService, times(1)).getNearByAttractions(anyString());
  }

}
