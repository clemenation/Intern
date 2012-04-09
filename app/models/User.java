package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User extends Model {
	
	
	
	// Properties
	
	public String email;
	public String password;
	
	public String userType;
	
	@OneToOne(cascade=CascadeType.ALL)
	public ContactInfo contactInfo;
	
	
	
	// Constructors
	
	public User(String email, String password, String userType) {
		this.email = email;
		this.password = password;
		this.userType = userType;
	}
	
	public User(String email, 
			String password, 
			String userType,
			ContactInfo contactInfo) {
		this.email = email;
		this.password = password;
		this.userType = userType;
		this.contactInfo = contactInfo;
	}
	
	
	
	// Methods
	
	public String toString() {
		return this.email;
	}
	
}