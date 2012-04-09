package models;

import play.db.jpa.Model;
import java.util.*;
import javax.persistence.*;

@Entity
public class CompanySize extends Model {
	
	
	
	// Properties
	
	// Required
	public String size;
	
	
	
	// Constructor
	
	public CompanySize(String size) {
		this.size = size;
	}
	
	
	
	// Methods
	
	public String toString() {
		return this.size;
	}
	
	
	
	// Static methods
	public static boolean deleteCompanySize(CompanySize companySize) {
		if (companySize == null) {
			System.out.println("ERROR: Deleting null companySize");
			return false;
		}
		
		// Check if there is any employer linking with this companySize
		Employer employer = Employer.find("byCompanySize", companySize).first();
		if (employer != null) {
			System.out.println("ERROR: There is still employers using companySize " + companySize + ", cannot delete yet.");
			return false;
		}
		
		companySize.delete();
		return true;
	}
}
