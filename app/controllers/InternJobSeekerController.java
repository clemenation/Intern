package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class InternJobSeekerController extends Controller {
	
	@Check("Job Seeker")
	public static void index() {
		String username = Security.connected();
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", username).first();
		List<InternResume> resumes = InternResume.find("owner = ? order by postedAt desc", jobSeeker).from(0).fetch(4);
		List<InternApplication> applications = InternApplication.find("jobSeeker = ? order by postedAt desc", jobSeeker).from(0).fetch(4);
		render(jobSeeker, resumes, applications);
	}
}
