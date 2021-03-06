package models;

import play.db.jpa.*;
import play.data.validation.*;
import java.util.*;
import javax.persistence.*;

import com.google.gson.annotations.Expose;

@Entity
public class InternDistrict extends GenericModel {
	
	
	
	// Properties
	
	@Expose
	@Id
	@GeneratedValue
	public long id;
	
	// Required
	@Expose
	@Required
	public String name;
	
	// Required
	@Required
	@ManyToOne	
	public InternCity city;
	

	
	// Constructors
	public InternDistrict(InternCity c, String n) {
		this.city = c;
		this.name = n;
	}
	
	
	
	// Methods
	public String toString() {
		return this.name + ", " + this.city;
	}
	
	
	
	// Static methods
	
	public static boolean deleteDistrict(InternDistrict district) {
		if (district == null) {
			System.out.println("ERROR: Deleting null district");
			return false;
		}
		
		// Check if any Address have this District
		InternAddress address = InternAddress.find("byDistrict", district).first();
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
