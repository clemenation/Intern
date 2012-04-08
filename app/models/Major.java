package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Major extends Model {
	
	
	
	// Properties
	
	public String name;
	
	
	
	// Constructors
	
	public Major(String name) {
		this.name = name;
	}
	
	
	
	// Methods
	
	public String toString() {
		return this.name;
	}
	
	
	
	// Static methods
	
	public static boolean deleteMajor(Major major) {
		if (major == null) {
			System.out.println("ERROR: Deleting null major");
			return false;
		}
		
		// Check if any education linking with this major
		Education education = Education.find("byMajor", major).first();
		if (education != null) {
			System.out.println("ERROR: There are still educations using major " + major + ", cannot delete yet");
			return false;
		}
		
		major.delete();
		return true;
	}
}
