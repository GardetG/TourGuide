package locationservice.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import locationservice.service.GpsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shared.dto.AttractionDto;
import shared.dto.AttractionWithDistanceDto;
import shared.dto.LocationDto;
import shared.dto.VisitedAttractionDto;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;

@WebMvcTest
class LocationControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GpsService gpsService;

  @Captor
  ArgumentCaptor<List<VisitedLocationDto>> visitedLocationsCaptor;

  @DisplayName("GET last location should return 200 with user last visited location")
  @Test
  void getLastLocationTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(45,-45), date);
    when(gpsService.getUserLastVisitedLocation(any(UUID.class))).thenReturn(visitedLocation);

    // WHEN
    mockMvc.perform(get("/getUserLastVisitedLocation?userId=" + userId))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(userId.toString())))
        .andExpect(jsonPath("$.location.latitude", is(45.0)))
        .andExpect(jsonPath("$.location.longitude", is(-45.0)))
        .andExpect(jsonPath("$.timeVisited").isNotEmpty());
    verify(gpsService, times(1)).getUserLastVisitedLocation(userId);
  }

  @DisplayName("GET all user last location should return 200 with list of last visited location")
  @Test
  void getAllUserLastVisitedLocationTest() throws Exception {
    // GIVEN
    VisitedLocationDto visitedLocation1 = new VisitedLocationDto(UUID.randomUUID(), new LocationDto(45,-45), new Date());
    VisitedLocationDto visitedLocation2 = new VisitedLocationDto(UUID.randomUUID(), new LocationDto(45,-45), new Date());
    when(gpsService.getAllUserLastVisitedLocation()).thenReturn(List.of(visitedLocation1, visitedLocation2));

    // WHEN
    mockMvc.perform(get("/getAllUserLastVisitedLocation"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)));
    verify(gpsService, times(1)).getAllUserLastVisitedLocation();
  }

  @DisplayName("GET last location when no location registered should return 409 conflict")
  @Test
  void getLastLocationWhenNoLocationTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    when(gpsService.getUserLastVisitedLocation(any(UUID.class)))
        .thenThrow(new NoLocationFoundException("No location registered for the user yet"));

    // WHEN
    mockMvc.perform(get("/getUserLastVisitedLocation?userId=" + userId))

        // THEN
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$", is("No location registered for the user yet")));
    verify(gpsService, times(1)).getUserLastVisitedLocation(userId);
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

  @DisplayName("GET attractions return 200 with list of all attractions")
  @Test
  void getAttractionsTest() throws Exception {
    // GIVEN
    AttractionDto attraction1 = new AttractionDto(UUID.randomUUID(), "attraction1","city1", "state1",45,-45);
    AttractionDto attraction2 = new AttractionDto(UUID.randomUUID(), "attraction2","city2", "state2",0,0);
    when(gpsService.getAttractions()).thenReturn(List.of(attraction1, attraction2));

    // WHEN
    mockMvc.perform(get("/getAttractions"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].attractionId", is(attraction1.getAttractionId().toString())))
        .andExpect(jsonPath("$[0].attractionName", is("attraction1")))
        .andExpect(jsonPath("$[0].city", is("city1")))
        .andExpect(jsonPath("$[0].state", is("state1")))
        .andExpect(jsonPath("$[0].latitude", is(45.0)))
        .andExpect(jsonPath("$[0].longitude", is(-45.0)));
    verify(gpsService, times(1)).getAttractions();
  }

  @DisplayName("POST visited location should return 200")
  @Test
  void addLocationTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(45,-45), date);

    // WHEN
    mockMvc.perform(post("/addLocation?userId=" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(visitedLocation))))

        // THEN
        .andExpect(status().isOk());
    verify(gpsService, times(1)).addVisitedLocation(any(UUID.class), visitedLocationsCaptor.capture());
    assertThat(visitedLocationsCaptor.getValue()).usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(visitedLocation);
  }

  @DisplayName("POST invalid visited location should return 422")
  @Test
  void addLocationWhenInvalidTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    UUID userId = UUID.randomUUID();
    VisitedLocationDto visitedLocation = new VisitedLocationDto(null, new LocationDto(100, -200), null);

    // WHEN
    mockMvc.perform(post("/addLocation?userId=" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(visitedLocation))))

        // THEN
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.userId", is("User id is mandatory")))
        .andExpect(jsonPath("$.latitude", is("Latitude can't be more than 90")))
        .andExpect(jsonPath("$.longitude", is("Longitude can't be less than -180")))
        .andExpect(jsonPath("$.timeVisited", is("Time visited is mandatory")));
    verify(gpsService, times(0)).addVisitedLocation(any(UUID.class), anyList());
  }

  @DisplayName("GET visited attractions should return 200 with list of visited attractions")
  @Test
  void getVisitedAttractionsTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    AttractionDto attraction1 = new AttractionDto(UUID.randomUUID(), "attraction1","city1", "state1",50,-50);
    AttractionDto attraction2 = new AttractionDto(UUID.randomUUID(), "attraction2","city2", "state2",0,0);
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(45,-45), date);
    when(gpsService.getVisitedAttractions(any(UUID.class))).thenReturn(List.of(
        new VisitedAttractionDto(attraction1, visitedLocation),
        new VisitedAttractionDto(attraction2, visitedLocation)
    ));

    // WHEN
    mockMvc.perform(get("/getVisitedAttractions?userId=" + userId))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].attraction.attractionId", is(attraction1.getAttractionId().toString())))
        .andExpect(jsonPath("$[0].attraction.attractionName", is("attraction1")))
        .andExpect(jsonPath("$[0].attraction.latitude", is(50.0)))
        .andExpect(jsonPath("$[0].attraction.longitude", is(-50.0)))
        .andExpect(jsonPath("$[0].visitedLocation.userId", is(userId.toString())))
        .andExpect(jsonPath("$[0].visitedLocation.location.latitude", is(45.0)))
        .andExpect(jsonPath("$[0].visitedLocation.location.longitude", is(-45.0)));
    verify(gpsService, times(1)).getVisitedAttractions(userId);
  }

  @DisplayName("GET nearby attractions should return 200 with list of attractions with distance")
  @Test
  void getNearbyAttractionsTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    AttractionDto attraction1 = new AttractionDto(UUID.randomUUID(), "attraction1","city1", "state1",50,-50);
    AttractionDto attraction2 = new AttractionDto(UUID.randomUUID(), "attraction2","city2", "state2",0,0);
    when(gpsService.getNearbyAttractions(any(UUID.class), anyInt())).thenReturn(List.of(
        new AttractionWithDistanceDto(attraction1, 10d),
        new AttractionWithDistanceDto(attraction2, 15d)
    ));

    // WHEN
    mockMvc.perform(get("/getNearbyAttractions?userId=" + userId + "&limit=2"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].attraction.attractionId", is(attraction1.getAttractionId().toString())))
        .andExpect(jsonPath("$[0].attraction.attractionName", is("attraction1")))
        .andExpect(jsonPath("$[0].attraction.latitude", is(50.0)))
        .andExpect(jsonPath("$[0].attraction.longitude", is(-50.0)))
        .andExpect(jsonPath("$[0].distance", is(10.0)));
    verify(gpsService, times(1)).getNearbyAttractions(userId, 2);
  }

}
