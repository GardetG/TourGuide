package tourguideservice.controller;

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
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;
import tourguideservice.dto.AttractionDto;
import tourguideservice.dto.LocationDto;
import tourguideservice.dto.NearbyAttractionsListDto;
import tourguideservice.dto.UserRewardDto;
import tourguideservice.dto.VisitedLocationDto;
import tourguideservice.exception.UserNotFoundException;
import tourguideservice.service.TourGuideService;
import tourguideservice.utils.EntitiesTestFactory;

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
  private ArgumentCaptor<PreferencesDto> argumentCaptor;

  @DisplayName("GET user location return 200 with user longitude and latitude")
  @Test
  void getLocationTest() throws Exception {
    // GIVEN
    LocationDto locationDto = new LocationDto(45,-45);
    when(tourGuideService.getUserLocation(anyString())).thenReturn(locationDto);

    // WHEN
    mockMvc.perform(get("/getLocation?userName=jon"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.longitude", is(45.0)))
        .andExpect(jsonPath("$.latitude", is(-45.0)));
    verify(tourGuideService, times(1)).getUserLocation("jon");
  }

  @DisplayName("GET not found user location should return 404")
  @Test
  void getLocationNotFoundTest() throws Exception {
    // GIVEN
    when(tourGuideService.getUserLocation(anyString())).thenThrow(
        new UserNotFoundException("User not found"));

    // WHEN
    mockMvc.perform(get("/getLocation?userName=nonExistent"))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("User not found")));
    verify(tourGuideService, times(1)).getUserLocation("nonExistent");
  }

  @DisplayName("GET all current locations should return 200 with list of user Id and current location")
  @Test
  void getAllCurrentLocationsTest() throws Exception {
    // GIVEN
    Map<UUID, LocationDto> locationsMap = new HashMap<>();
    locationsMap.put(new UUID(0, 1), new LocationDto(0, 0));
    locationsMap.put(new UUID(0, 2), new LocationDto(45, -45));
    when(tourGuideService.getAllCurrentLocations()).thenReturn(locationsMap);

    // WHEN
    mockMvc.perform(get("/getAllCurrentLocations"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.00000000-0000-0000-0000-000000000001.longitude", is(0.0)))
        .andExpect(jsonPath("$.00000000-0000-0000-0000-000000000001.latitude", is(0.0)))
        .andExpect(jsonPath("$.00000000-0000-0000-0000-000000000002.longitude", is(45.0)))
        .andExpect(jsonPath("$.00000000-0000-0000-0000-000000000002.latitude", is(-45.0)));
    verify(tourGuideService, times(1)).getAllCurrentLocations();
  }

  @DisplayName("GET user rewards return 200 with list of rewards dto")
  @Test
  void getRewardsTest() throws Exception {
    // GIVEN
    VisitedLocationDto visitedLocationDto = new VisitedLocationDto(UUID.randomUUID(), new LocationDto(45,-45), new Date());
    AttractionDto attractionDto = new AttractionDto(UUID.randomUUID(), 50,-50,"Test1", "city", "state");
    UserRewardDto userRewardDto = new UserRewardDto(visitedLocationDto, attractionDto, 10);
    when(tourGuideService.getUserRewards(anyString())).thenReturn(List.of(userRewardDto));

    // WHEN
    mockMvc.perform(get("/getRewards?userName=jon"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].visitedLocation.location.longitude", is(45.0)))
        .andExpect(jsonPath("$[0].visitedLocation.location.latitude", is(-45.0)))
        .andExpect(jsonPath("$[0].attraction.attractionName", is("Test1")))
        .andExpect(jsonPath("$[0].attraction.longitude", is(50.0)))
        .andExpect(jsonPath("$[0].attraction.latitude", is(-50.0)))
        .andExpect(jsonPath("$[0].rewardPoints", is(10)));
    verify(tourGuideService, times(1)).getUserRewards("jon");
  }

  @DisplayName("GET not found user rewards should return 404")
  @Test
  void getRewardsNotFoundTest() throws Exception {
    // GIVEN
    when(tourGuideService.getUserRewards(anyString())).thenThrow(
        new UserNotFoundException("User not found"));

    // WHEN
    mockMvc.perform(get("/getRewards?userName=nonExistent"))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("User not found")));
    verify(tourGuideService, times(1)).getUserRewards("nonExistent");
  }

  @DisplayName("GET user preferences should return 200 with user preferences Dto")
  @Test
  void getUserPreferencesTest() throws Exception {
    // GIVEN
    PreferencesDto preferencesDto =
        new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE), 1, 1, 2, 3);
    when(tourGuideService.getUserPreferences(anyString())).thenReturn(preferencesDto);

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
    when(tourGuideService.getUserPreferences(anyString())).thenThrow(
        new UserNotFoundException("User not found"));

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
    PreferencesDto preferencesDto =
        new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE), 1, 1, 2, 3);
    when(
        tourGuideService.setUserPreferences(anyString(), any(PreferencesDto.class))).thenReturn(
        preferencesDto);

    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=jon")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonStream.serialize(preferencesDto)))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tripDuration", is(1)))
        .andExpect(jsonPath("$.numberOfAdults", is(2)))
        .andExpect(jsonPath("$.numberOfChildren", is(3)));
    verify(tourGuideService, times(1)).setUserPreferences(anyString(), argumentCaptor.capture());
    assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(preferencesDto);
  }

  @DisplayName("PUT not found user preferences should return 404")
  @Test
  void setUserPreferencesNotFoundTest() throws Exception {
    // GIVEN
    PreferencesDto preferencesDto =
        new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE), 1, 1, 2, 3);
    when(tourGuideService.setUserPreferences(anyString(), any(PreferencesDto.class))).thenThrow(
        new UserNotFoundException("User not found"));

    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=nonExistent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonStream.serialize(preferencesDto)))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("User not found")));
    verify(tourGuideService, times(1)).setUserPreferences(anyString(),
        any(PreferencesDto.class));
  }

  @DisplayName("PUT invalid user preferences should return 422")
  @Test
  void setUserPreferencesWhenInvalidTest() throws Exception {
    // GIVEN
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(-1), -1, -1, -1, -1);

    // WHEN
    mockMvc.perform(put("/setUserPreferences?userName=j")
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonStream.serialize(preferencesDto)))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.userPreferencesDto",
            is("Lower price point cannot be greater than High price point")))
        .andExpect(jsonPath("$.highPricePoint", is("High price point cannot be negative")))
        .andExpect(
            jsonPath("$.tripDuration", is("Trip Duration cannot be negative or equals to 0")))
        .andExpect(jsonPath("$.ticketQuantity", is("Ticket Quantity cannot be negative")))
        .andExpect(jsonPath("$.numberOfAdults", is("Number of Adults cannot be negative")))
        .andExpect(jsonPath("$.numberOfChildren", is("Number of Children cannot be negative")));
    verify(tourGuideService, times(0)).setUserPreferences(anyString(),
        any(PreferencesDto.class));
  }

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
    when(tourGuideService.getTripDeals(anyString())).thenThrow(
        new UserNotFoundException("User not found"));

    // WHEN
    mockMvc.perform(get("/getTripDeals?userName=nonExistent"))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("User not found")));
    verify(tourGuideService, times(1)).getTripDeals("nonExistent");
  }

  @DisplayName("GET nearby attractions should return 200 with nearby attractions Dto")
  @Test
  void getNearbyAttractionsTest() throws Exception {
    // GIVEN
    NearbyAttractionsListDto nearbyAttractionsDto = new NearbyAttractionsListDto(
        new LocationDto(45, -45),
        EntitiesTestFactory.getAttractionsDto()
    );
    when(tourGuideService.getNearByAttractions(anyString())).thenReturn(nearbyAttractionsDto);

    // WHEN
    mockMvc.perform(put("/getNearbyAttractions?userName=jon"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userLocation.longitude", is(45.0)))
        .andExpect(jsonPath("$.userLocation.latitude", is(-45.0)))
        .andExpect(jsonPath("$.attractions", hasSize(5)));
    verify(tourGuideService, times(1)).getNearByAttractions("jon");
  }

  @DisplayName("GET not found nearby attractions should return 404")
  @Test
  void getNearbyAttractionsNotFoundTest() throws Exception {
    // GIVEN
    when(tourGuideService.getNearByAttractions(anyString())).thenThrow(
        new UserNotFoundException("User not found"));

    // WHEN
    mockMvc.perform(put("/getNearbyAttractions?userName=nonExistent"))

        // THEN
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$", is("User not found")));
    verify(tourGuideService, times(1)).getNearByAttractions(anyString());
  }

}
