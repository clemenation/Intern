package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class InternAddress extends Model {
	
	
	
	// Properties

	// Required
	@ManyToOne
	public InternCity city;

	// Required
	@Required
	@OneToOne
	public InternContactInfo contactInfo;

	public String address;

	@ManyToOne
	public InternDistrict district;

	
	
	
	
	// Constructors
	
	public InternAddress(InternContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}
	
	public InternAddress(InternContactInfo contactInfo, InternCity city) {
		this.contactInfo = contactInfo;
		this.city = city;
	}
	
	public InternAddress(InternContactInfo contactInfo, String address, InternCity city, InternDistrict district) {
		this(contactInfo, city);
		
		this.address = address;
		this.district = district;
	}
	
	
	
	// Methods
	
	public InternAddress update(InternAddress address) {
		if (address != null) {
			this.address = address.address;
			this.city = address.city;
			this.district = address.district;
		}
		
		return this;
	}
	
	public String toString() {
		if (this.city != null) return this.city.name;
		return this.contactInfo.toString();
	}
	
	
	
	// Static methods
	
	public static boolean deleteAddress(InternAddress address) {
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
