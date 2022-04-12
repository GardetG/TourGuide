package rewardservice;

import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Class of RewardService Application.
 */
@SpringBootApplication
public class Application {

  public static void main(String[] args) {
    Locale.setDefault(Locale.UK);
    SpringApplication.run(Application.class, args);
  }

}
