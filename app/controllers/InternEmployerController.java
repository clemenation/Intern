package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

@With(Secure.class)
public class InternEmployerController extends Controller {
	
	@Check("Job Seeker")
	public static void index() {
		render();
	}
}
