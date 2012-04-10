package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class InternUser extends Model {
	
	
	
	// Properties
	
	// Required
	@Required
	@Email
	public String email;
	
	// Required
	@Required
	public String password;
	
	// Required
	@Required
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