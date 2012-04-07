package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class City extends Model {
	
	// Properties
	public String name;
	
	@OneToMany(mappedBy="city", cascade=CascadeType.ALL)
	List <District> districts;
	
	// Methods
	public City(String name) {
		this.name = name;
		this.districts = new ArrayList<District>();
	}
	
	/*
	public City addDistrict(String name) {
		District newdis = new District (name); 
		newdis.save();
		districts.add(newdis);
		this.save();
		return this;
	}
	*/
}
