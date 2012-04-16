package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class InternResume extends Model {
	
	
	
	// Properties
	
	// Required
	@Required
	public String name;
	
	// Required
	@Required
	@ManyToOne
	public InternJobSeeker owner;
	
	@Required
	public Date postedAt;
	
	@Min(0)
	@Max(10)
	public int workExperience;
	
	@OneToOne(cascade=CascadeType.ALL)
	public InternEducation education;
	
	@Lob
	public String description;
	
	@OneToOne(cascade=CascadeType.ALL)
	@Valid
	@Required
	public InternContactInfo contactInfo;
	
	@OneToMany(mappedBy="resume")
	public List<InternApplication> applications;
	
	@ManyToMany
	public List<InternLanguage> languages;
	
	
	
	// Constructors
	
	public InternResume() {
		this.postedAt = new Date();
		this.applications = new ArrayList<InternApplication>();
		this.languages = new ArrayList<InternLanguage>();
		this.education = new InternEducation();
		this.contactInfo = new InternContactInfo("");
	}
	
	public InternResume(InternJobSeeker owner, String name) {
		this();
		
		this.owner = owner;
		this.name = name;
		this.contactInfo.update(owner.contactInfo);
	}
	
	public InternResume(InternJobSeeker owner, 
			String name, 
			int workExperience, 
			String description, 
			InternEducation education, 
			InternContactInfo contactInfo) {
		this(owner, name);
		this.workExperience = workExperience;
		this.description = description;
		this.education.update(education);
		this.contactInfo.update(owner.contactInfo);
	}
	
	
	
	// Method
	
	public InternResume update(InternResume resume) {
		this.name = resume.name;
		this.workExperience = resume.workExperience;
		this.education.update(resume.education);
		this.description = resume.description;
		this.contactInfo.update(resume.contactInfo);
		this.languages = resume.languages;
		
		return this;
	}
	
	public List<InternPoint> findJobs() {
		List<InternJob> jobs = InternJob.all().fetch();		// Getting all jobs from database
		
		List<InternPoint> points = new ArrayList<InternPoint>();
		
		for (InternJob job : jobs) {
			points.add(new InternPoint(this, job, true));		// Calculate point for all jobs
		}
		
		Collections.sort(points, new InternPoint.InternPointComparator());	// Sort by point
		Collections.reverse(points);
		
		return points;
	}
	
	public String toString() {
		return this.name;
	}
	
	public boolean addLanguage(InternLanguage language) {
		if (language == null) {
			System.out.println("ERROR: Adding null language");
			return false;
		}
		
		if (InternLanguage.find("byLanguage", language.language).first() == null) {
			System.out.println("ERROR: Adding language not in language database");
			return false;
		}
		
		this.languages.add(language);
		this.save();
		language.useCount++;
		language.save();
		
		return true;
	}
	
	public boolean removeLanguage(InternLanguage language) {
		if (language == null) {
			System.out.println("ERROR: Adding null language");
			return false;
		}
		
		if (InternLanguage.find("byLanguage", language.language).first() == null) {
			System.out.println("ERROR: Adding language not in language database");
			return false;
		}
		
		this.languages.remove(language);
		this.save();
		language.useCount--;
		language.save();		
		
		return true;
	}
	
	
	
	// Static method
	
	public static boolean deleteResume(InternResume resume) {
		if (resume == null) {
			System.out.println("ERROR: Deleting null resume");
			return false;
		}
		
		// Check if any application is linking to this resume
		InternApplication application = InternApplication.find("byResume", resume).first();
		if (application != null) {
			System.out.println("ERROR: There are still applications owned by resume " + resume + ", cannot delete yet");
			return false;
		}
		
		// Remove languages from resume properly
		for (InternLanguage language : resume.languages) {
			resume.removeLanguage(language);
		}
		
		resume.owner.resumes.remove(resume);
		resume.delete();
		
		return true;
	}
}
