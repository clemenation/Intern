package models;

import java.util.*;

import javax.persistence.*;

import play.db.jpa.*;
import play.data.validation.*;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class InternUser extends Model {
	
	
	
	// Properties
	
	// Required
	@Required
	@Email
	@CheckWith(EmailUniqueCheck.class)
	public String email;
	
	// Required
	@Required
	@MinSize(6)
	public String password;
	
	// Required
	@Required
	public String userType;
	
	// Required
	@Required
	public boolean isAdmin;
	
	@Valid
	@OneToOne(cascade=CascadeType.ALL)
	public InternContactInfo contactInfo;
	
	
	
	// Static class

	public static class EmailUniqueCheck extends Check {
		
		public boolean checkJobSeeker(InternUser jobSeeker, String email) {
			
			boolean checkOk = true;

			List<InternJobSeeker> existingJobSeekers = InternJobSeeker.find("byEmail", email).fetch(2);
			checkOk = (existingJobSeekers.size() == 0);
			if (existingJobSeekers.size() == 1) {
				if (jobSeeker.id != null) {
					InternJobSeeker existingJobSeeker = existingJobSeekers.get(0);
					if (existingJobSeeker.getId().equals(jobSeeker.id)) {
						checkOk = true;
					}
				}
			}

			return checkOk;
		}
		
		public boolean checkEmployer(InternUser employer, String email) {
			
			boolean checkOk = true;

			List<InternEmployer> existingEmployers = InternEmployer.find("byEmail", email).fetch(2);
			checkOk = (existingEmployers.size() == 0);
			if (existingEmployers.size() == 1) {
				if (employer.id != null) {
					InternEmployer existingEmployer = existingEmployers.get(0);
					if (existingEmployer.getId().equals(employer.id)) {
						checkOk = true;
					}
				}
			}

			return checkOk;
		}

		public boolean isSatisfied(Object user, Object email) {
			setMessage("validation.emailUsed", (String)email);
			
			return (checkJobSeeker((InternUser)user, (String)email) && checkEmployer((InternUser)user, (String)email));
		}

	}
	
	
	
	// Constructors
	
	public InternUser(String userType) {
		this.userType = userType;
		this.isAdmin = false;
	}
	
	public InternUser(String email, String password, String userType) {
		this.email = email;
		this.password = password;
		this.userType = userType;
		this.isAdmin = false;
	}
	
	public InternUser(String email, String password, String userType, boolean isAdmin) {
		this(email, password, userType);
		this.isAdmin = true;
	}
	
	public InternUser(String email, 
			String password, 
			String userType,
			InternContactInfo contactInfo) {
		this(email, password, userType);
		this.contactInfo = contactInfo;
	}
	
	
	
	// Methods
	
	public String toString() {
		return this.email;
	}
	
	
	
	// Static methods
	
}