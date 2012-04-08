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
	
	public String toString() {
		return this.contactEmail;
	}
	
	
	
	// Static methods
	
	public static boolean deleteContactInfo(ContactInfo contactInfo) {
		if (contactInfo == null) {
			System.out.println("ERROR: Deleting null contactInfo");
			return false;
		}
		
		// Check if any jobSeeker linking to this contactInfo
		JobSeeker jobSeeker = JobSeeker.find("byContactInfo", contactInfo).first();
		if (jobSeeker != null) {
			System.out.println("ERROR: jobSeeker " + jobSeeker + " is linking with contactInfo " + contactInfo + ", cannot delete yet.");
			return false;
		}
		
		// Check if any employer linking to this contactInfo
		Employer employer = Employer.find("byContactInfo", contactInfo).first();
		if (employer != null) {
			System.out.println("ERROR: employer " + employer + " is linking with contactInfo " + contactInfo + ", cannot delete yet.");
			return false;
		}

		// Check if any resume linking to this contactInfo
		Resume resume = Resume.find("byContactInfo", contactInfo).first();
		if (resume != null) {
			System.out.println("ERROR: resume " + resume + " is linking with contactInfo " + contactInfo + ", cannot delete yet.");
			return false;
		}
		
		// Check if any job linking to this contactInfo
		Job job = Job.find("byContactInfo", contactInfo).first();
		if (job != null) {
			System.out.println("ERROR: job " + job + " is linking with contactInfo " + contactInfo + ", cannot delete yet.");
			return false;
		}
		
		contactInfo.delete();
		return true;
	}
}
