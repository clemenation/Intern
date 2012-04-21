package controllers;

import play.*;
import play.data.validation.Validation;
import play.libs.Images;
import play.mvc.*;

import java.util.*;

import models.*;

@Check("Employer")
@With(Secure.class)
public class InternEmployerController extends Controller {
	
	private static InternEmployer getEmployer() {
		String username = Security.connected();
		InternEmployer employer = InternEmployer.find("byEmail", username).first();
		
		return employer;
	}
	
	public static void index() {
		InternEmployer employer = getEmployer();
		
		List<InternPoint> finalPoints = employer.findResume();
		
		render(finalPoints);
	}
	
	public static void profile() {
		InternEmployer employer = getEmployer();
		List<InternJob> jobs = InternJob.find("owner = ? order by postedAt desc", employer).from(0).fetch(4);
		List<InternApplication> applications = InternApplication.find("employer = ? order by postedAt desc", employer).from(0).fetch(4);
		render(employer, jobs, applications);
	}
	
	public static void viewResume(long resumeId) {
		InternResume resume = InternResume.findById(resumeId);
		if (resume != null) {
			List<InternPoint> points = resume.findJobsOfEmployer(getEmployer(), false);
			render(resume, points);
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
		InternEmployer employer = getEmployer();
		List<InternCity> cities = InternCity.all().fetch();
		List<InternDistrict> districts;
		if (employer.contactInfo.address.city != null) districts = employer.contactInfo.address.city.districts;
		else districts = cities.get(0).districts;
		render(employer, cities, districts);
	}
	
	public static void updateProfile() {
		InternEmployer editedEmployer = params.get("employer", InternEmployer.class);
		
		if (editedEmployer.contactInfo.contactEmail.equals("")) {
			editedEmployer.contactInfo.contactEmail = editedEmployer.email;
		}
		
		if (editedEmployer.logo != null) {
			Images.resize(editedEmployer.logo.getFile(), editedEmployer.logo.getFile(), 160, 240, true);
		}
		
		String username = Security.connected();
		InternEmployer employer = getEmployer();
		
		employer.update(editedEmployer);
		validation.valid(employer);
		
		if (validation.hasErrors()) {
			System.out.println(validation.errorsMap());
			params.flash();
			validation.keep();
			updateProfileForm();
		}
		
		employer.save();
		
		params.put("success", "Update profile successful!");
		
		if (!username.equals(employer.email)) {
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
		InternEmployer employer = getEmployer();
		List<InternCity> cities = InternCity.all().fetch();
		render(employer, cities);
	}
	
	public static void addJob(InternJob job, InternAddress address) {
		InternEmployer employer = getEmployer();
		if (job.contactInfo.address == null) job.contactInfo.address = new InternAddress(job.contactInfo);
		
		job.contactInfo.address.update(address);
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
		viewJob(job.id);
	}

	public static void editJobForm(long jobId) {
		InternJob job = InternJob.findById(jobId);
		if (job == null) {
			jobs(1);
		}
		
		List<InternCity> cities = InternCity.all().fetch();
		InternEmployer employer = job.owner;
		render(job, employer, cities);
	}
	
	public static void editJob(long jobId) {
		InternJob job = InternJob.findById(jobId);
		if (job == null) {
			jobs(1);
		}
		
		InternJob editedJob = params.get("job", InternJob.class);
		job.update(editedJob);
		validation.valid(job);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			editJobForm(jobId);
		}
		
		job.save();
		
		params.put("success", "Edit job successful!");
		params.flash();
		
		viewJob(jobId);
	}
	
	public static void jobs(int page) {
		InternEmployer employer = getEmployer();
		
		List<InternJob> jobs = InternJob.find("owner = ? order by postedAt desc", employer).fetch();
		
		render(jobs);
	}
	
	public static void logo() {
		InternEmployer employer = getEmployer();
		
		response.setContentTypeIfNotSet(employer.logo.type());
		java.io.InputStream binaryData = employer.logo.get();
		
		if (binaryData != null) renderBinary(binaryData);
	}
}
	
