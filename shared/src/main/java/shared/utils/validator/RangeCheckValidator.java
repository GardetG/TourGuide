package shared.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import shared.dto.PreferencesDto;

/**
 * Custom Validator to check if the lower point of the range is less than the high point.
 */
public class RangeCheckValidator implements ConstraintValidator<RangeCheck, PreferencesDto> {

  @Override
  public void initialize(RangeCheck date) {
    // Nothing here
  }

  @Override
  public boolean isValid(PreferencesDto value, ConstraintValidatorContext context) {
    return value.getHighPricePoint().compareTo(value.getLowerPricePoint()) >= 0;
  }

}