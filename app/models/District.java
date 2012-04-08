package models;

import play.db.jpa.Model;
import java.util.*;
import javax.persistence.*;

@Entity
public class District extends Model {
	
	
	
	// Properties
	public String name;
	
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
		
		// Check if any address have this city
		Address address = Address.find("byDistrict", district).first();
		if (address != null) {
			System.out.println("ERROR: There is still an address that have city " + district.name + " in it so we cannot delete now.");
			return false;
		}
		
		district.city.districts.remove(district);
		district.delete();
		return true;
	}
}
