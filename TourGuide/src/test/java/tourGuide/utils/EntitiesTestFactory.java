package tourGuide.utils;

import gpsUtil.location.Attraction;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import tourGuide.dto.AttractionDto;
import tourGuide.dto.ProviderDto;
import tripPricer.Provider;

public class EntitiesTestFactory {

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

  // Return a Map of 5 Attractions with distance for testing purposes
  public static Map<Attraction, Double> getAttractionsWithDistance() {
    return IntStream.range(0,5)
        .mapToObj(index -> new Attraction("Attraction"+index, "", "", 0, index*50))
        .collect(Collectors.toMap(Function.identity(), attraction -> attraction.longitude ));
  }

  // Return a List of 5 AttractionsDto
  public static List<AttractionDto> getAttractionsDto() {
    return IntStream.range(0,5)
        .mapToObj(index -> new AttractionDto("Attraction"+index, 0, index*50, index*50, 100))
        .collect(Collectors.toList());
  }

}