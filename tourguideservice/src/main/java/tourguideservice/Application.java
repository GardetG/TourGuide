package tourguideservice;

import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main Class of TourGuideService Application.
 */
@SpringBootApplication
@EnableFeignClients
public class Application {

  public static void main(String[] args) {
    Locale.setDefault(Locale.UK);
    SpringApplication.run(Application.class, args);
  }

}
