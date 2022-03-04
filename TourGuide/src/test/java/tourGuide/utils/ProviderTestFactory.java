package tourGuide.testutils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tourGuide.dto.ProviderDto;
import tripPricer.Provider;

public class ProviderTestFactory {

  // Return a list of 5 providers with the following price [0,25,50,75,100] for testing purposes
  public static List<Provider> getProviders(UUID attractionId) {
    return IntStream.range(0,5)
        .mapToObj(index -> new Provider(attractionId, String.format("Provider %s", index), index*25))
        .collect(Collectors.toList());
  }

  // Return a list of 5 providerDtos with the following price [0,25,50,75,100] for testing purposes
  public static List<ProviderDto> getProvidersDto(UUID attractionId) {
    return IntStream.range(0,5)
        .mapToObj(index -> new ProviderDto(attractionId, String.format("Provider %s", index), index*25))
        .collect(Collectors.toList());
  }

}