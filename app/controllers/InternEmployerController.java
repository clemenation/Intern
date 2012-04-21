package controllers;

import play.*;
import play.cache.Cache;
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
		render(employer);
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
		if (Cache.get("cityId") != null) {
			long cityId = ((Long)Cache.get("cityId")).longValue();
			districts = ((InternCity)InternCity.findById(cityId)).districts;
		}
		else if (employer.contactInfo.address.city != null) districts = employer.contactInfo.address.city.districts;
		else districts = cities.get(0).districts;
		render(employer, districts);
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
    		Cache.set("cityId", employer.contactInfo.address.city.id);
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
		List<InternDistrict> districts;
		
		if (Cache.get("cityId") != null) {
			long cityId = ((Long)Cache.get("cityId")).longValue();
			districts = ((InternCity)InternCity.findById(cityId)).districts;
			Cache.delete("cityId");
		} else {
			districts = cities.get(0).districts;
		}
		render(employer, districts);
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
			Cache.set("cityID", job.contactInfo.address.city.id);
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
		List<InternDistrict> districts;
		
		if (Cache.get("cityId") != null) {
			long cityId = ((Long)Cache.get("cityId")).longValue();
			districts = ((InternCity)InternCity.findById(cityId)).districts;
			Cache.delete("cityId");
		} else {
			districts = cities.get(0).districts;
		}
		
		InternEmployer employer = job.owner;
		
		render(job, employer, districts);
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
			Cache.set("cityId", job.contactInfo.address.city.id);
			editJobForm(jobId);
		}
		
		job.save();
		
		params.put("success", "Edit job successful!");
		params.flash();
		
		viewJob(jobId);
	}
	
	public static void deleteJob(long jobId) {
		InternJob job = InternJob.findById(jobId);
		
		if ((job == null) || (job.owner != getEmployer())) {
			params.put("error", "Cannot delete this job");
			params.flash();
			jobs(1);
		}
		
		if (InternJob.deleteJob(job) == false) {
			params.put("error", "Cannot delete this job");
			params.flash();
			jobs(1);
		} else {
			params.put("success", "Job deleted successful");
			params.flash();
			jobs(1);
		}
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
	
