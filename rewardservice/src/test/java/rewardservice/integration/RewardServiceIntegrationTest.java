package rewardservice.integration;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rewardservice.domain.Attraction;
import rewardservice.domain.Location;
import rewardservice.domain.UserReward;
import rewardservice.domain.VisitedLocation;
import rewardservice.repository.RewardsRepository;
import shared.dto.AttractionDto;
import shared.dto.LocationDto;
import shared.dto.VisitedAttractionDto;
import shared.dto.VisitedLocationDto;

@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc
class RewardServiceIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private RewardsRepository rewardsRepository;

  @DisplayName("GET all rewards of a user should return the list of user's rewards")
  @Test
  void getAllRewardsRewardTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    Attraction attraction = new Attraction("attraction", "", "", 45, -45);
    VisitedLocation location = new VisitedLocation(userId, new Location(45, -45), new Date());
    UserReward reward = new UserReward(location, attraction,10);
    rewardsRepository.save(reward);

    // WHEN
    mockMvc.perform(get("/getAllRewards?userId=" + userId))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].attraction.attractionName", is("attraction")))
        .andExpect(jsonPath("$[0].visitedLocation.location.latitude", is(45.0)))
        .andExpect(jsonPath("$[0].visitedLocation.location.longitude", is(-45.0)))
        .andExpect(jsonPath("$[0].rewardPoints", is(10)));
  }

  @DisplayName("GET total reward points of a user should return the total of points")
  @Test
  void getTotalRewardPointsTest() throws Exception {
    // GIVEN
    UUID userId = UUID.randomUUID();
    VisitedLocation location = new VisitedLocation(userId, new Location(45, -45), new Date());

    Attraction attraction1 = new Attraction("attraction1", "", "", 45, -45);
    UserReward reward1 = new UserReward(location, attraction1,7);
    rewardsRepository.save(reward1);

    Attraction attraction2 = new Attraction("attraction2", "", "", 50, -45);
    UserReward reward2 = new UserReward(location, attraction2,13);
    rewardsRepository.save(reward2);

    // WHEN
    mockMvc.perform(get("/getTotalRewardPoints?userId=" + userId))

        // THEN
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", is(20)));
  }

  @DisplayName("Calculate reward for visited attraction registered a new reward if it didn't exist")
  @Test
  void calculateReward() throws Exception {
    // GIVEN
    ObjectMapper objectMapper = new ObjectMapper();
    UUID userId = UUID.randomUUID();
    UUID attractionId = UUID.randomUUID();
    Date date = new Date();
    AttractionDto attraction = new AttractionDto(attractionId, "attraction", "", "", 0, 0);
    VisitedLocationDto location1 = new VisitedLocationDto(userId, new LocationDto(0, 0), date);
    VisitedLocationDto location2 = new VisitedLocationDto(userId, new LocationDto(0, 0), date);
    List<VisitedAttractionDto> attractionToReward = List.of(
        new VisitedAttractionDto(attraction, location1),
        new VisitedAttractionDto(attraction, location2));

    // WHEN
    mockMvc.perform(post("/calculateRewards?userId=" + userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(attractionToReward)))

        // THEN
        .andExpect(status().isOk());
    mockMvc.perform(get("/getAllRewards?userId=" + userId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)));
  }

}
