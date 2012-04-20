package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class InternJob extends Model {
	
	
	
	// Properties
	
	// Required
	@Required
	public String name;
	
	// Required
	@Required
	@ManyToOne
	public InternEmployer owner;
	
	@Required
	public Date postedAt;
	
	@Min(0)
	@Max(200)
	public int requiredWorkExperience;
	
	@OneToOne(cascade=CascadeType.ALL)
	public InternEducation requiredEducation;
	
	@Lob
	public String description;
	
	@OneToOne(cascade=CascadeType.ALL)
	@Valid
	@Required
	public InternContactInfo contactInfo;
	
	@OneToMany(mappedBy="job")
	public List<InternApplication> applications;
	
	@ManyToMany
	public List<InternLanguage> requiredLanguages;
	

	
	
	
	//Constructor
	
	public InternJob() {
		this.postedAt = new Date();
		this.applications = new ArrayList<InternApplication>();
		this.requiredLanguages = new ArrayList<InternLanguage>();
		this.requiredEducation = new InternEducation();
		this.contactInfo = new InternContactInfo("");
	}
	
	public InternJob(InternEmployer owner, String name) {
		this();
		
		this.owner = owner;
		this.name = name;
		this.contactInfo.update(owner.contactInfo);
	}
	
	public InternJob(InternEmployer owner, 
			String name, 
			int requiredWorkExperience, 
			String description, 
			InternContactInfo contactInfo,
			InternEducation requiredEducation) {
		this(owner, name);
		this.requiredWorkExperience = requiredWorkExperience;
		this.description = description;
		this.contactInfo.update(owner.contactInfo);
		this.requiredEducation.update(requiredEducation);
	}
	
	
	
	// Methods
	
	public InternJob update(InternJob job) {
		this.name = job.name;
		this.requiredWorkExperience = job.requiredWorkExperience;
		this.requiredEducation.update(job.requiredEducation);
		this.description = job.description;
		this.contactInfo.update(job.contactInfo);
		this.requiredLanguages = job.requiredLanguages;
	
		return this;
	}

	public List<InternPoint> findResumesOfJobSeeker(InternJobSeeker jobSeeker, boolean notApplied) {
		List<InternResume> resumes = new ArrayList<InternResume>(jobSeeker.resumes);
		
		if (notApplied == true) {
			for (InternApplication application : jobSeeker.applications) {
				resumes.remove(application.resume);
			}
		}
		
		return pointsFromResumes(resumes);
	}

	public List<InternPoint> findResumes() {
		List<InternResume> resumes = InternResume.all().fetch();	// Getting all resumes from database
		
		return pointsFromResumes(resumes);
	}
	
	public List<InternPoint> pointsFromResumes(List<InternResume> resumes) {
		List<InternPoint> points = new ArrayList<InternPoint>();

		for (InternResume resume : resumes) {
			points.add(new InternPoint(resume, this, false));		// Calculate point for all resumes
		}

		Collections.sort(points, new InternPoint.InternPointComparator());
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
		
		this.requiredLanguages.add(language);
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
		
		this.requiredLanguages.remove(language);
		language.useCount--;
		language.save();		
		
		return true;
	}
	
	
	
	// Static methods
	
	public static boolean deleteJob(InternJob job) {
		if (job == null) {
			System.out.println("ERROR: Deleting null job");
			return false;
		}
		
		// Check if any application is linking to this job
		InternApplication application = InternApplication.find("byJob", job).first();
		if (application != null) {
			System.out.println("ERROR: There are still applications owned by job " + job + ", cannot delete yet");
			return false;
		}

		// Remove languages from job properly
		for (InternLanguage language : job.requiredLanguages) {
			job.removeLanguage(language);
		}
		
		// Removing job from its owner's job list
		job.owner.jobs.remove(job);
		
		job.delete();
		return true;
	}
}
