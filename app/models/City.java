package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class City extends Model {
	
	// Properties
	public String name;
	
	
	// Methods
	public City(String name) {
		this.name = name;
	}
}
