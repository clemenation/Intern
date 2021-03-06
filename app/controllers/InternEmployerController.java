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
	
	public static void index(int page) {
		InternEmployer employer = getEmployer();
		
		List<InternPoint> points = employer.findResumes();
		List<InternPoint> finalPoints;
		if (points.size() == 0) {
			finalPoints = points;
			render(finalPoints, page);
		}
		
		int begin = (page-1)*12;
		int end = page*12;
		if (begin < 0 || end < 0) index(1);
		if (begin >= points.size()) {
			index((points.size()-1)/12 + 1);
		} else if (end >= points.size()) {
			end = points.size();
		}
				
		finalPoints = points.subList(begin, end);
		
		int max = (points.size()-1)/12 + 1;
		render(finalPoints, page, max);
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
			index(1);
		}
	}
	
	public static void viewApplication(long applicationId) {
		InternApplication application = InternApplication.findById(applicationId);
		InternEmployer employer = getEmployer();
		
		if ((application == null) || (!employer.applications.contains(application))) {
			// If the application is null or not of current user
			System.out.println("ERROR: Cannot view this application");
			profile();
		}
		
		render(application);
	}
	
	public static void viewJob(long jobId) {
		InternJob job = InternJob.findById(jobId);
		if (job != null) {
			List<InternPoint> points = job.findResumes();
			List<InternPoint> jobPoints;
			if (points.size() > 4) jobPoints = job.findResumes().subList(0, 4);
			else jobPoints = points;
			render(job, jobPoints);
		} else {
			index(1);
		}
	}
	
	public static void resumesForJob(long jobId, int page) {
		InternJob job = InternJob.findById(jobId);
		if (job != null) {
			List<InternPoint> points = job.findResumes();
			List<InternPoint> resumePoints = points;
			
			if (points.size() != 0) {
				int begin = (page - 1) * 12;
				int end = page * 12;
				if (begin < 0 || end < 0) resumesForJob(jobId, 1);
				if (begin >= points.size()) {
					resumesForJob(jobId, (points.size()-1)/ 12 + 1);
				} else if (end >= points.size()) {
					end = points.size();
				}
				resumePoints = points.subList(begin, end);
			}
			
			int max = (points.size()-1)/ 12 + 1;
			render(job, resumePoints, page, max);
		} else {
			index(1);
		}
	}
	
	public static void applicationsForJob(long jobId, int page) {
		InternJob job = InternJob.findById(jobId);
		if (job != null) {
			int max = (job.applications.size()-1)/12 + 1;
			if (page < 0) applicationsForJob(jobId, 1);
			if (page > max) applicationsForJob(jobId, max);
			
			List<InternApplication> applications = InternApplication.find("job = ? order by postedAt desc", job).fetch(page, 12);
			render(job, applications, page, max);
		} else {
			index(1);
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
	
	public static void updateProfilePhoto() {
		InternEmployer editedEmployer = params.get("employer", InternEmployer.class);
		
		if (editedEmployer.logo != null) {
			Images.resize(editedEmployer.logo.getFile(), editedEmployer.logo.getFile(), 160, 240, true);
		}
		
		InternEmployer employer = getEmployer();
		
		employer.updateLogo(editedEmployer);
		employer.save();
		
		updateProfileForm();
	}
	
	public static void updateProfile() {
		InternEmployer editedEmployer = params.get("employer", InternEmployer.class);
		
		if (editedEmployer.contactInfo.contactEmail.equals("")) {
			editedEmployer.contactInfo.contactEmail = editedEmployer.email;
		}
		
		if (editedEmployer.logo != null) {
			Images.resize(editedEmployer.logo.getFile(), editedEmployer.logo.getFile(), 160, 240, true);
		}
		
		System.out.println("hello" + editedEmployer.companySize.size);
		
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
		} else if (job.contactInfo.address.city != null){
			districts = job.contactInfo.address.city.districts;
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
		
		if (employer.jobs.size() != 0) {
			if (page > ((employer.jobs.size()-1)/12+1)) jobs((int) ((employer.jobs.size()-1)/12 + 1));
			if (page < 1) jobs(1);
		}
		
		List<InternJob> jobs = InternJob.find("owner = ? order by postedAt desc", employer).fetch();
		
		int max = (employer.jobs.size()-1)/12+1;
		render(jobs, page, max);
	}
	
	public static void applications(int page) {
		InternEmployer employer = getEmployer();
		
		if (employer.applications.size() != 0) {
			if (page > ((employer.applications.size()-1)/12+1)) applications((int) ((employer.jobs.size()-1)/12 + 1));
			if (page < 1) applications(1);
		}
		
		List<InternApplication> applications = InternApplication.find("employer = ? order by postedAt desc", employer).fetch(page, 12);
		
		int max = (employer.applications.size()-1)/12 + 1;
		render(applications, page, max);
	}
}
	
