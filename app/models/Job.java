package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Job extends Model {
	
	
	
	// Properties
	
	// Required
	public String name;
	
	// Required
	@ManyToOne
	public Employer owner;
	
	public Date postedAt;
	public int requiredWorkExperience;
	
	@Lob
	public String description;
	
	@OneToMany(mappedBy="job")
	public List<Application> application;
	
	@OneToOne(cascade=CascadeType.ALL)
	public ContactInfo contactInfo;
	
	@ManyToMany
	public List<Language> requiredLanguages;
	
	@OneToOne(cascade=CascadeType.ALL)
	public Education requiredEducation;
	
	
	
	//Constructor
	
	public Job(Employer owner, String name) {
		this.owner = owner;
		this.name = name;
		this.postedAt = new Date();
		this.application = new ArrayList<Application>();
		this.requiredLanguages = new ArrayList<Language>();
	}
	
	public Job(Employer owner, 
			String name, 
			int requiredWorkExperience, 
			String description, 
			ContactInfo contactInfo,
			Education requiredEducation) {
		this(owner, name);
		this.requiredWorkExperience = requiredWorkExperience;
		this.description = description;
		this.contactInfo = contactInfo;
		this.requiredEducation = requiredEducation;
	}
	
	
	
	// Methods
	
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
		
		this.requiredLanguages.add(language);
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
		
		this.requiredLanguages.remove(language);
		language.useCount--;
		language.save();		
		
		return true;
	}
	
	
	
	// Static methods
	
	public static boolean deleteJob(Job job) {
		if (job == null) {
			System.out.println("ERROR: Deleting null job");
			return false;
		}
		
		// Check if any application is linking to this job
		Application application = Application.find("byJob", job).first();
		if (application != null) {
			System.out.println("ERROR: There are still applications owned by job " + job + ", cannot delete yet");
			return false;
		}
		
		// Removing job from its owner's job list
		job.owner.jobs.remove(job);
		
		job.delete();
		return true;
	}
}
