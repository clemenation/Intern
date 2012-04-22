package controllers;

import play.*;
import play.mvc.*;
import play.data.validation.*;
import play.db.jpa.*;

import java.util.*;

import com.google.gson.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	// Check for superadmin
    	try {
    		if (Security.connected().equals("admin")) {
    			CRUD.index();
    		}
    	} catch (NullPointerException e) {
    		// Do nothing
    	}
    	
    	// If logged in check for userType and redirect to proper page
    	String userType = session.get("userType");
    	if (userType != null) {
    		if (userType.equals("Job Seeker")) {
    			InternJobSeekerController.index(1);
    		} else if (userType.equals("Employer")) {
    			InternEmployerController.index(1);
    		}
    	}
    	
    	// If not logged in then display login page
    	try {
			Secure.login();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    public static void registerJobSeekerForm() {
    	// Check if user already logged in
    	if (Security.connected() == null) {
    		render();
    	} else {
    		// If logged in then they cannot register, redirect to index now
    		index();
    	}
    }
    
    public static void registerJobSeeker(InternJobSeeker jobSeeker) {
    	jobSeeker.contactInfo.contactEmail = jobSeeker.email;
    	jobSeeker.contactInfo.address = new InternAddress(jobSeeker.contactInfo);
    	
    	validation.valid(jobSeeker);
    	
    	if (validation.hasErrors()) {
    		params.flash();		// add http parameters to the flash scope
    		validation.keep();	// keep the errors for the next request
    		registerJobSeekerForm();
    	}
    	
    	jobSeeker.save();	// Save to database
    	
    	params.put("success", "Registration successful! Login with your created account.");
    	params.put("username", jobSeeker.email);
    	params.flash();
    	
    	try {
    		Secure.login();
    	} catch (Throwable e) {

    	}
    }
    
    public static void registerEmployerForm() {
    	// Check if user already logged in
    	if (Security.connected() == null) {
    		render();
    	} else {
    		// If logged in then they cannot register, redirect to index now
    		index();
    	}
    }
    
    public static void registerEmployer(InternEmployer employer) {
    	employer.contactInfo.contactEmail = employer.email;
    	employer.contactInfo.address = new InternAddress(employer.contactInfo);
    	
    	validation.valid(employer);
    	System.out.println(validation.errorsMap());
    	
    	if (validation.hasErrors()) {
    		params.flash();		// add http parameters to the flash scope
    		validation.keep();	// keep the errors for the next request
    		registerEmployerForm();
    	}
    	
    	employer.save();	// save to database
    	
    	params.put("success", "Registration successful! Login with your created account.");
    	params.put("username", employer.email);
    	params.flash();
    	
    	try {
    		Secure.login();
    	} catch (Throwable e) {
    		
    	}
    }
    
    public static void districtsForCity(Long cityId) {
    	InternCity city = InternCity.findById(cityId);
    	if (city == null) {
    		return;
    	}
    	
    	List<InternDistrict> districts = city.districts;
    	
    	Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    	renderJSON(gson.toJson(districts));
    	//renderText(districts.toString());
    }
    
    public static void jobSeekerPhoto(Long jobSeekerId) {
    	InternJobSeeker jobSeeker = InternJobSeeker.findById(jobSeekerId);
    	
		response.setContentTypeIfNotSet(jobSeeker.photo.type());
		java.io.InputStream binaryData = jobSeeker.photo.get();

		if (binaryData != null) renderBinary(binaryData);
    }
    
    public static void employerPhoto(Long employerId) {
    	InternEmployer employer = InternEmployer.findById(employerId);
    	
    	response.setContentTypeIfNotSet(employer.logo.type());
		java.io.InputStream binaryData = employer.logo.get();

		if (binaryData != null) renderBinary(binaryData);
    }

}
