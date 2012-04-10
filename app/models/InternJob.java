package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class InternJob extends Model {
	
	
	
	// Properties
	
	// Required
	@Required
	public String name;
	
	// Required
	@Required
	@ManyToOne
	public InternEmployer owner;
	
	public Date postedAt;
	
	@Min(0)
	@Max(200)
	public int requiredWorkExperience;
	
	@Lob
	public String description;
	
	@OneToMany(mappedBy="job")
	public List<InternApplication> applications;
	
	@OneToOne(cascade=CascadeType.ALL)
	public InternContactInfo contactInfo;
	
	@ManyToMany
	public List<InternLanguage> requiredLanguages;
	
	@OneToOne(cascade=CascadeType.ALL)
	public InternEducation requiredEducation;
	
	
	
	//Constructor
	
	public InternJob(InternEmployer owner, String name) {
		this.owner = owner;
		this.name = name;
		this.postedAt = new Date();
		this.applications = new ArrayList<InternApplication>();
		this.requiredLanguages = new ArrayList<InternLanguage>();
	}
	
	public InternJob(InternEmployer owner, 
			String name, 
			int requiredWorkExperience, 
			String description, 
			InternContactInfo contactInfo,
			InternEducation requiredEducation) {
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
	
	public boolean addLanguage(InternLanguage language) {
		if (language == null) {
			System.out.println("ERROR: Adding null language");
			return false;
		}
		
		if (InternLanguage.find("byLanguage", language.language).first() == null) {
			System.out.println("ERROR: Adding language not in language database");
			return false;
		}
		
		this.requiredLanguages.add(language);
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
		
		this.requiredLanguages.remove(language);
		language.useCount--;
		language.save();		
		
		return true;
	}
	
	
	
	// Static methods
	
	public static boolean deleteJob(InternJob job) {
		if (job == null) {
			System.out.println("ERROR: Deleting null job");
			return false;
		}
		
		// Check if any application is linking to this job
		InternApplication application = InternApplication.find("byJob", job).first();
		if (application != null) {
			System.out.println("ERROR: There are still applications owned by job " + job + ", cannot delete yet");
			return false;
		}

		// Remove languages from job properly
		for (InternLanguage language : job.requiredLanguages) {
			job.removeLanguage(language);
		}
		
		// Removing job from its owner's job list
		job.owner.jobs.remove(job);
		
		job.delete();
		return true;
	}
}
