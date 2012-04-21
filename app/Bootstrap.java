import java.util.*;

import play.*;
import play.jobs.*;
import play.test.*;

import models.*;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {
		// Check if the database is empty
		if(!Play.id.equals("test")) {
			if (InternJobSeeker.count() == 0) {
				List<String> models = new ArrayList<String>();
				models.add("initial-data/city.yml");
				models.add("initial-data/district.yml");
				models.add("initial-data/initial-data.yml");
				models.add("initial-data/major.yml");
				models.add("initial-data/jobseeker.yml");
				models.add("initial-data/employer.yml");
				models.add("initial-data/applications.yml");
				Fixtures.loadModels(models);
			}
		}
	}
}
