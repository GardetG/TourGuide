package userservice.service;

import org.springframework.stereotype.Service;
import shared.dto.PreferencesDto;
import shared.exception.UserNotFoundException;

/**
 * Service Interface to manage users' preferences.
 */
@Service
public interface PreferencesService {

  /**
   * Get the preferences of a user or throw an exception if the user can't be found.
   *
   * @param userName of the user
   * @return Preferences Dto
   * @throws UserNotFoundException when user not found
   */
  PreferencesDto getUserPreferences(String userName) throws UserNotFoundException;

  /**
   * Set the preferences of a user according to the provided Dto or throw an exception if the user
   * can't be found.
   *
   * @param userName       of the user
   * @param preferencesDto of the user to update
   * @return Preference Dto updated
   * @throws UserNotFoundException when user not found
   */
  PreferencesDto setUserPreferences(String userName, PreferencesDto preferencesDto)
      throws UserNotFoundException;

}
