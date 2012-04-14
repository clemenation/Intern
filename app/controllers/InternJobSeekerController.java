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
	
	public static void viewJob(long jobId) {
		InternJob job = InternJob.findById(jobId);
		if (job != null) {
			render(job);
		} else {
			index();
		}
	}
	
	public static void viewResume(long resumeId) {
		InternResume resume = InternResume.findById(resumeId);
		if (resume != null) {
			List<InternPoint> jobPoints = resume.findJobs();
			render(resume, jobPoints);
		} else {
			index();
		}
	}
	
	public static void updateProfileForm() {
		String username = Security.connected();
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", username).first();
		
		render(jobSeeker);
	}
	
	public static void updateProfile() {
		InternJobSeeker editedJobSeeker = params.get("jobSeeker", InternJobSeeker.class);
		
		if (editedJobSeeker.contactInfo != null && editedJobSeeker.contactInfo.contactEmail.equals("")) {
			editedJobSeeker.contactInfo.contactEmail = editedJobSeeker.email;
		}
		
		String username = Security.connected();
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", username).first();
		
		jobSeeker.update(editedJobSeeker);
		validation.valid(jobSeeker);
		
		System.out.println("Hello guys");
		System.out.println(validation.errorsMap());
		
		if (validation.hasErrors()) {
    		params.flash();		// add http parameters to the flash scope
    		validation.keep();	// keep the errors for the next request
    		updateProfileForm();
    	}
		
		jobSeeker.save();
		
		params.put("success", "Update profile successful!");
		
		if (!username.equals(jobSeeker.email)) {
			// If email changed than have to logout and login again
			params.put("username", jobSeeker.email);
			params.flash();
			try {
				Secure.logout();
			} catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			params.flash();
			profile();
		}
	}
	
	public static void addResumeForm() {
		String username = Security.connected();
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", username).first();
		
		render(jobSeeker);
	}
	
	public static void addResume(InternResume resume) {
		String username = Security.connected();
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", username).first();
		
		resume.owner = jobSeeker;
		validation.valid(resume);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			addResumeForm();
		}
		
		resume.save();
		
		params.put("success", "Add new resume successful!");
		params.flash();
		profile();
	}
}
