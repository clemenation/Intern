package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Resume extends Model {
	
	public String name;
	public Date postedAt;
	public int workExperience;
	public Education education;
	public Address preferWorkLocation;
	
	@Lob
	public String description;
	
	@ManyToOne
	public JobSeeker owner;
	
	@OneToOne
	public ContactInfo contactInfo;
	
	// ??????
	//public List<Application> applications;
	
	@ManyToMany
	public List<Language> languages;
	
	public Resume(JobSeeker owner, String name) {
		this.owner = owner;
		this.name = name;
		this.postedAt = new Date();
	}
	
	public Resume(JobSeeker owner, String name, int workExperience, String description, Education education, Address prefer, ContactInfo contactInfo) {
		this(owner, name);
		this.workExperience = workExperience;
		this.description = description;
		this.education = education;
		this.preferWorkLocation = prefer;
		this.contactInfo = contactInfo;
	}
	
	
	
	// Static methods
	public static void deleteResume(Resume resume) {
		if (resume == null) return;
		
		resume.owner.resumes.remove(resume);
		resume.delete();
	}
	
	// Method
	/*
	  public Resume addLanguage() {
	  }
	
	 */
}
