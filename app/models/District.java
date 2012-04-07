package models;

import play.db.jpa.Model;
import java.util.*;
import javax.persistence.*;

@Entity
public class District extends Model {
	public String name;
	
	@ManyToOne
	public City city;
	
	//Constructor
	public District(City c, String n) {
		this.city = c;
		this.name = n;
	}
}
