package models;

import javax.persistence.*;

import play.db.jpa.Model;

@Entity
public class Education extends Model {



	// Properties

	//Required
	public int studyYears;

	public double gpa;
	public String college;

	@ManyToOne
	public Major major;



	// Constructors

	public Education(int studyYears) {
		this.studyYears = studyYears;
	}

	public Education(int studyYears,
			String college, 
			double gpa, 
			Major major) {
		this.studyYears = studyYears;
		this.college = college;
		this.gpa = gpa;
		this.major = major;
	}
}