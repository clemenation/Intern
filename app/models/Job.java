package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Job extends Model {
	
	
	
	// Properties
	
	public Education requiredEducation;
	public String name;
	public Date postedAt;
	public int requiredWorkExperience;
	
	@Lob
	public String description;
	
	@ManyToOne
	public Employer owner;
	
	@OneToMany(mappedBy="job")
	public List<Application> application;
	
	@OneToOne(cascade=CascadeType.ALL)
	public ContactInfo contactInfo;
	
	@ManyToMany
	public List<Language> requiredLanguage;
	
	
	
	//Constructor
	
	public Job(Employer owner, String name) {
		this.owner = owner;
		this.name = name;
		this.postedAt = new Date();
		this.application = new ArrayList<Application>();
	}
	
	public Job(Employer owner, String name, int requiredWorkExperience, String description, ContactInfo contactInfo) {
		this(owner, name);
		this.requiredWorkExperience = requiredWorkExperience;
		this.description = description;
		this.contactInfo = contactInfo;
	}
	
	
	
	// Methods
	
	public String toString() {
		return this.name;
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
