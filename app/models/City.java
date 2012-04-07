package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class City extends Model {
	
	
	
	// Properties
	
	public String name;
	
	@OneToMany(mappedBy="city", cascade=CascadeType.ALL)
	public List <District> districts;
	
	
	
	// Constructors
	
	public City(String name) {
		this.name = name;
		this.districts = new ArrayList<District>();
	}
	
	
	
	// Methods
	
	public City addDistrict(String name) {
		if (this.districts.contains(new District(this, name))) return this;
		District district = new District (this, name); 
		district.save();
		this.districts.add(district);
		this.save();
		System.out.println("District " + name + " added to city " + this.name);	// Logging
		return this;
	}
	
	public City removeDistrict(District district) {
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
	
	public City removeDistrict(int index) {
		return this.removeDistrict(this.districts.get(index));
	}
}
