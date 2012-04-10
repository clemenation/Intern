package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class InternUser extends Model {
	
	
	
	// Properties
	
	public String email;
	public String password;
	
	public String userType;
	
	@OneToOne(cascade=CascadeType.ALL)
	public InternContactInfo contactInfo;
	
	
	
	// Constructors
	
	public InternUser(String email, String password, String userType) {
		this.email = email;
		this.password = password;
		this.userType = userType;
	}
	
	public InternUser(String email, 
			String password, 
			String userType,
			InternContactInfo contactInfo) {
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