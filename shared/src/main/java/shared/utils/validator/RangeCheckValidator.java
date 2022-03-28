package shared.utils.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import shared.dto.PreferencesDto;

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