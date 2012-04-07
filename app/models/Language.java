package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Language extends Model {
	public String language;
	
	//Constructor
	public Language(String lang) {
		this.language = lang;
	}
}
