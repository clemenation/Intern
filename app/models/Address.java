package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Address extends Model {
	
	
	
	// Properties

	// Required
	@ManyToOne
	public City city;

	// Required
	@OneToOne
	public ContactInfo contactInfo;

	public String address;

	@ManyToOne
	public District district;

	
	
	
	
	// Constructors
	
	public Address(ContactInfo contactInfo, City city) {
		this.contactInfo = contactInfo;
		this.city = city;
	}
	
	public Address(ContactInfo contactInfo, String address, City city, District district) {
		this(contactInfo, city);
		
		this.address = address;
		this.district = district;
	}
	
	
	
	// Methods
	
	public String toString() {
		return this.city.name;
	}
	
	
	
	// Static methods
	
	public static boolean deleteAddress(Address address) {
		if (address == null) {
			System.out.println("ERROR: Deleting null address");
			return false;
		}
		
		// Removing this address from its contactInfo
		address.contactInfo.address = null;
		address.contactInfo.save();
		
		address.delete();
		return true;
	}
}
