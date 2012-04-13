package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class InternContactInfo extends Model {
	
	
	
	// Properties
	
	// Required
	@Required
	@Email
	public String contactEmail;
	
	@Phone
	public String mobile;
	
	@Phone
	public String phone;
	
	@OneToOne(mappedBy="contactInfo", cascade=CascadeType.ALL)
	public InternAddress address;
	
	
	
	// Constructors
	
	public InternContactInfo(String email) {
		this.contactEmail = email;
	}
	
	public InternContactInfo(String email,
			String mobile,
			String phone,
			InternAddress address) {
		this.contactEmail = email;
		this.mobile = mobile;
		this.phone = phone;
		this.address = address;
	}
	
	
	
	// Methods
	
	public InternContactInfo update(InternContactInfo contactInfo) {
		this.contactEmail = contactInfo.contactEmail;
		this.mobile = contactInfo.mobile;
		this.phone = contactInfo.phone;
		if (this.address == null) {
			this.address = contactInfo.address;
		} else {
			this.address.update(contactInfo.address);
		}
		
		return this;
	}
	
	public String toString() {
		return this.contactEmail;
	}
	
	
	
	// Static methods
	
	public static boolean deleteContactInfo(InternContactInfo contactInfo) {
		if (contactInfo == null) {
			System.out.println("ERROR: Deleting null contactInfo");
			return false;
		}
		
		// Check if any jobSeeker linking to this contactInfo
		InternJobSeeker jobSeeker = InternJobSeeker.find("byContactInfo", contactInfo).first();
		if (jobSeeker != null) {
			System.out.println("ERROR: jobSeeker " + jobSeeker + " is linking with contactInfo " + contactInfo + ", cannot delete yet.");
			return false;
		}
		
		// Check if any employer linking to this contactInfo
		InternEmployer employer = InternEmployer.find("byContactInfo", contactInfo).first();
		if (employer != null) {
			System.out.println("ERROR: employer " + employer + " is linking with contactInfo " + contactInfo + ", cannot delete yet.");
			return false;
		}

		// Check if any resume linking to this contactInfo
		InternResume resume = InternResume.find("byContactInfo", contactInfo).first();
		if (resume != null) {
			System.out.println("ERROR: resume " + resume + " is linking with contactInfo " + contactInfo + ", cannot delete yet.");
			return false;
		}
		
		// Check if any job linking to this contactInfo
		InternJob job = InternJob.find("byContactInfo", contactInfo).first();
		if (job != null) {
			System.out.println("ERROR: job " + job + " is linking with contactInfo " + contactInfo + ", cannot delete yet.");
			return false;
		}
		
		contactInfo.delete();
		return true;
	}
}
