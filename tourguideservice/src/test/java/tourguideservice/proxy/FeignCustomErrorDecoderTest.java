package tourguideservice.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import feign.Request;
import feign.Response;
import feign.Util;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import shared.exception.NoLocationFoundException;
import shared.exception.UserNameAlreadyUsedException;
import shared.exception.UserNotFoundException;
import tourguideservice.exception.ProxyResponseErrorException;

class FeignCustomErrorDecoderTest {

  private final FeignCustomErrorDecoder errorDecoder = new FeignCustomErrorDecoder();

  @DisplayName("Response 404 from LocationService should be decoded in NoLocationFoundException")
  @Test
  void decode404LocationServiceTest() {
    // Given
    String methodKey = "LocationServiceProxy#method";
    Response response = Response.builder()
        .status(404)
        .reason("Not Found")
        .request(Request.create(Request.HttpMethod.GET, "/api", Collections.emptyMap(), null, Util.UTF_8))
        .build();

    // When
    Exception exception = errorDecoder.decode(methodKey, response);

    // Then
    assertThat(exception).isInstanceOf(NoLocationFoundException.class);
  }

  @DisplayName("Response 404 from UserService should be decoded in UserNotFoundException")
  @Test
  void decode404UserServiceTest() {
    // Given
    String methodKey = "UserServiceProxy#method";
    Response response = Response.builder()
        .status(404)
        .reason("Not Found")
        .request(Request.create(Request.HttpMethod.GET, "/api", Collections.emptyMap(), null, Util.UTF_8))
        .build();

    // When
    Exception exception = errorDecoder.decode(methodKey, response);

    // Then
    assertThat(exception).isInstanceOf(UserNotFoundException.class);
  }

  @DisplayName("Response 409 from UserService should be decoded in UserNameAlreadyUsedException")
  @Test
  void decode409UserServiceTest() {
    // Given
    String methodKey = "UserServiceProxy#method";
    Response response = Response.builder()
        .status(409)
        .reason("Conflict")
        .request(Request.create(Request.HttpMethod.GET, "/api", Collections.emptyMap(), null, Util.UTF_8))
        .build();

    // When
    Exception exception = errorDecoder.decode(methodKey, response);

    // Then
    assertThat(exception).isInstanceOf(UserNameAlreadyUsedException.class);
  }

  @DisplayName("Other errors should be decoded in ProxyResponseErrorException")
  @ParameterizedTest(name = "Error {0}")
  @CsvSource({"404", "409", "500"})
  void decodeOtherErrorTest(int errorCode) {
    // Given
    String methodKey = "OtherProxy#method";
    Response response = Response.builder()
        .status(errorCode)
        .reason("Internal Server Error")
        .request(Request.create(Request.HttpMethod.GET, "/api", Collections.emptyMap(), null, Util.UTF_8))
        .build();

    // When
    Exception exception = errorDecoder.decode(methodKey, response);

    // Then
    assertThat(exception).isInstanceOf(ProxyResponseErrorException.class);
    assertThat(exception.getMessage()).isEqualTo("Unexpected error "+ errorCode +" in OtherProxy response");
  }

}
