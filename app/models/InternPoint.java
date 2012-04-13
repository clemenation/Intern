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
	
	// Required
	public boolean resumeBase;

	public double strongPoint;
	public double weakPoint;
	
	
	
	// Sub classes
	
	public static class InternPointComparator implements Comparator<InternPoint> {
		@Override
		public int compare(InternPoint a, InternPoint b) {
			double aPoint = (a.weakPoint + (1.0/(a.strongPoint + 2.0))) * 1000.0;
			double bPoint = (b.weakPoint + (1.0/(b.strongPoint + 2.0))) * 1000.0;
			return (int)(bPoint - aPoint);
		}
	}



	// Constructor

	public InternPoint(InternResume resume, InternJob job, boolean resumeBase) {
		this.job = job;
		this.resume = resume;
		this.resumeBase = resumeBase;
		this.compare();		// Calculate point immediately
	}



	// Methods

	public void compare() {
		
		System.out.println("Job \"" + this.job + "\" and resume \"" + this.resume + "\" point base on the " + (resumeBase?"resume":"job") + "'s requirement:");
		
		strongPoint = 0;
		weakPoint = 0;
		
		compareAddresses();
		compareEducations();
		compareWorkExperiences();
		compareLanguages();
		
		System.out.println("Total score: +" + strongPoint + " & -" + weakPoint);
	}

	// Compare addresses
	public void compareAddresses() {
		
		double [] points;
		double strong = 0;
		double weak = 0;
		
		InternAddress jobAddress = (this.job.contactInfo != null) ? this.job.contactInfo.address : null;
		InternAddress resumeAddress = (this.resume.contactInfo != null) ? this.resume.contactInfo.address : null;
		
		InternCity jobCity = (jobAddress != null) ? jobAddress.city : null;
		InternCity resumeCity = (resumeAddress != null) ? resumeAddress.city : null;
		
		InternDistrict jobDistrict = (jobAddress != null) ? jobAddress.district: null;
		InternDistrict resumeDistrict = (resumeAddress != null) ? resumeAddress.district : null;

		System.out.println("\tCompare addresses: ");
		
		// Comparing cities
		points = compareEqualStrings(resumeCity, jobCity, resumeBase);
		System.out.println("\t\tCompare cities: +" + points[0] + " -" + points[1]);
		strong += points[0];
		weak += points[1];

		// Comparing districts
		points = compareEqualStrings(resumeDistrict, jobDistrict, resumeBase);
		System.out.println("\t\tCompare districts: +" + points[0] + " -" + points[1]);
		strong += points[0];
		weak += points[1];
		
		System.out.println("\tCompare addresses total: +" + strong + " -" + weak);		// Logging

		strongPoint += strong;
		//weakPoint += weak;	// Not counting point for required address
	}
	
	// Compare educations
	public void compareEducations() {
		
		InternEducation jobEducation = (this.job.requiredEducation != null) ? this.job.requiredEducation : null;
		InternEducation resumeEducation = (this.resume.education != null) ? this.resume.education : null;
		
		int jobStudyYears, resumeStudyYears;
		InternMajor jobMajor, resumeMajor;
		double jobGpa, resumeGpa;
		String jobCollege, resumeCollege;
		
		if (jobEducation != null) {
			jobStudyYears = jobEducation.studyYears;
			jobMajor = jobEducation.major;
			jobGpa = jobEducation.gpa;
			jobCollege = jobEducation.college;
		} else {
			jobGpa = jobStudyYears = 0;
			jobMajor = null;
			jobCollege = null;
		}
		
		if (resumeEducation != null) {
			resumeStudyYears = resumeEducation.studyYears;
			resumeMajor = resumeEducation.major;
			resumeGpa = resumeEducation.gpa;
			resumeCollege = resumeEducation.college;
		} else {
			resumeGpa = resumeStudyYears = 0;
			resumeMajor = null;
			resumeCollege = null;
		}
		
		double [] points;
		double strong = 0;
		double weak = 0;
		
		System.out.println("\tCompare educations: ");
		
		if (resumeBase == true) {
			points = compareNearLower(resumeStudyYears, jobStudyYears);
		} else {
			final int MAX_STUDY_YEARS = 10;		//  constant showing max year of study
			points = compareNearHigher(jobStudyYears, resumeStudyYears, MAX_STUDY_YEARS);
		}
		System.out.println("\t\tCompare study years: +" + points[0] + " -" + points[1]);
		strong += points[0];
		weak += points[1];
		
		points = compareEqualStrings(resumeMajor, jobMajor, resumeBase);
		System.out.println("\t\tCompare majors: +" + points[0] + " -" + points[1]);
		strong += points[0];
		weak += points[1];
		
		if (resumeBase == true) {
			points = compareNearLower(resumeGpa, jobGpa);
		} else {
			points = compareHighest(jobGpa, resumeGpa);
		}
		System.out.println("\t\tCompare GPAs: +" + points[0] + " -" + points[1]);
		strong += points[0];
		weak += points[1];
		
		points = compareEqualStrings(resumeCollege, jobCollege, resumeBase);
		System.out.println("\t\tCompare colleges: +" + points[0] + " -" + points[1]);
		strong += points[0];
		weak += points[1];
			
		System.out.println("\tCompare educations total: +" + strong + " -" + weak);

		strongPoint += strong;
		weakPoint += weak;
	}
	
	// Compare languages
	public void compareLanguages() {
		
		double strong = 0;
		double weak = 0;
		
		List<InternLanguage> jobLanguages = (this.job.requiredLanguages != null) ? this.job.requiredLanguages : null;
		List<InternLanguage> resumeLanguages = (this.resume.languages != null) ? this.resume.languages : null;
		
		if (resumeBase == true) {
			// Resume based
			if (resumeLanguages != null) {
				if (jobLanguages != null) {
					for (InternLanguage language : jobLanguages) {
						if (resumeLanguages.contains(language)) {
							strong++;
						} else {
							weak++;
						}
					}
				} else {
					// Nothing counted
				}
			} else {
				// Nothing counted
			}
		} else {
			// Job based
			if (jobLanguages != null) {
				if (resumeLanguages != null) {
					strong += jobLanguages.size();
					for (InternLanguage language : jobLanguages) {
						if (resumeLanguages.contains(language)) {
							// Nothing counted
						} else {
							weak++;
						}
					}
				} else {
					weak += jobLanguages.size();
				}
			} else {
				if (resumeLanguages != null) {
					strong += resumeLanguages.size();
				} else {
					// Nothing counted
				}
			}
		}
		
		System.out.println("\tCompare languages: +" + strong + " -" + weak);

		strongPoint += strong;
		weakPoint += weak;
	}
	
	// Compare work experience
	public void compareWorkExperiences() {
		double [] points;
		double strong = 0;
		double weak = 0;
		
		int resumeWorkExperience = this.resume.workExperience;
		int jobWorkExperience = this.job.requiredWorkExperience;
		
		if (resumeBase == true) {
			points = compareNearLower((double)resumeWorkExperience, (double)jobWorkExperience);
		} else {
			final int MAX_WORK_EXPERIENCE = 10;		//  constant showing max year of study
			points = compareNearHigher((double)jobWorkExperience, (double)resumeWorkExperience, (double)MAX_WORK_EXPERIENCE);
		}
		strong += points[0];
		weak += points[1];
		
		System.out.println("\tCompare work experiences: +" + strong + " -" + weak);

		strongPoint += strong;
		weakPoint += weak;
	}
	
	
	
	// Static methods
	
	public static double[] compareEqualStrings(Object a, Object b, boolean aBase) {
		double[] points = {0, 0};
		
		if (aBase == true) {
			if (a != null && !a.toString().equals("")) {
				// Base is specified
				if (b != null && !b.toString().equals("")) {
					// Check is specified
					if (b.toString().equals(a.toString())) {
						points[0]++;			// Same as base
					} else {
						points[1]++;			// Different to base
					}
				} else {
					// Check is unspecified
					points[1]++;				// Different to base
				}
			} else {
				// Base is unspecified
				// Don't count point
			}
		} else {
			points = compareEqualStrings(b, a, true);
		}
		
		return points;
	}
	
	public static double[] compareNearLower(double base, double check) {
		// System.out.print("\t\t\tComparing near lower of " + base + " & " + check);		// Logging
		
		double[] points = {0, 0};
		
		if (base != 0) {
			if (check != 0) {
				if (base < check) {
					points[1] = (check - base);
				} else {
					points[0] = check;
				}
			} else {
				points[0] = check;
			}
		} else {
			// Do nothing
		}
		
		// System.out.println(": +" + points[0] + " -" + points[1]);						// Logging
		
		return points;
	}
	
	public static double[] compareNearHigher(double base, double check, double max) {
		double[] points = {0, 0};
		
		if (base != 0) {
			if (check != 0) {
				if (base <= check) {
					points[0] = (max - (check - base));
				} else {
					points[1] = (base - check);
				}
			} else {
				points[1] = base - check;
			}
		} else {
			points[0] = check;
		}
		
		return points;
	}
	
	public static double[] compareHighest(double base, double check) {
		double[] points = {0, 0};
		
		if (base != 0) {
			if (check != 0) {
				if (check >= base) {
					points[0] = (1 + (check - base));
				} else {
					points[1] = (base - check);
				}
			} else {
				points[1] = base - check;
			}
		} else {
			points[0] = check;
		}
		
		return points;
	}
}


