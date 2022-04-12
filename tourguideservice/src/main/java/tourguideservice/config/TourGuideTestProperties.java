package tourguideservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  Configuration Class for TourGuide management and tests properties:
 *  - trackerOnStart : Start the tracker on application start.
 *  - useInternalUser : Generate internal users on application start.
 *  - internalUserNumber : Number of internal users to generate.
 */
@Configuration
@ConfigurationProperties(prefix = "tourguide.test")
public class TourGuideTestProperties {

  private boolean trackingOnStart;
  private boolean useInternalUser;
  private int internalUserNumber;

  public boolean isTrackingOnStart() {
    return trackingOnStart;
  }

  public void setTrackingOnStart(boolean trackingOnStart) {
    this.trackingOnStart = trackingOnStart;
  }

  public boolean isUseInternalUser() {
    return useInternalUser;
  }

  public void setUseInternalUser(boolean useInternalUser) {
    this.useInternalUser = useInternalUser;
  }

  public int getInternalUserNumber() {
    return internalUserNumber;
  }

  public void setInternalUserNumber(int internalUserNumber) {
    this.internalUserNumber = internalUserNumber;
  }

}