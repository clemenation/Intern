package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class InternMajor extends Model {
	
	
	
	// Properties
	
	public String name;
	
	
	
	// Constructors
	
	public InternMajor(String name) {
		this.name = name;
	}
	
	
	
	// Methods
	
	public String toString() {
		return this.name;
	}
	
	
	
	// Static methods
	
	public static boolean deleteMajor(InternMajor major) {
		if (major == null) {
			System.out.println("ERROR: Deleting null major");
			return false;
		}
		
		// Check if any education linking with this major
		InternEducation education = InternEducation.find("byMajor", major).first();
		if (education != null) {
			System.out.println("ERROR: There are still educations using major " + major + ", cannot delete yet");
			return false;
		}
		
		major.delete();
		return true;
	}
}
