package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Address extends Model {
	public String address;
	
	@ManyToOne
	public City city;
	
	public Address(City city) {
		this.city = city;
	}
	
	public Address(String address, City city) {
		this.address = address;
		this.city = city;
	}
}
