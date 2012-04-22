package models;

import java.util.*;

import javax.persistence.*;

import play.data.validation.Check;
import play.db.jpa.*;

@Entity
public class InternEmployer extends InternUser {

	
	
	// Variables
	
	public String companyName;
	public String industry;
	public Blob logo;
		
	@Lob
	public String description;
	
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
	public List<InternJob> jobs;
	
	@OneToMany(mappedBy="employer")
	public List<InternApplication> applications;

	@ManyToOne
	public InternCompanySize companySize;
	
	
	
	// Constructors
	
	public InternEmployer() {
		super("Employer");
		this.jobs = new ArrayList<InternJob>();
		this.applications = new ArrayList<InternApplication>();
	}
	
	public InternEmployer(String email, String password) {
		super(email, password, "Employer");
		this.jobs = new ArrayList<InternJob>();
		this.applications = new ArrayList<InternApplication>();
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
	
	public InternEmployer update(InternEmployer employer) {
		this.email = employer.email;
		this.password = employer.password;
		this.description = employer.description;
		this.companyName = employer.companyName;
		this.companySize = employer.companySize;
		System.out.println(this.companySize.size);
		
		this.industry = employer.industry;
		//this.logo = employer.logo;
		if (this.contactInfo == null) {
			this.contactInfo = employer.contactInfo;
		} else {
			this.contactInfo.update(employer.contactInfo);
		}
		
		return this;
	}
	
	public InternEmployer updateLogo(InternEmployer employer) {
		if (this.logo.exists()) {
			this.logo.getFile().delete();
		}
		this.logo = employer.logo;
		
		return this;
	}
	
	public List<InternPoint> findResumes() {
		// Find all resumes respectively to each job		
		List<List<InternPoint>> pointLists = new ArrayList<List<InternPoint>>();
		for (InternJob job : jobs) {
			pointLists.add(job.findResumes());
		}
		
		// Find the longest resume list		
		int max = 0;
		for (List<InternPoint> points : pointLists) {
			if (max < points.size()) max = points.size();
		}
		
		// Extract distinct satisfied resumes from all resumes
		List<InternResume> finalResumes = new ArrayList<InternResume>();
		List<InternPoint> finalPoints = new ArrayList<InternPoint>();
		for (int i=0; i<max; i++) {
			for (List<InternPoint> points : pointLists) {
				try {
					if (!finalResumes.contains(points.get(i).resume)) {
						finalResumes.add(points.get(i).resume);
						finalPoints.add(points.get(i));
					}
				} catch (IndexOutOfBoundsException e) {
					// Do nothing if go over this resume list size
				}
			}
		}
		
		return finalPoints;
	}
	
	public String toString() {
		if (this.companyName != null && !this.companyName.equals("")) return this.companyName;
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
