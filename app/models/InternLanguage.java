package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@Entity
public class InternLanguage extends Model {
	
	
	
	// Properties
	
	// Required
	@Required
	public String language;
	
	@Min(0)
	public int useCount;
	
	
	
	// Constructors
	
	public InternLanguage(String language) {
		this.language = language;
	}
	
	
		
	// Methods
	
	public String toString() {
		return this.language;
	}
	
	
	
	// Static methods
	
	public static boolean deleteLanguage(InternLanguage language) {
		if (language == null) {
			System.out.println("ERROR: Deleting null language");
			return false;
		}
		
		// Check if any resume or job linking with it
		if (language.useCount != 0) {
			System.out.println("ERROR: There are still resumes and jobs linking with language " + language);
			return false;
		}
		
		language.delete();
		return true;
	}
}
