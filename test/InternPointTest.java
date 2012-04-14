import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class InternPointTest extends UnitTest {

	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
	@Test
	public void pointTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running pointTestWithYml");
		
		// Loading
		Fixtures.loadModels("dungnguyendata.yml");

		// Counting
		assertEquals(1, InternJobSeeker.count());
		assertEquals(1, InternEmployer.count());
		assertEquals(1, InternApplication.count());
		assertEquals(1, InternResume.count());
		assertEquals(3, InternJob.count());
		
		// Getting the resume and job
		InternResume resume = InternResume.all().first();
		InternJob job = InternJob.all().<InternJob>fetch().get(1);
		
		// Create point system
		System.out.println(resume.findJobs());
		System.out.println(job.findResumes());
	}
}
