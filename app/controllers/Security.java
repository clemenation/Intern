package controllers;

import models.*;

public class Security extends Secure.Security {

	static boolean authenticate(String username, String password) {
		
		return (InternJobSeeker.connect(username, password) != null) || (InternEmployer.connect(username, password) != null);
		
	}
	
	static boolean check(String profile) {
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", connected()).first();
		InternEmployer employer = InternEmployer.find("byEmail", connected()).first();
		
		if ("admin".equals(profile)) {
			return ((jobSeeker != null) && (jobSeeker.isAdmin) ||
					(employer != null) && (employer.isAdmin));
		}
		
		return ((jobSeeker != null) && (jobSeeker.userType.equals(profile)) ||
				(employer != null) && (employer.userType.equals(profile)));
	}
	
	static void onDisconnected() {
		Application.index();
	}
	
	static void onAuthenticated() {
		String userType="";
		
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", connected()).first();
		InternEmployer employer = InternEmployer.find("byEmail", connected()).first();
		
		if (((jobSeeker != null) && (jobSeeker.isAdmin) ||
				(employer != null) && (employer.isAdmin)) == true) {
			session.put("isAdmin", true);
		} else {
			session.put("isAdmin", false);
		}
		
		if (jobSeeker != null) userType = "Job Seeker";
		if (employer != null) userType = "Employer";
		
		session.put("userType", userType);
	}
	
}
