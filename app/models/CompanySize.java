package models;

import play.db.jpa.Model;
import java.util.*;
import javax.persistence.*;

@Entity
public class CompanySize extends Model {
	
	
	
	// Properties
	
	// Required
	public String size;
	
	
	
	// Constructor
	
	public CompanySize(String s) {
		this.size = s;
	}
}
