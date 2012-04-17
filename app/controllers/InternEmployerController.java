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
		
		List<InternPoint> finalPoints = employer.findResume();
		
		render(finalPoints);
	}
	
	public static void profile() {
		String username = Security.connected();
		InternEmployer employer = InternEmployer.find("byEmail", username).first();
		List<InternJob> jobs = InternJob.find("owner = ? order by postedAt desc", employer).from(0).fetch(4);
		List<InternApplication> applications = InternApplication.find("employer = ? order by postedAt desc", employer).from(0).fetch(4);
		render(employer, jobs, applications);
	}
	
	public static void viewResume(long resumeId) {
		InternResume resume = InternResume.findById(resumeId);
		if (resume != null) {
			render(resume);
		} else {
			index();
		}
	}
	
	public static void viewJob(long jobId) {
		InternJob job = InternJob.findById(jobId);
		if (job != null) {
			List<InternPoint> jobPoints = job.findResumes();
			render(job, jobPoints);
		} else {
			index();
		}
	}
}
	
