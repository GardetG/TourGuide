package tourguideservice.proxy;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import shared.exception.NoLocationFoundException;
import shared.exception.UserNameAlreadyUsedException;
import shared.exception.UserNotFoundException;
import tourguideservice.exception.ProxyResponseErrorException;

/**
 * Custom Feign Client Error Decoder.
 */
public class FeignCustomErrorDecoder implements ErrorDecoder {

  private static final Logger LOGGER = LoggerFactory.getLogger(FeignCustomErrorDecoder.class);

  @Override
  public Exception decode(String methodKey, Response response) {

    String proxyName = methodKey.split("#")[0];

    switch (response.status()) {
      case 404:
        if (proxyName.equals("LocationServiceProxy")) {
          LOGGER.error("No location registered for the user yet");
          return new NoLocationFoundException("No location registered for the user yet");
        }
        if (proxyName.equals("UserServiceProxy")) {
          LOGGER.error("User not found");
          return new UserNotFoundException("User not found");
        }
        return defaultException(404, proxyName);
      case 409:
        if (proxyName.equals("UserServiceProxy")) {
          LOGGER.error("This username is already used");
          return new UserNameAlreadyUsedException("This username is already used");
        }
        return defaultException(409, proxyName);
      default:
        return defaultException(response.status(), proxyName);
    }
  }

  private Exception defaultException(int errorCode, String proxyName) {
    String message = String.format("Unexpected error %s in %s response", errorCode, proxyName);
    LOGGER.error(message);
    return new ProxyResponseErrorException(message);
  }

}