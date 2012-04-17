package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@Check("Employer")
@With(Secure.class)
public class InternEmployerController extends Controller {
	
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
	
	public static void updateProfileForm() {
		String username = Security.connected();
		InternEmployer employer = InternEmployer.find("byEmail", username).first();
		
		render(employer);
	}
	
	public static void updateProfile() {
		InternEmployer editedEmployer = params.get("employer", InternEmployer.class);
		
		if (editedEmployer.contactInfo != null && editedEmployer.contactInfo.contactEmail.equals("")) {
			editedEmployer.contactInfo.contactEmail = editedEmployer.email;
		}
		
		String username = Security.connected();
		InternEmployer employer = InternEmployer.find("byEmail", username).first();
		
		employer.update(editedEmployer);
		validation.valid(employer);
		
		System.out.println(validation.errorsMap());
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			updateProfileForm();
		}
		
		employer.save();
		
		params.put("success", "Update profile successfully");
		
		if (!username.equals(employer.email)) {
			// If email changed that have to logout and login again
			params.put("username", employer.email);
			params.flash();
			try {
				Secure.logout();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else {
			params.flash();
			profile();
		}
	}
	
	public static void addJobForm() {
		String username = Security.connected();
		InternEmployer employer = InternEmployer.find("byEmail", username).first();
		
		render(employer);
	}
	
	public static void addJob(InternJob job) {
		String username = Security.connected();
		InternEmployer employer = InternEmployer.find("byEmail", username).first();
		
		job.owner = employer;
		validation.valid(job);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			addJobForm();
		}
		
		job.save();
		
		params.put("success", "Add new job successful!");
		params.flash();
		profile();
	}
	
	public static void jobs(int page) {
		String username = Security.connected();
		InternEmployer employer = InternEmployer.find("byEmail", username).first();
		
		List<InternJob> jobs = InternJob.find("owner = ? order by postedAt desc", employer).fetch();
		
		render(jobs);
	}
}
	
