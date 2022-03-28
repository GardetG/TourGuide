package shared.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import shared.dto.UserPreferencesDto;

public class RangeCheckValidator implements ConstraintValidator<RangeCheck, UserPreferencesDto> {

  @Override
  public void initialize(RangeCheck date) {
    // Nothing here
  }

  @Override
  public boolean isValid(UserPreferencesDto value, ConstraintValidatorContext context) {
    return value.getLowerPricePoint() <= value.getHighPricePoint();
  }

}