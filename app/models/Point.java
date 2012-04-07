package models;

import java.util.*;
import javax.persistence.*;
import play.db.jpa.Model;

public class Point extends Model {
	//Variables
	public Job job;
	public Resume resume;
	public int point;
	
	//Constructor
	public Point(Job j, Resume r) {
		this.job = j;
		this.resume = r;
	}
}
