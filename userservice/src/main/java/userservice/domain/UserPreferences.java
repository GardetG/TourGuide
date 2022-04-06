package userservice.domain;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.javamoney.moneta.Money;

/**
 * Entity Class for the user preferences used when getting trip deals with the range of expecting
 * prices, the trip duration, and number of adults and children.
 */
public class UserPreferences {
	
	private int attractionProximity = Integer.MAX_VALUE;
	private final CurrencyUnit currency = Monetary.getCurrency("USD");
	private Money lowerPricePoint;
	private Money highPricePoint;
	private int tripDuration;
	private int ticketQuantity;
	private int numberOfAdults ;
	private int numberOfChildren;

	/**
	 * Default constructor to instantiate UserPreferences with default values.
	 */
	public UserPreferences() {
		this.lowerPricePoint = Money.of(0, currency);
		this.highPricePoint = Money.of(Integer.MAX_VALUE, currency);
		this.tripDuration = 1;
		this.ticketQuantity = 1;
		this.numberOfAdults = 1;
		this.numberOfChildren = 0;
	}

	/**
	 * Parametric constructor to instantiate a UserPreferences with the values chosen by the user.
	 */
	public UserPreferences(double lowerPricePoint, double highPricePoint, int tripDuration,
						   int ticketQuantity, int numberOfAdults, int numberOfChildren) {
		this.lowerPricePoint = Money.of(lowerPricePoint, currency);
		this.highPricePoint = Money.of(highPricePoint, currency);
		this.tripDuration = tripDuration;
		this.ticketQuantity = ticketQuantity;
		this.numberOfAdults = numberOfAdults;
		this.numberOfChildren = numberOfChildren;
	}

	public void setAttractionProximity(int attractionProximity) {
		this.attractionProximity = attractionProximity;
	}
	
	public int getAttractionProximity() {
		return attractionProximity;
	}
	
	public Money getLowerPricePoint() {
		return lowerPricePoint;
	}

	public void setLowerPricePoint(Money lowerPricePoint) {
		this.lowerPricePoint = lowerPricePoint;
	}

	public Money getHighPricePoint() {
		return highPricePoint;
	}

	public void setHighPricePoint(Money highPricePoint) {
		this.highPricePoint = highPricePoint;
	}
	
	public int getTripDuration() {
		return tripDuration;
	}

	public void setTripDuration(int tripDuration) {
		this.tripDuration = tripDuration;
	}

	public int getTicketQuantity() {
		return ticketQuantity;
	}

	public void setTicketQuantity(int ticketQuantity) {
		this.ticketQuantity = ticketQuantity;
	}
	
	public int getNumberOfAdults() {
		return numberOfAdults;
	}

	public void setNumberOfAdults(int numberOfAdults) {
		this.numberOfAdults = numberOfAdults;
	}

	public int getNumberOfChildren() {
		return numberOfChildren;
	}

	public void setNumberOfChildren(int numberOfChildren) {
		this.numberOfChildren = numberOfChildren;
	}

}
