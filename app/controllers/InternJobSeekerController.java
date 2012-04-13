package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@Check("Job Seeker")
@With(Secure.class)
public class InternJobSeekerController extends Controller {
		
	public static void index() {
		String username = Security.connected();
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", username).first();
		
		List<InternPoint> finalPoints = jobSeeker.findJobs();
		
		render(finalPoints);
	}
	
	public static void profile() {
		String username = Security.connected();
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", username).first();
		List<InternResume> resumes = InternResume.find("owner = ? order by postedAt desc", jobSeeker).from(0).fetch(4);
		List<InternApplication> applications = InternApplication.find("jobSeeker = ? order by postedAt desc", jobSeeker).from(0).fetch(4);
		render(jobSeeker, resumes, applications);
	}
}
