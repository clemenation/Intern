package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class InternJobSeekerController extends Controller {
	
	@Check("Job Seeker")
	public static void index() {
		String username = Security.connected();
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", username).first();
		render(jobSeeker);
	}
}
