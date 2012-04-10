package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.Model;

@Entity
public class InternApplication extends Model {
	
	
	
	// Properties 
	
	// Required
	@ManyToOne
	public InternJob job;

	// Required
	@ManyToOne
	public InternResume resume;
	
	@ManyToOne
	public InternJobSeeker jobSeeker;
	
	@ManyToOne
	public InternEmployer employer;
	
	@Lob
	public String message;
	
	
	
	// Constructor
	
	public InternApplication(InternJob job, InternResume resume) {
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
