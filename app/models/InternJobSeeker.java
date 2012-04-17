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
	public Blob photo;
	
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
			Date birthday,
			InternContactInfo contactInfo) {
		this(email, password);
		this.aboutMe = aboutMe;
		this.college = college;
		this.fullName = fullName;
		this.contactInfo = contactInfo;
		this.birthday = birthday;
	}
	
	
	
	// Methods
	
	public InternJobSeeker update(InternJobSeeker jobSeeker) {
		this.email = jobSeeker.email;
		this.password = jobSeeker.password;
		this.aboutMe = jobSeeker.aboutMe;
		this.college = jobSeeker.college;
		this.fullName = jobSeeker.fullName;
		this.birthday = jobSeeker.birthday;
		this.photo = jobSeeker.photo;
		if (this.contactInfo == null) {
			this.contactInfo = jobSeeker.contactInfo;
		} else {
			this.contactInfo.update(jobSeeker.contactInfo);
		}
		
		return this;
	}
	
	public List<InternPoint> findJobs() {
		List<List<InternPoint>> pointLists = new ArrayList<List<InternPoint>>();
		for (InternResume resume : resumes) {
			pointLists.add(resume.findJobs());
		}
		
		int max = 0;		// Getting longest job list
		for (List<InternPoint> points : pointLists) {
			if (max < points.size()) max = points.size();
		}
		
		List<InternJob> finalJobs = new ArrayList<InternJob>();
		List<InternPoint> finalPoints = new ArrayList<InternPoint>();
		for (int i=0; i<max; i++) {
			for (List<InternPoint> points : pointLists) {
				try {
					if (!finalJobs.contains(points.get(i).job)) {
						finalJobs.add(points.get(i).job);
						finalPoints.add(points.get(i));
					}
				} catch (IndexOutOfBoundsException e) {
					// Do nothing if go over this jobs list size
				}
			}
		}
		
		return finalPoints;
	}
	
	public String toString() {
		if (this.fullName != null && !this.fullName.equals("")) return this.fullName;
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
