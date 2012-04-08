package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Resume extends Model {
	
	
	
	// Properties
	
	// Required
	public String name;
	
	// Required
	@ManyToOne
	public JobSeeker owner;
	
	public Date postedAt;
	public int workExperience;
	
	@OneToOne(cascade=CascadeType.ALL)
	public Education education;
	
	@Lob
	public String description;
	
	@OneToOne(cascade=CascadeType.ALL)
	public ContactInfo contactInfo;
	
	@OneToMany(mappedBy="resume")
	public List<Application> applications;
	
	@ManyToMany
	public List<Language> languages;
	
	
	
	// Constructors
	
	public Resume(JobSeeker owner, String name) {
		this.owner = owner;
		this.name = name;
		this.postedAt = new Date();
		this.applications = new ArrayList<Application>();
	}
	
	public Resume(JobSeeker owner, String name, int workExperience, String description, Education education, ContactInfo contactInfo) {
		this(owner, name);
		this.workExperience = workExperience;
		this.description = description;
		this.education = education;
		this.contactInfo = contactInfo;
	}
	
	
	
	// Method
	
	public String toString() {
		return this.name;
	}
	
	
	
	// Static method
	
	public static boolean deleteResume(Resume resume) {
		if (resume == null) {
			System.out.println("ERROR: Deleting null resume");
			return false;
		}
		
		// Check if any application is linking to this resume
		Application application = Application.find("byResume", resume).first();
		if (application != null) {
			System.out.println("ERROR: There are still applications owned by resume " + resume + ", cannot delete yet");
			return false;
		}
		
		resume.owner.resumes.remove(resume);
		resume.delete();
		
		return true;
	}
}
