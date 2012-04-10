package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class InternResume extends Model {
	
	
	
	// Properties
	
	// Required
	@Required
	public String name;
	
	// Required
	@Required
	@ManyToOne
	public InternJobSeeker owner;
	
	public Date postedAt;
	
	@Min(0)
	@Max(200)
	public int workExperience;
	
	@OneToOne(cascade=CascadeType.ALL)
	public InternEducation education;
	
	@Lob
	public String description;
	
	@OneToOne(cascade=CascadeType.ALL)
	public InternContactInfo contactInfo;
	
	@OneToMany(mappedBy="resume")
	public List<InternApplication> applications;
	
	@ManyToMany
	public List<InternLanguage> languages;
	
	
	
	// Constructors
	
	public InternResume(InternJobSeeker owner, String name) {
		this.owner = owner;
		this.name = name;
		this.postedAt = new Date();
		this.applications = new ArrayList<InternApplication>();
		this.languages = new ArrayList<InternLanguage>();
	}
	
	public InternResume(InternJobSeeker owner, 
			String name, 
			int workExperience, 
			String description, 
			InternEducation education, 
			InternContactInfo contactInfo) {
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
	
	public boolean addLanguage(InternLanguage language) {
		if (language == null) {
			System.out.println("ERROR: Adding null language");
			return false;
		}
		
		if (InternLanguage.find("byLanguage", language.language).first() == null) {
			System.out.println("ERROR: Adding language not in language database");
			return false;
		}
		
		this.languages.add(language);
		language.useCount++;
		language.save();
		
		return true;
	}
	
	public boolean removeLanguage(InternLanguage language) {
		if (language == null) {
			System.out.println("ERROR: Adding null language");
			return false;
		}
		
		if (InternLanguage.find("byLanguage", language.language).first() == null) {
			System.out.println("ERROR: Adding language not in language database");
			return false;
		}
		
		this.languages.remove(language);
		language.useCount--;
		language.save();		
		
		return true;
	}
	
	
	
	// Static method
	
	public static boolean deleteResume(InternResume resume) {
		if (resume == null) {
			System.out.println("ERROR: Deleting null resume");
			return false;
		}
		
		// Check if any application is linking to this resume
		InternApplication application = InternApplication.find("byResume", resume).first();
		if (application != null) {
			System.out.println("ERROR: There are still applications owned by resume " + resume + ", cannot delete yet");
			return false;
		}
		
		// Remove languages from resume properly
		for (InternLanguage language : resume.languages) {
			resume.removeLanguage(language);
		}
		
		resume.owner.resumes.remove(resume);
		resume.delete();
		
		return true;
	}
}
