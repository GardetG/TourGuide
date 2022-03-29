package tourguideservice.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import tripPricer.Provider;

/**
 * User Entity with id, personal coordinates, user preferences and trips deals.
 */
public class User {

	private final UUID userId;
	private final String userName;
	private String phoneNumber;
	private String emailAddress;
	private UserPreferences userPreferences = new UserPreferences();
	private List<Provider> tripDeals = new ArrayList<>();

	/**
	 * User constructor instantiate a user from id, and personal coordinates.
	 *
	 * @param userId of the user
	 * @param userName of the user
	 * @param phoneNumber of the user
	 * @param emailAddress of the user
	 */
	public User(UUID userId, String userName, String phoneNumber, String emailAddress) {
		this.userId = userId;
		this.userName = userName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}
	
	public UUID getUserId() {
		return userId;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	public String getEmailAddress() {
		return emailAddress;
	}

	public UserPreferences getUserPreferences() {
		return userPreferences;
	}
	
	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	public void setTripDeals(List<Provider> tripDeals) {
		this.tripDeals = tripDeals;
	}
	
	public List<Provider> getTripDeals() {
		return tripDeals;
	}

}
