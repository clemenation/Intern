package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Address extends Model {
	public String address;
	
	@ManyToOne
	public City city;
	
	@ManyToOne
	public District district;
	
	public Address(City city) {
		this.city = city;
	}
	
	public Address(String address, City city, District district) {
		this.address = address;
		this.district = district;
		this.city = city;
	}
}
