import org.junit.*;

import java.util.*;
import play.test.*;
import models.*;

public class InternApplicationTest extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
	@Test
	public void deleteTest() {
		// Logging
		System.out.println();
		System.out.println("Running deleteTest");
		
		// Loading
		Fixtures.loadModels("dungnguyendata.yml");
		
		// Counting
		assertEquals(1, InternApplication.count());
		
		InternJobSeeker jobSeeker = InternJobSeeker.find("byEmail", "nguyentuandung2991@gmail.com").first();
		assertNotNull(jobSeeker);
		assertEquals(1, jobSeeker.applications.size());
		
		InternEmployer employer = InternEmployer.find("byEmail", "hoangtran@gmail.com").first();
		assertNotNull(employer);
		assertEquals(1, employer.applications.size());
		
		InternApplication application = InternApplication.all().first();
		
		assertNotNull(application);
		assertNotNull(application.jobSeeker);
		assertEquals(application.jobSeeker, jobSeeker);
		assertNotNull(application.employer);
		assertEquals(application.employer, employer);
		
		InternResume resume = application.resume;
		assertEquals(1, resume.applications.size());
		InternJob job = application.job;
		assertEquals(1, job.applications.size());
		
		assertEquals(true, InternApplication.deleteApplication(application));
		assertEquals(0, InternApplication.count());
		assertEquals(0, jobSeeker.applications.size());
		assertEquals(0, employer.applications.size());
		assertEquals(0, resume.applications.size());
		assertEquals(0, job.applications.size());
		
		//==== Deleting a null application
		assertEquals(false, InternApplication.deleteApplication(null));
		
	}

}
