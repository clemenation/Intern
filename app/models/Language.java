package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@Entity
public class Language extends Model {
	
	
	
	// Properties
	
	// Required
	public String language;
	
	public int useCount;
	
	
	
	// Constructors
	
	public Language(String language) {
		this.language = language;
	}
	
	
		
	// Methods
	
	public String toString() {
		return this.language;
	}
	
	
	
	// Static methods
	
	public static boolean deleteLanguage(Language language) {
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
