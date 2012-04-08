package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class ContactInfo extends Model {
	
	
	
	// Properties
	
	// Required
	public String contactEmail;
	
	public String mobile;
	public String phone;
	
	@OneToOne(mappedBy="contactInfo", cascade=CascadeType.ALL)
	public Address address;
	
	
	
	// Constructors
	
	public ContactInfo(String email) {
		this.contactEmail = email;
	}
	
	public ContactInfo(String email,
			String mobile,
			String phone,
			Address address) {
		this.contactEmail = email;
		this.mobile = mobile;
		this.phone = phone;
		this.address = address;
	}
	
	
	
	// Methods
	
}
