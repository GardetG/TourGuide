package userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shared.dto.PreferencesDto;
import shared.exception.UserNotFoundException;
import userservice.domain.UserPreferences;
import userservice.service.PreferencesService;
import userservice.service.UserService;
import userservice.utils.PreferencesMapper;

/**
 * Service Class implementation to manage users' preferences.
 */
@Service
public class PreferencesServiceImpl implements PreferencesService {

  @Autowired
  private UserService userService;

  /**
   * {@inheritDoc}
   */
  @Override
  public PreferencesDto getUserPreferences(String userName) throws UserNotFoundException {
    UserPreferences preferences = userService.retrieveUser(userName).getUserPreferences();
    return PreferencesMapper.toDto(preferences);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreferencesDto setUserPreferences(String userName, PreferencesDto preferencesDto)
      throws UserNotFoundException {
    UserPreferences preferences = PreferencesMapper.toEntity(preferencesDto);
    userService.retrieveUser(userName).setUserPreferences(preferences);
    return preferencesDto;
  }

}
