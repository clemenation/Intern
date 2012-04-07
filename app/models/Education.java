package models;

import javax.persistence.*;

import play.db.jpa.Model;

@Entity
public class Education extends Model {
	public String college;
	public double gpa;
	public int soNamHoc;
	
	@ManyToOne
	public Major major;
	
	//Constructor
	public Education(String college) {
		this.college = college;
	}
	
	//Constructor
	public Education(String college, double gpa) {
		this.college = college;
		this.gpa = gpa;
	}
	
	//Constructor
	public Education(String college, double gpa, Major major) {
		this.college = college;
		this.gpa = gpa;
		this.major = major;
	}
	
	//Constructor
	public Education(String college, double gpa, Major major, int soNamHoc) {
		this.college = college;
		this.gpa = gpa;
		this.major = major;
		this.soNamHoc = soNamHoc;
	}
}
