package locationservice.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.UUID;
import locationservice.service.GpsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import shared.dto.LocationDto;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;

@WebMvcTest
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GpsService gpsService;

  @DisplayName("GET last location should return 200 with user last visited location")
  @Test
  void getLastLocationTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(45,-45), date);
    when(gpsService.getLastLocation(any(UUID.class))).thenReturn(visitedLocation);

    // WHEN
    mockMvc.perform(get("/getLastLocation?userId=" + userId))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(userId.toString())))
        .andExpect(jsonPath("$.location.latitude", is(45.0)))
        .andExpect(jsonPath("$.location.longitude", is(-45.0)))
        .andExpect(jsonPath("$.timeVisited").isNotEmpty());
    verify(gpsService, times(1)).getLastLocation(userId);
  }

  @DisplayName("GET last location when no location registered should return 409 conflict")
  @Test
  void getTripDealsTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    when(gpsService.getLastLocation(any(UUID.class)))
        .thenThrow(new NoLocationFoundException("No location registered for the user yet"));

    // WHEN
    mockMvc.perform(get("/getLastLocation?userId=" + userId))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("No location registered for the user yet")));
    verify(gpsService, times(1)).getLastLocation(userId);
  }

  @DisplayName("GET track location should return 200 with user current visited location")
  @Test
  void trackUserLocationTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(45,-45), date);
    when(gpsService.trackUserLocation(any(UUID.class))).thenReturn(visitedLocation);

    // WHEN
    mockMvc.perform(get("/trackUserLocation?userId=" + userId))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(userId.toString())))
        .andExpect(jsonPath("$.location.latitude", is(45.0)))
        .andExpect(jsonPath("$.location.longitude", is(-45.0)))
        .andExpect(jsonPath("$.timeVisited").isNotEmpty());
    verify(gpsService, times(1)).trackUserLocation(userId);
  }

}
