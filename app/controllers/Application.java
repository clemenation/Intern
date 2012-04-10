package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	// If logged in check for userType and redirect to proper site
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

}