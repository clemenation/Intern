package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Major extends Model {
	public String name;
	
	//Constructor
	public Major(String name) {
		this.name = name;
	}
}
