package tourguideservice.proxy;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeignCustomErrorDecoder implements ErrorDecoder {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeignCustomErrorDecoder.class);

  @Override
  public Exception decode(String methodKey, Response response) {
    switch (response.status()) {
      case 400:
        LOGGER.error("Error in request went through feign client");
        return new Exception("Bad Request Through Feign");
      case 404:
        LOGGER.error("Error in request went through feign client");
        return new Exception("Unauthorized Request Through Feign");
      case 409:
        LOGGER.error("Error in request went through feign client");
        return new Exception("Unidentified Request Through Feign");
      default:
        LOGGER.error("Error in request went through feign client");
        return new Exception("Common Feign Exception");
    }
  }

}