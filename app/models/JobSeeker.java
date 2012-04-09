package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class JobSeeker extends User {
		
	
	
	// Variables
	
	@Lob
	public String aboutMe;
	
	public String college;
	public String fullName;
	public Date birthday;
	
	@OneToMany(mappedBy="owner", cascade=CascadeType.ALL)
	public List<Resume> resumes;

	@OneToMany(mappedBy="jobSeeker")
	public List<Application> applications;
	
	
	
	// Constructors
	
	public JobSeeker(String email, String password) {
		super(email, password, "Job Seeker");
		this.resumes = new ArrayList<Resume>();
		this.applications = new ArrayList<Application>();
	}

	public JobSeeker(String email, 
			String password, 
			String aboutMe, 
			String college, 
			String fullName,
			ContactInfo contactInfo) {
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
	
	public JobSeeker addResume(String name) {
		Resume newResume = new Resume(this, name).save();
		this.resumes.add(newResume);
		this.save();
		return this;
	}
	

	public boolean removeResume(Resume resume) {
		if (!this.resumes.contains(resume)) {
			System.out.println("ERROR: JobSeeker " + this + " does not have resume " + resume);
			return false;
		}
		
		return Resume.deleteResume(resume);
	}
	
	public boolean removeResume(int index) {
		return this.removeResume(this.resumes.get(index));
	}
	
	
	
	// Static methods
	
	public static boolean deleteJobSeeker(JobSeeker jobSeeker) {
		if (jobSeeker == null) {
			System.out.println("ERROR: Deleting null jobSeeker");
			return false;
		}
		
		// Check if any application is linking to this jobSeeker
		Application application = Application.find("byJobSeeker", jobSeeker).first();
		if (application != null) {
			System.out.println("ERROR: There are still applications owned by jobSeeker " + jobSeeker + ", cannot delete yet");
			return false;
		}
		
		jobSeeker.delete();
		return true;
	}
	
	public static JobSeeker connect(String email, String password) {
		return find("byEmailAndPassword", email, password).first();
	}
	
}
