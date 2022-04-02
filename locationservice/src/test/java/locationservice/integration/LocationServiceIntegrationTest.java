package locationservice.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import locationservice.repository.LocationHistoryRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import shared.dto.LocationDto;
import shared.dto.VisitedLocationDto;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class LocationServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private LocationHistoryRepository locationHistoryRepository;

  @BeforeAll
  public static void setDefaultLocale() {
    Locale.setDefault(Locale.UK);
  }

  @DisplayName("Get last location should return the last tracked location")
  @Test
  void getLastLocationTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    MvcResult trackResult = mockMvc.perform(get("/trackUserLocation?userId=" + userId))
        .andExpect(status().isOk())
        .andReturn();

    // WHEN
    MvcResult lastLocationResult = mockMvc.perform(get("/getUserLastVisitedLocation?userId=" + userId))
        .andExpect(status().isOk())
        .andReturn();

    // THEN
    assertThat(trackResult.getResponse().getContentAsString()).isEqualTo(
        lastLocationResult.getResponse().getContentAsString());
  }

  @DisplayName("Get attraction should return the list of attractions")
  @Test
  void getAttractionsTest() throws Exception {
    // WhHEN
    mockMvc.perform(get("/getAttractions"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(26)));
  }

  @DisplayName("Add location should add to the user a new location")
  @Test
  void addLocationTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    VisitedLocationDto visitedLocation =
        new VisitedLocationDto(userId, new LocationDto(45, -45), date);

    // WHEN
    mockMvc.perform(post("/addVisitedLocation?userId=" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(visitedLocation))))

        // THEN
        .andExpect(status().isOk());
    mockMvc.perform(get("/getUserLastVisitedLocation?userId=" + userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.userId", is(userId.toString())))
        .andExpect(jsonPath("$.location.latitude", is(45.0)))
        .andExpect(jsonPath("$.location.longitude", is(-45.0)))
        .andExpect(jsonPath("$.timeVisited").isNotEmpty());
  }

  @DisplayName("Get visited location should return list of visited attraction")
  @Test
  void getVisitedLocationTest() throws Exception {
    // GIVEN a location at DisneyLand
    ObjectMapper objectMapper = new ObjectMapper();
    UUID userId = UUID.randomUUID();
    VisitedLocationDto visitedLocation =
        new VisitedLocationDto(userId, new LocationDto(33.817595D, -117.922008D), new Date());
    mockMvc.perform(post("/addVisitedLocation?userId=" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(visitedLocation))))
        .andExpect(status().isOk());

    // WHEN
    mockMvc.perform(get("/getVisitedAttractions?userId=" + userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].attraction.attractionName", is("Disneyland")))
        .andExpect(jsonPath("$[0].visitedLocation.userId", is(userId.toString())))
        .andExpect(jsonPath("$[0].visitedLocation.location.latitude", is(33.817595D)))
        .andExpect(jsonPath("$[0].visitedLocation.location.longitude", is(-117.922008D)));
  }

  @DisplayName("Get last location should return the last tracked location")
  @Test
  void getNearbyAttractionsTest() throws Exception {
    // GIVEN a location at DisneyLand
    ObjectMapper objectMapper = new ObjectMapper();
    UUID userId = UUID.randomUUID();
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(33.817595D, -117.922008D), new Date());
    mockMvc.perform(post("/addVisitedLocation?userId=" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(visitedLocation))))
        .andExpect(status().isOk());


    // WHEN
    mockMvc.perform(get("/getNearbyAttractions?userId=" + userId + "&limit=3"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].attraction.attractionName", is("Disneyland")))
        .andExpect(jsonPath("$[0].distance", is(0.0)));
  }

  @DisplayName("Get last location should return the last tracked location")
  @Test
  void getAllUserLastLocationTest() throws Exception {
    // GIVEN
    locationHistoryRepository.deleteAll();
    ObjectMapper objectMapper = new ObjectMapper();
    UUID userId = UUID.randomUUID();
    VisitedLocationDto oldVisitedLocation = new VisitedLocationDto(userId, new LocationDto(90, -90), new Date(0));
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(45, -45), new Date());
    mockMvc.perform(post("/addVisitedLocation?userId=" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(List.of(oldVisitedLocation, visitedLocation))))
        .andExpect(status().isOk());

    // WHEN
    mockMvc.perform(get("/getAllUserLastVisitedLocation"))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].userId", is(userId.toString())))
        .andExpect(jsonPath("$[0].location.latitude", is(45.0)))
        .andExpect(jsonPath("$[0].location.longitude", is(-45.0)))
        .andExpect(jsonPath("$[0].timeVisited").isNotEmpty());
  }

}
