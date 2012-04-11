package controllers;

import play.*;
import play.mvc.*;
import play.data.validation.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	// If logged in check for userType and redirect to proper page
    	String userType = session.get("userType");
    	if (userType != null) {
    		if (userType.equals("Job Seeker")) {
    			InternJobSeekerController.index();
    		} else if (userType.equals("Employer")) {
    			InternEmployerController.index();
    		}
    	}
    	
    	// If not logged in then display login page
    	try {
    		Secure.login();
    	} catch (Throwable e) {

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
    	if (jobSeeker.contactInfo != null) {
    		jobSeeker.contactInfo.contactEmail = jobSeeker.email;
    	}
    	
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
    
    public static void registerEmployer() {
    	
    }

}