package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.Model;

public class Point extends Model {
	
	
	
	// Properties
	
	public Job job;
	public Resume resume;
	public double point;
	
	
	
	// Constructor
	
	public Point(Job job, Resume resume) {
		this.job = job;
		this.resume = resume;
	}
}
