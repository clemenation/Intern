package controllers;

import play.*;
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
		
	public static void index() {
		InternJobSeeker jobSeeker = getJobSeeker();
		
		List<InternPoint> finalPoints = jobSeeker.findJobs();
		
		render(finalPoints);
	}
	
	public static void profile() {
		InternJobSeeker jobSeeker = getJobSeeker();
		List<InternResume> resumes = InternResume.find("owner = ? order by postedAt desc", jobSeeker).from(0).fetch(4);
		List<InternApplication> applications = InternApplication.find("jobSeeker = ? order by postedAt desc", jobSeeker).from(0).fetch(4);
		render(jobSeeker, resumes, applications);
	}
	
	public static void viewJob(long jobId) {
		InternJob job = InternJob.findById(jobId);
		if (job != null) {
			List<InternPoint> points = job.findResumesOfJobSeeker(getJobSeeker());
			render(job, points);
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
		InternJobSeeker jobSeeker = getJobSeeker();
		render(jobSeeker);
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
		render(jobSeeker);
	}
	
	public static void addResume(InternResume resume) {
		InternJobSeeker jobSeeker = getJobSeeker();
		
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
		viewResume(resume.id);
	}
	
	public static void editResumeForm(long resumeId) {
		InternResume resume = InternResume.findById(resumeId);
		if (resume == null) {
			resumes(1);		// Go to resumes list if resume not found
		}
		
		render(resume);
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
			editResumeForm(resumeId);
		}
		
		resume.save();
		
		params.put("success", "Edit resume successful!");
		params.flash();
		
		viewResume(resumeId);
	}
	
	public static void resumes(int page) {
		InternJobSeeker jobSeeker = getJobSeeker();
		
		List<InternResume> resumes = InternResume.find("owner = ? order by postedAt desc", jobSeeker).fetch();
		
		render(resumes);
	}
	
	public static void photo() {
		InternJobSeeker jobSeeker = getJobSeeker();

		response.setContentTypeIfNotSet(jobSeeker.photo.type());
		java.io.InputStream binaryData = jobSeeker.photo.get();

		if (binaryData != null) renderBinary(binaryData);
	}
	
}
