package models;

import play.db.jpa.Model;
import play.data.validation.*;
import java.util.*;
import javax.persistence.*;

@Entity
public class InternCompanySize extends Model {
	
	
	
	// Properties
	
	// Required
	@Required
	public String size;
	
	
	
	// Constructor
	
	public InternCompanySize(String size) {
		this.size = size;
	}
	
	
	
	// Methods	
	
	public String toString() {
		return this.size;
	}
	
	
	
	// Static methods
	public static boolean deleteCompanySize(InternCompanySize companySize) {
		if (companySize == null) {
			System.out.println("ERROR: Deleting null companySize");
			return false;
		}
		
		// Check if there is any employer linking with this companySize
		InternEmployer employer = InternEmployer.find("byCompanySize", companySize).first();
		if (employer != null) {
			System.out.println("ERROR: There is still employers using companySize " + companySize + ", cannot delete yet.");
			return false;
		}
		
		companySize.delete();
		return true;
	}
}
