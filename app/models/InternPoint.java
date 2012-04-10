package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.Model;

public class InternPoint extends Model {
	
	
	
	// Properties
	
	public InternJob job;
	public InternResume resume;
	public double point;
	
	
	
	// Constructor
	
	public InternPoint(InternJob job, InternResume resume) {
		this.job = job;
		this.resume = resume;
	}
}
