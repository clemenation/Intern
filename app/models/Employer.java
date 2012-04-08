package models;

import java.util.*;

import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Employer extends Model {

	// Variables
	
	public String email;
	public String password;
	
	public String companyName;
	public String industry;
	
	@Lob
	public String description;
	
	@OneToOne(cascade=CascadeType.ALL)
	public ContactInfo contactInfo;
	
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
	public List<Job> jobs;
	
	@OneToMany(mappedBy="employer")
	public List<Application> applications;
	
	@ManyToOne
	public CompanySize companySize;
	
	
	
	// Constructors
	
	public Employer(String email, String password) {
		this.email = email;
		this.password = password;
		this.jobs = new ArrayList<Job>();
		this.applications = new ArrayList<Application>();
	}
	
	public Employer(String email, 
			String password, 
			String companyName, 
			String industry, 
			String description,
			ContactInfo contactInfo,
			CompanySize companySize) {
		this(email, password);
		this.companyName = companyName;
		this.industry = industry;
		this.description = description;
		this.contactInfo = contactInfo;
		this.companySize = companySize;
	}
	
	
	
	// Methods
	
	public String toString() {
		return this.email;
	}
	
	public Employer addJob(String name) {
		Job newJob = new Job(this, name).save();
		this.jobs.add(newJob);
		this.save();
		return this;
	}
	
	public boolean removeJob(Job job) {
		if (!this.jobs.contains(job)) {
			System.out.println("ERROR: employer " + this + " does not have job " + job);
			return false;
		}
		
		return Job.deleteJob(job);
	}
	
	public boolean removeJob(int index) {
		return this.removeJob(this.jobs.get(index));
	}
	
	
	
	// Static methods
	
	public static boolean deleteEmployer(Employer employer) {
		if (employer == null) {
			System.out.println("ERROR: Deleting null employer");
			return false;
		}
		
		// Check if any application is linking to this employer
		Application application = Application.find("byEmployer", employer).first();
		if (application != null) {
			System.out.println("ERROR: There are still applications owned by employer " + employer + ", cannot delete yet");
			return false;
		}
		
		employer.delete();
		return true;
	}
	
	public static Employer connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}
	
	
}
