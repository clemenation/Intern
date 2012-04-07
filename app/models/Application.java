package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.Model;

@Entity
public class Application extends Model {
	
	@Lob
	public String message;
	
	@ManyToOne
	public Employer employer;
	
	@ManyToOne
	public Job job;
	
	@ManyToOne
	public JobSeeker jobSeeker;
	
	@OneToOne
	public Resume resume;
	
	//Constructor
	public Application(Employer employer, Job job, JobSeeker jobseeker, Resume resume) {
		this.employer = employer;
		this.job = job;
		this.jobSeeker = jobseeker;
		this.resume = resume;
	}
	
	public Application(Employer employer, Job job, JobSeeker jobseeker, Resume resume, String message) {
		this (employer, job, jobseeker, resume);
		this.message = message;
	}
}
