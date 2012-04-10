package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class InternCity extends Model {
	
	
	
	// Properties
	
	// Required
	@Required
	public String name;
	
	@OneToMany(mappedBy="city", cascade=CascadeType.ALL)
	public List <InternDistrict> districts;
	
	
	
	// Constructors
	
	public InternCity(String name) {
		this.name = name;
		this.districts = new ArrayList<InternDistrict>();
	}
	
	
	
	// Methods
	
	public String toString() {
		return this.name;
	}
	
	public InternCity addDistrict(String name) {
		if (this.districts.contains(new InternDistrict(this, name))) return this;
		InternDistrict district = new InternDistrict (this, name); 
		district.save();
		this.districts.add(district);
		this.save();
		System.out.println("District " + name + " added to city " + this.name);	// Logging
		return this;
	}
	
	public InternCity removeDistrict(InternDistrict district) {
		try {
			this.districts.remove(district);
			System.out.println("District " + district.name + " removed from city " + this.name);	// Logging
			district.delete();
		} catch(IllegalArgumentException exception) {
			// If the district is not in this city
			return this;
		}
		
		this.save();
		return this;
	}
	
	public InternCity removeDistrict(int index) {
		return this.removeDistrict(this.districts.get(index));
	}
	
	
	
	// Static methods
	
	public static boolean deleteCity(InternCity city) {
		if (city == null) {
			System.out.println("ERROR: Deleting null city");
			return false;
		}
		
		// Check if any address have this city
		InternAddress address = InternAddress.find("byCity", city).first();
		if (address != null) {
			System.out.println("ERROR: There is still an address that have city " + city.name + " in it so we cannot delete now.");
			return false;
		}
		
		city.delete();
		
		return true;
	}
}
