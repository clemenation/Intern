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
		this.languages = new ArrayList<Language>();
	}
	
	public Resume(JobSeeker owner, 
			String name, 
			int workExperience, 
			String description, 
			Education education, 
			ContactInfo contactInfo) {
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
	
	public boolean addLanguage(Language language) {
		if (language == null) {
			System.out.println("ERROR: Adding null language");
			return false;
		}
		
		if (Language.find("byLanguage", language.language).first() == null) {
			System.out.println("ERROR: Adding language not in language database");
			return false;
		}
		
		this.languages.add(language);
		language.useCount++;
		language.save();
		
		return true;
	}
	
	public boolean removeLanguage(Language language) {
		if (language == null) {
			System.out.println("ERROR: Adding null language");
			return false;
		}
		
		if (Language.find("byLanguage", language.language).first() == null) {
			System.out.println("ERROR: Adding language not in language database");
			return false;
		}
		
		this.languages.remove(language);
		language.useCount--;
		language.save();		
		
		return true;
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
		
		// Remove languages from resume properly
		for (Language language : resume.languages) {
			resume.removeLanguage(language);
		}
		
		resume.owner.resumes.remove(resume);
		resume.delete();
		
		return true;
	}
}
