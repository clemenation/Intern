package models;

import java.util.*;
import javax.persistence.*;

import play.db.jpa.*;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class User extends Model {
	public String email;
	public String password;
	
	public String userType;
	
	@OneToOne
	public ContactInfo contactInfo;
}