package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.Model;

public class InternPoint extends Model {



	// Properties

	// Required
	public InternJob job;

	// Required
	public InternResume resume;

	public double strongPoint;
	public double weakPoint;



	// Constructor

	public InternPoint(InternResume resume, InternJob job) {
		this.job = job;
		this.resume = resume;
		this.compare();		// Calculate point immediately
	}



	// Methods

	public void compare() {
		
		System.out.println("Job \"" + this.job + "\" and resume \"" + this.resume + "\" point:");
		
		compareAddresses();
		compareEducations();
		compareLanguages();
		compareWorkExperiences();
		
		System.out.println("Total score: +" + strongPoint + " & -" + weakPoint);
	}

	// Compare addresses
	public void compareAddresses() {
		InternAddress requirement = (this.job.contactInfo != null) ? this.job.contactInfo.address : null;
		InternAddress address = (this.resume.contactInfo != null) ? this.resume.contactInfo.address : null;
		
		double [] points;
		double strong = 0;
		double weak = 0;
		
		points = compareEqualStrings((requirement != null) ? requirement.city : null, (address != null) ? address.city : null);
		strong += points[0];
		weak += points[1];
		
		points = compareEqualStrings((requirement != null) ? requirement.district : null, (address != null) ? address.district : null);
		strong += points[0];
		weak += points[1];
		
		System.out.println("Compare addresses: +" + strong + " -" + weak);

		strongPoint += strong;
		//weakPoint += weak;	// Not counting point for required address
	}
	
	// Compare educations
	public void compareEducations() {
		InternEducation requirement = (this.job.requiredEducation != null) ? this.job.requiredEducation : null;
		InternEducation education = (this.resume.education != null) ? this.resume.education : null;
		
		double [] points;
		double strong = 0;
		double weak = 0;
		
		points = compareSizes((requirement != null) ? (double)requirement.studyYears : 0, (education != null) ? (double)education.studyYears : 0);
		strong += points[0];
		weak += points[1];
		
		points = compareEqualStrings((requirement != null) ? requirement.major : null, (education != null) ? education.major : null);
		strong += points[0];
		weak += points[1];
		
		points = compareSizes((requirement != null) ? requirement.gpa : 0, (education != null) ? education.gpa : 0);
		strong += points[0];
		weak += points[1];
		
		points = compareEqualStrings((requirement != null) ? requirement.college : null, (education != null) ? education.college : null);
		strong += points[0];
		weak += points[1];
			
		System.out.println("Compare educations: +" + strong + " -" + weak);

		strongPoint += strong;
		weakPoint += weak;
	}
	
	// Compare languages
	public void compareLanguages() {
		List<InternLanguage> requirement = (this.job.requiredLanguages != null) ? this.job.requiredLanguages : null;
		List<InternLanguage> languages = (this.resume.languages != null) ? this.resume.languages : null;
		
		double strong = 0;
		double weak = 0;
		
		strong += (languages != null) ? languages.size() : 0;
		
		if (requirement != null) {
			if (languages != null) {
				for (InternLanguage language : requirement) {
					if (!languages.contains(language)) weak++;
				}
			} else {
				weak += requirement.size();
			}
		}
		
		System.out.println("Compare languages: +" + strong + " -" + weak);

		strongPoint += strong;
		weakPoint += weak;
	}
	
	// Compare work experience
	public void compareWorkExperiences() {
		double [] points;
		double strong = 0;
		double weak = 0;
		
		points = compareSizes(this.job.requiredWorkExperience, this.resume.workExperience);
		strong += points[0];
		weak += points[1];
		
		System.out.println("Compare work experiences: +" + strong + " -" + weak);

		strongPoint += strong;
		weakPoint += weak;
	}
	
	
	
	// Static methods
	
	public static double[] compareEqualStrings(Object requirement, Object a) {
		double[] points = {0, 0};
		
		if (requirement != null && !requirement.toString().equals("")) {
			if ((a != null && !a.toString().equals("")) && (requirement.toString().equals(a.toString()))){
				points[0]++;
			} else {
				points[1]++;
			}
		}
		
		return points;
	}
	
	public static double[] compareSizes(double requirement, double a) {
		double[] points = {0, 0};
		
		if (a >= requirement) {
			points[0] += (a - requirement);
			if (requirement != 0) points[0]++;
		} else {
			points[1] += requirement - a;
		}
		
		return points;
	}
}
