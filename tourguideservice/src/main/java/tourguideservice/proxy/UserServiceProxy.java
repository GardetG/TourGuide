package tourguideservice.proxy;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import shared.dto.PreferencesDto;
import shared.dto.UserDto;
import shared.exception.UserNameAlreadyUsedException;
import shared.exception.UserNotFoundException;

@FeignClient(value = "user-service", url = "http://localhost:8084")
public interface UserServiceProxy {

  @GetMapping("/getUser")
  UserDto getUser(@RequestParam String userName) throws UserNotFoundException;

  @PostMapping("/addUser")
  UserDto addUser(@RequestBody @Valid UserDto userDto) throws UserNameAlreadyUsedException;

  @GetMapping("/getAllUserId")
  List<UUID> getAllUserId();

  @GetMapping("/getUserPreferences")
  PreferencesDto getUserPreferences(@RequestParam String userName) throws UserNotFoundException;

  @PutMapping("/setUserPreferences")
  PreferencesDto setUserPreferences(@RequestParam String userName,
                                     @RequestBody @Valid PreferencesDto preferencesDto) throws UserNotFoundException;

}
