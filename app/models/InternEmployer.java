package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class InternEmployer extends InternUser {

	
	
	// Variables
	
	public String companyName;
	public String industry;
	
	@Lob
	public String description;
	
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
	public List<InternJob> jobs;
	
	@OneToMany(mappedBy="employer")
	public List<InternApplication> applications;
	
	@ManyToOne
	public InternCompanySize companySize;
	
	
	
	// Constructors
	
	public InternEmployer(String email, String password) {
		super(email, password, "Employer");
		this.jobs = new ArrayList<InternJob>();
		this.applications = new ArrayList<InternApplication>();
	}
	
	public InternEmployer(String email, String password, boolean isAdmin) {
		this(email, password);
		this.isAdmin = true;
	}
	
	public InternEmployer(String email, 
			String password, 
			String companyName, 
			String industry, 
			String description,
			InternContactInfo contactInfo,
			InternCompanySize companySize) {
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
	
	public InternEmployer addJob(String name) {
		InternJob newJob = new InternJob(this, name).save();
		this.jobs.add(newJob);
		this.save();
		return this;
	}
	
	public boolean removeJob(InternJob job) {
		if (!this.jobs.contains(job)) {
			System.out.println("ERROR: employer " + this + " does not have job " + job);
			return false;
		}
		
		return InternJob.deleteJob(job);
	}
	
	public boolean removeJob(int index) {
		return this.removeJob(this.jobs.get(index));
	}
	
		
	// Static methods
	
	public static boolean deleteEmployer(InternEmployer employer) {
		if (employer == null) {
			System.out.println("ERROR: Deleting null employer");
			return false;
		}
		
		// Check if any application is linking to this employer
		InternApplication application = InternApplication.find("byEmployer", employer).first();
		if (application != null) {
			System.out.println("ERROR: There are still applications owned by employer " + employer + ", cannot delete yet");
			return false;
		}
		
		employer.delete();
		return true;
	}
	
	public static InternEmployer connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}
	
	
}
