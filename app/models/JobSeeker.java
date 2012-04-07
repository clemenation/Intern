package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class JobSeeker extends Model {
		
	// Variables
	
	public String email;
	public String password;
	
	@Lob
	public String aboutMe;
	
	public String college;
	public String fullName;
	public Date birthday;
	
	public ContactInfo contactInfo;
	
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
	public List<Resume> resumes;

	@OneToMany(mappedBy="jobSeeker", cascade=CascadeType.ALL)
	public List<Application> applications;
	
	
	// Constructors
	
	public JobSeeker(String email, String password) {
		this.email = email;
		this.password = password;
		this.resumes = new ArrayList<Resume>();
		this.applications = new ArrayList<Application>();
	}

	public JobSeeker(String email, 
			String password, 
			String aboutMe, 
			String college, 
			String fullName,
			ContactInfo contactInfo) {
		this(email, password);
		this.aboutMe = aboutMe;
		this.college = college;
		this.fullName = fullName;
		this.contactInfo = contactInfo;
	}
	
	
	
	// Methods
	
	public JobSeeker addResume(String name) {
		Resume newResume = new Resume(this, name).save();
		this.resumes.add(newResume);
		this.save();
		return this;
	}
	
	// Problem in here when try to run resume.delete()
	/*
	public JobSeeker removeResume(int index) {
		Resume resume = this.resumes.get(index);
		resume.delete();
		System.out.println("Resume " + resume.name + " removed from JobSeeker " + this.email);	// Logging
		this.resumes.remove(index);
		
		this.save();
		return this;
	}
	*/
	
	/*
	 public JobSeeker addApplication() {
	 }
	 */
	
	
	
	// Static methods
	
	public static JobSeeker connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}
	
	
}
