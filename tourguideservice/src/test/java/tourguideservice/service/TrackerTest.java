package tourguideservice.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import tourguideservice.service.tracker.Tracker;

@SpringBootTest(properties = {"tourguide.test.trackingOnStart=false",
    "tourguide.test.useInternalUser=false"})
@ActiveProfiles("test")
class TrackerTest {

  @Autowired
  private Tracker tracker;

  @MockBean
  private TourGuideService tourGuideService;

  @DisplayName("Get user location should return last location dto")
  @Test
  void getUserLocationTest() {
    // Given
    UUID userId = UUID.randomUUID();
    when(tourGuideService.getAllUsersId()).thenReturn(List.of(userId));

    // When
    tracker.run();

    //Then
    verify(tourGuideService, times(1)).getAllUsersId();
    verify(tourGuideService, times(1)).trackUserLocation(userId);
    verify(tourGuideService, times(1)).calculateRewards(userId);
  }

}
