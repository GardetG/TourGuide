package tourguideservice.utils;

import java.util.List;
import java.util.stream.Collectors;
import tourguideservice.domain.UserReward;
import shared.dto.AttractionDto;
import tourguideservice.dto.UserRewardDto;
import shared.dto.VisitedLocationDto;

/**
 * Mapper utility class to map UserReward DTO and entity.
 */
public class UserRewardMapper {

  private UserRewardMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a UserReward entity into DTO.
   *
   * @param userReward to map
   * @return corresponding UserRewardDto mapped
   */
  public static UserRewardDto toDto(UserReward userReward) {
    VisitedLocationDto visitedLocation = new VisitedLocationDto(
        userReward.visitedLocation.userId,
        LocationMapper.toDto(userReward.visitedLocation.location),
        userReward.visitedLocation.timeVisited
    );
    AttractionDto attraction = AttractionMapper.toDto(userReward.attraction);
    return new UserRewardDto(
        visitedLocation,
        attraction,
        userReward.getRewardPoints()
    );
  }

  /**
   * Map a list of UserReward entity into DTOs.
   *
   * @param userRewards to map
   * @return corresponding list of UserRewardDto mapped
   */
  public static List<UserRewardDto> toDto(List<UserReward> userRewards) {
    return userRewards.stream()
        .map(UserRewardMapper::toDto)
        .collect(Collectors.toList());
  }

}
