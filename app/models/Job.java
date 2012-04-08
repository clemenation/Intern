package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Job extends Model {
	
	public Education requiredEducation;
	public String name;
	public Date postedAt;
	public int requiredWorkExperience;
	
	@Lob
	public String description;
	
	@ManyToOne
	public Employer owner;
	
	@OneToMany(mappedBy="job", cascade=CascadeType.ALL)
	public List<Application> application;
	
	@OneToOne(cascade=CascadeType.ALL)
	public ContactInfo contactInfo;
	
	@ManyToMany
	public List<Language> requiredLanguage;
	
	//Constructor
	public Job(Employer owner, String name) {
		this.owner = owner;
		this.name = name;
		this.postedAt = new Date();
	}
	
	public Job(Employer owner, String name, int requiredWorkExperience, String description, ContactInfo contactInfo) {
		this(owner, name);
		this.requiredWorkExperience = requiredWorkExperience;
		this.description = description;
		this.contactInfo = contactInfo;
	}
	
	// Methods
	
	public String toString() {
		return this.name;
	}
}
