package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class InternEmployerController extends Controller {
	
	@Check("Employer")
	public static void index() {
		String username = Security.connected();
		InternEmployer employer = InternEmployer.find("byEmail", username).first();
		List<InternJob> jobs = InternJob.find("owner = ? order by postedAt desc", employer).from(0).fetch(4);
		List<InternApplication> applications = InternApplication.find("employer = ? order by postedAt desc", employer).from(0).fetch(4);
		render(employer, jobs, applications);
	}
}
