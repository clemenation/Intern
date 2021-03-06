package controllers;

import play.*;
import play.cache.Cache;
import play.data.validation.Validation;
import play.libs.Images;
import play.mvc.*;

import java.util.*;

import models.*;

@Check("Job Seeker")
@With(Secure.class)
public class InternJobSeekerController extends Controller {
	
	private static InternJobSeeker getJobSeeker() {
		String username = Security.connected();
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", username).first();
		
		return jobSeeker;
	}
	
	public static void index(int page) {
		InternJobSeeker jobSeeker = getJobSeeker();
		
		List<InternPoint> points = jobSeeker.findJobs();
		List<InternPoint> finalPoints;
		if (points.size() == 0) {
			finalPoints = points;
			render(finalPoints, page);
		}
		
		int begin = (page-1)*12;
		int end = page*12;
		if (begin < 0 || end < 0) index(1);
		if (begin >= points.size()) {
			index(points.size()/12 + 1);
		} else if (end >= points.size()) {
			end = points.size();
		}
				
		finalPoints = points.subList(begin, end);
		
		int max = points.size()/12 + 1;
		render(finalPoints, page, max);
	}
	
	public static void profile() {
		InternJobSeeker jobSeeker = getJobSeeker();
		render(jobSeeker);
	}
	
	public static void viewJob(long jobId) {
		InternJob job = InternJob.findById(jobId);
		if (job != null) {
			List<InternPoint> points = job.findResumesOfJobSeeker(getJobSeeker(), false);
			List<InternApplication> applications = job.applicationsByJobSeeker(getJobSeeker());
			render(job, points, applications);
		} else {
			index(1);
		}
	}
	
	public static void applyJobForm(long jobId) {
		InternJob job = InternJob.findById(jobId);
		if (job != null) {
			List<InternPoint> points = job.findResumesOfJobSeeker(getJobSeeker(), true);
			render(job, points);
		} else {
			index(1);
		}
	}
	
	public static void applyJob(long jobId) {
		InternApplication application = params.get("application", InternApplication.class);
		
		if (application == null || application.resume==null) {
			System.out.println("ERROR: Application info not entered yet");
			applyJobForm(jobId);
		}
		
		InternJob job = InternJob.findById(jobId);
		
		application.job = job;
		application.jobSeeker = application.resume.owner;
		application.employer = application.job.owner;

		// Check if resume applied
		for (InternApplication checkApplication : application.resume.applications) {
			if (checkApplication.job == job) {
				System.out.println("ERROR: Cannot use this resume to apply for this job again");
				params.put("error", "Cannot use this resume to apply this job again");
				params.flash();
				viewJob(jobId);
			}
		}
		
		/* Not needed
		validation.valid(application);
		
		if (validation.hasErrors()) {
			System.out.println(validation.errorsMap());
			params.flash();
			validation.keep();
			applyJobForm(jobId);
		}
		*/
		
		application.apply();
		
		params.put("success", "Job applied successful!");
		params.flash();
		viewApplication(application.id);
	}
	
	public static void viewApplication(long applicationId) {
		InternApplication application = InternApplication.findById(applicationId);
		InternJobSeeker jobSeeker = getJobSeeker();
		
		if ((application == null) || (!jobSeeker.applications.contains(application))) {
			// If the application is null or not of current user
			System.out.println("ERROR: Cannot view this application");
			profile();
		}
		
		render(application);
	}
	
	public static void viewResume(long resumeId) {
		InternResume resume = InternResume.findById(resumeId);
		if (resume != null) {
			List<InternPoint> points = resume.findJobs();
			List<InternPoint> jobPoints;
			if (points.size() > 4) jobPoints = resume.findJobs().subList(0, 4);
			else jobPoints = points;
			render(resume, jobPoints);
		} else {
			index(1);
		}
	}
	
	public static void jobsForResume(long resumeId, int page) {
		InternResume resume = InternResume.findById(resumeId);
		if (resume != null) {
			List<InternPoint> points = resume.findJobs();
			List<InternPoint> jobPoints = points;
			
			if (points.size() != 0) {
				int begin = (page - 1) * 12;
				int end = page * 12;
				if (begin < 0 || end < 0) jobsForResume(resumeId, 1);
				if (begin >= points.size()) {
					jobsForResume(resumeId, points.size() / 12 + 1);
				} else if (end >= points.size()) {
					end = points.size();
				}
				jobPoints = points.subList(begin, end);
			}
			
			int max = points.size() / 12 + 1;
			render(resume, jobPoints, page, max);
		} else {
			index(1);
		}
	}
	
	public static void applicationsForResume(long resumeId, int page) {
		InternResume resume = InternResume.findById(resumeId);
		if (resume != null) {
			int max = resume.applications.size()/12 + 1;
			if (page < 0) applicationsForResume(resumeId, 1);
			if (page > max) applicationsForResume(resumeId, max);
			
			List<InternApplication> applications = InternApplication.find("resume = ? order by postedAt desc", resume).fetch(page, 12);
			render(resume, applications, page, max);
		} else {
			index(1);
		}
	}
	
	public static void updateProfileForm() {
		InternJobSeeker jobSeeker = getJobSeeker();
		List<InternCity> cities = InternCity.all().fetch();
		List<InternDistrict> districts;
		if (Cache.get("cityId") != null) {
			long cityId = ((Long)Cache.get("cityId")).longValue();
			districts = ((InternCity)InternCity.findById(cityId)).districts;
		}
		else if (jobSeeker.contactInfo.address.city != null) districts = jobSeeker.contactInfo.address.city.districts;
		else districts = cities.get(0).districts;
		render(jobSeeker, districts);
	}
	
	public static void updateProfilePhoto() {
		InternJobSeeker editedJobSeeker = params.get("jobSeeker", InternJobSeeker.class);
		
		if (editedJobSeeker.photo != null) {
			Images.resize(editedJobSeeker.photo.getFile(), editedJobSeeker.photo.getFile(), 160, 240, true);
		}
		
		InternJobSeeker jobSeeker = getJobSeeker();
		
		jobSeeker.updatePhoto(editedJobSeeker);
		jobSeeker.save();
		
		updateProfileForm();
	}
	
	public static void updateProfile() {
		InternJobSeeker editedJobSeeker = params.get("jobSeeker", InternJobSeeker.class);
		
		if (editedJobSeeker.contactInfo.contactEmail.equals("")) {
			editedJobSeeker.contactInfo.contactEmail = editedJobSeeker.email;
		}
		
		if (editedJobSeeker.photo != null) {
			Images.resize(editedJobSeeker.photo.getFile(), editedJobSeeker.photo.getFile(), 160, 240, true);
		}
		
		String username = Security.connected();
		InternJobSeeker jobSeeker = getJobSeeker();
		
		jobSeeker.update(editedJobSeeker);
		validation.valid(jobSeeker);
		
		if (validation.hasErrors()) {
			System.out.println(validation.errorsMap());
    		params.flash();		// add http parameters to the flash scope
    		validation.keep();	// keep the errors for the next request
    		Cache.set("cityId", jobSeeker.contactInfo.address.city.id);
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
				e.printStackTrace();
			}
		} else {
			params.flash();
			profile();
		}
	}
	
	public static void addResumeForm() {
		InternJobSeeker jobSeeker = getJobSeeker();
		
		List<InternCity> cities = InternCity.all().fetch();
		List<InternDistrict> districts;
		
		if (Cache.get("cityId") != null) {
			long cityId = ((Long)Cache.get("cityId")).longValue();
			districts = ((InternCity)InternCity.findById(cityId)).districts;
			Cache.delete("cityId");
		} else {
			districts = cities.get(0).districts;
		}
		render(jobSeeker, districts);
	}
	
	public static void addResume(InternResume resume, InternAddress address) {
		InternJobSeeker jobSeeker = getJobSeeker();
		if (resume.contactInfo.address == null) resume.contactInfo.address = new InternAddress(resume.contactInfo);
		
		resume.contactInfo.address.update(address);
		resume.owner = jobSeeker;
		validation.valid(resume);
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			Cache.set("cityId", resume.contactInfo.address.city.id);
			addResumeForm();
		}
		
		resume.save();
		
		params.put("success", "Add new resume successful!");
		params.flash();
		viewResume(resume.id);
	}
	
	public static void editResumeForm(long resumeId) {
		InternResume resume = InternResume.findById(resumeId);
		if (resume == null) {
			resumes(1);		// Go to resumes list if resume not found
		}
		
		List<InternCity> cities = InternCity.all().fetch();
		List<InternDistrict> districts;
		
		if (Cache.get("cityId") != null) {
			long cityId = ((Long)Cache.get("cityId")).longValue();
			districts = ((InternCity)InternCity.findById(cityId)).districts;
			Cache.delete("cityId");
		} else if (resume.contactInfo.address.city != null) {
			districts = resume.contactInfo.address.city.districts;
		} else {
			districts = cities.get(0).districts;
		}
		
		render(resume, districts);
	}
	
	public static void editResume(long resumeId) {
		InternResume resume = InternResume.findById(resumeId);
		if (resume == null) {
			resumes(1);		// Go to resumes list if resume not found
		}
		
		InternResume editedResume = params.get("resume", InternResume.class);	// Getting the edited resume from the params
		resume.update(editedResume);		// Update the old resume with new info (not saved yet)
		validation.valid(resume);			// Checking fields for errors
		
		if (validation.hasErrors()) {
			params.flash();
			validation.keep();
			Cache.set("cityId", resume.contactInfo.address.city.id);
			editResumeForm(resumeId);
		}
		
		resume.save();
		
		params.put("success", "Edit resume successful!");
		params.flash();
		
		viewResume(resumeId);
	}
	
	public static void deleteResume(long resumeId) {
		InternResume resume = InternResume.findById(resumeId);
		
		if ((resume == null) || (resume.owner != getJobSeeker())) {
			params.put("error", "Cannot delete this resume");
			params.flash();
			resumes(1);
		}
		
		if (InternResume.deleteResume(resume) == false) {
			params.put("error", "Cannot delete this resume");
			params.flash();
			resumes(1);
		} else {
			params.put("success", "Resume deleted successful");
			params.flash();
			resumes(1);
		}
	}
	
	public static void resumes(int page) {
		InternJobSeeker jobSeeker = getJobSeeker();
		
		if (jobSeeker.resumes.size() != 0) {
			if (page > (jobSeeker.resumes.size()/12+1)) resumes((int) (jobSeeker.resumes.size()/12 + 1));
			if (page < 1) resumes(1);
		}

		List<InternResume> resumes = InternResume.find("owner = ? order by postedAt desc", jobSeeker).fetch(page, 12);
		
		int max = jobSeeker.resumes.size()/12 + 1;
		render(resumes, page, max);
	}
	
	public static void applications(int page) {
		InternJobSeeker jobSeeker = getJobSeeker();
		
		if (jobSeeker.applications.size() != 0) {
			if (page > (jobSeeker.applications.size()/12 + 1)) applications((int) (jobSeeker.applications.size()/12 + 1));
			if (page < 1) applications(1);
		}
		
		List<InternApplication> applications = InternApplication.find("jobSeeker = ? order by postedAt desc", jobSeeker).fetch(page, 12);
		
		int max = jobSeeker.applications.size()/12 + 1;
		render(applications, page, max);		
	}
}
