package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.Model;
import play.data.validation.*;

@Entity
public class InternApplication extends Model {
	
	
	
	// Properties 
	
	// Required
	@Required
	@ManyToOne
	public InternJob job;

	// Required
	@Required
	@ManyToOne
	public InternResume resume;
	
	// Required
	@Required
	public Date postedAt;
	
	@ManyToOne
	public InternJobSeeker jobSeeker;
	
	@ManyToOne
	public InternEmployer employer;
	
	@Lob
	@MaxSize(10000)
	public String message;
	
	
	
	// Constructor
	
	public InternApplication() {
		this.postedAt = new Date();
	}
	
	public InternApplication(InternJob job, InternResume resume) {
		this();
		
		this.job = job;
		this.resume = resume;
		this.jobSeeker = resume.owner;
		this.employer = job.owner;
	}
	
	public InternApplication(InternJob job, InternResume resume, String message) {
		this (job, resume);
		this.message = message;
	}
	
	
	
	// Methods
	
	public String toString() {
		return (this.jobSeeker + "'s " + this.resume + " to " + this.employer + "'s " + this.job);
	}
	
	public boolean apply() {
		if (job == null || resume == null || jobSeeker==null || employer == null) {
			System.out.println("ERROR: Applying null job and resume");
			return false;
		}
		
		job.applications.add(this);
		job.save();
		resume.applications.add(this);
		resume.save();
		jobSeeker.applications.add(this);
		jobSeeker.save();
		employer.applications.add(this);
		employer.save();
		
		this.save();
		
		return true;
	}
	
	
	
	// Static methods
	
	public static boolean deleteApplication(InternApplication application) {
		if (application == null) {
			System.out.println("ERROR: Deleting null application");
			return false;
		}
		
		System.out.println("ERROR: All applications can never be deleted");		
		
		return false;
	}
}
