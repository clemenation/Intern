package models;

import play.db.jpa.Model;
import java.util.*;
import javax.persistence.*;

@Entity
public class District extends Model {
	
	
	
	// Properties
	
	// Required
	public String name;
	
	// Required
	@ManyToOne
	public City city;
	

	
	// Constructors
	public District(City c, String n) {
		this.city = c;
		this.name = n;
	}
	
	
	
	// Static methods
	
	public static boolean deleteDistrict(District district) {
		if (district == null) {
			System.out.println("ERROR: Deleting null district");
			return false;
		}
		
		// Check if any Address have this District
		Address address = Address.find("byDistrict", district).first();
		if (address != null) {
			System.out.println("ERROR: There is still an address that have city " + district.name + " in it so we cannot delete now.");
			return false;
		}
		
		// Removing this district from its city
		district.city.districts.remove(district);
		district.city.save();
		
		district.delete();
		return true;
	}
}
