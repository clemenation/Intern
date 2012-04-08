package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.Model;

@Entity
public class Application extends Model {
	
	
	
	// Properties 
	
	// Required
	@ManyToOne
	public Job job;

	// Required
	@ManyToOne
	public Resume resume;
	
	@ManyToOne
	public JobSeeker jobSeeker;
	
	@ManyToOne
	public Employer employer;
	
	@Lob
	public String message;
	
	
	
	// Constructor
	
	public Application(Job job, Resume resume) {
		this.job = job;
		this.resume = resume;
		this.jobSeeker = resume.owner;
		this.employer = job.owner;
	}
	
	public Application(Job job, Resume resume, String message) {
		this (job, resume);
		this.message = message;
	}
	
	
	
	// Methods
	
	
	
	// Static methods
	
	public static boolean deleteApplication(Application application) {
		if (application == null) {
			System.out.println("ERROR: Deleting null application");
			return false;
		}
		
		System.out.println("ERROR: All applications can never be deleted");		
		
		return false;
	}
}
