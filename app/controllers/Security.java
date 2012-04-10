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
	
}
