package models;

import play.db.jpa.Model;
import java.util.*;
import javax.persistence.*;

@Entity
public class CompanySize extends Model {
	
	public String size;
	
	//Constructor
	public CompanySize(String s) {
		this.size = s;
	}
}
