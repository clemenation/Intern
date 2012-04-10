package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class InternJobSeeker extends InternUser {
		
	
	
	// Properties
	
	@Lob
	public String aboutMe;
	
	public String college;
	public String fullName;
	
	@InPast
	public Date birthday;
	
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
	public List<InternResume> resumes;

	@OneToMany(mappedBy="jobSeeker")
	public List<InternApplication> applications;
	
	
	
	// Constructors
	
	public InternJobSeeker() {
		super("Job Seeker");
		this.resumes = new ArrayList<InternResume>();
		this.applications = new ArrayList<InternApplication>();
	}
	
	public InternJobSeeker(String email, String password) {
		super(email, password, "Job Seeker");
		this.resumes = new ArrayList<InternResume>();
		this.applications = new ArrayList<InternApplication>();
	}

	public InternJobSeeker(String email, 
			String password, 
			String aboutMe, 
			String college, 
			String fullName,
			InternContactInfo contactInfo) {
		this(email, password);
		this.aboutMe = aboutMe;
		this.college = college;
		this.fullName = fullName;
		this.contactInfo = contactInfo;
	}
	
	
	
	// Methods
	
	public String toString() {
		return this.email;
	}
	
	public InternJobSeeker addResume(String name) {
		InternResume newResume = new InternResume(this, name).save();
		this.resumes.add(newResume);
		this.save();
		return this;
	}
	

	public boolean removeResume(InternResume resume) {
		if (!this.resumes.contains(resume)) {
			System.out.println("ERROR: JobSeeker " + this + " does not have resume " + resume);
			return false;
		}
		
		return InternResume.deleteResume(resume);
	}
	
	public boolean removeResume(int index) {
		return this.removeResume(this.resumes.get(index));
	}
	
	
	
	// Static methods
	
	public static boolean deleteJobSeeker(InternJobSeeker jobSeeker) {
		if (jobSeeker == null) {
			System.out.println("ERROR: Deleting null jobSeeker");
			return false;
		}
		
		// Check if any application is linking to this jobSeeker
		InternApplication application = InternApplication.find("byJobSeeker", jobSeeker).first();
		if (application != null) {
			System.out.println("ERROR: There are still applications owned by jobSeeker " + jobSeeker + ", cannot delete yet");
			return false;
		}
		
		jobSeeker.delete();
		return true;
	}
	
	public static InternJobSeeker connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}
	
}
