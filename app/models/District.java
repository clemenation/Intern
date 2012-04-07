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
	
	public static void deleteDistrict(District district) {
		district.city.districts.remove(district);
		district.delete();
	}
}
