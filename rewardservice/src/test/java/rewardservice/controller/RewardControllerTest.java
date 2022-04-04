package rewardservice.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rewardservice.service.RewardsService;
import shared.dto.AttractionDto;
import shared.dto.LocationDto;
import shared.dto.UserRewardDto;
import shared.dto.VisitedAttractionDto;
import shared.dto.VisitedLocationDto;

@WebMvcTest
class RewardControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private RewardsService rewardsService;

  @DisplayName("GET all rewards should return 200 with all user rewards")
  @Test
  void getAllRewardsTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    AttractionDto attraction = new AttractionDto(UUID.randomUUID(), "attraction", "city", "state", 45, -45);
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(50,-50), new Date());
    when(rewardsService.getAllRewards(any(UUID.class))).thenReturn(List.of(new UserRewardDto(
        visitedLocation,
        attraction,
        10
    )));

    // WHEN
    mockMvc.perform(get("/getAllRewards?userId=" + userId))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].visitedLocation.userId", is(userId.toString())))
        .andExpect(jsonPath("$[0].visitedLocation.location.latitude", is(50.0)))
        .andExpect(jsonPath("$[0].visitedLocation.location.longitude", is(-50.0)))
        .andExpect(jsonPath("$[0].attraction.attractionName", is("attraction")));
    verify(rewardsService, times(1)).getAllRewards(userId);
  }

  @DisplayName("GET total reward points should return 200 with user total points")
  @Test
  void getTotalRewardPointsTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    when(rewardsService.getTotalRewardPoints(any(UUID.class))).thenReturn(15);

    // WHEN
    mockMvc.perform(get("/getTotalRewardPoints?userId=" + userId))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(15)));
    verify(rewardsService, times(1)).getTotalRewardPoints(userId);
  }


  @DisplayName("POST visited attractions to calculate reward should return 200")
  @Test
  void calculateRewardsTest() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    UUID userId = UUID.randomUUID();
    UUID attractionId = UUID.randomUUID();
    Date date = new Date();
    AttractionDto attraction = new AttractionDto(attractionId, "attraction", "", "",0,0);
    VisitedLocationDto location = new VisitedLocationDto(userId, new LocationDto(0,0), date);
    List<VisitedAttractionDto> attractionToReward = List.of(new VisitedAttractionDto(attraction, location));

    // WHEN
    mockMvc.perform(post("/calculateRewards?userId=" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(attractionToReward)))

        // THEN
        .andExpect(status().isOk());
    verify(rewardsService, times(1)).calculateRewards(any(UUID.class),anyList());
  }

  @DisplayName("GET reward points should return 200 with points")
  @Test
  void getRewardPointsTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    UUID attractionId = UUID.randomUUID();
    when(rewardsService.getRewardPoints(any(UUID.class),any(UUID.class))).thenReturn(20);

    // WHEN
    mockMvc.perform(get("/getRewardPoints?attractionId=" + attractionId + "&userId=" + userId))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(20)));
    verify(rewardsService, times(1)).getRewardPoints(attractionId, userId);
  }

}
