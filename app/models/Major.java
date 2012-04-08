package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Major extends Model {
	
	
	
	// Properties
	
	public String name;
	
	
	
	// Constructors
	
	public Major(String name) {
		this.name = name;
	}
}
