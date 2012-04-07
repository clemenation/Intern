import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
	// 1 city, 2 district, 3 address, 3 contactinfo
	// jobSeeker: bob, steve
	// steve has 2 resumes (firstResume, secondResume), bob has a resume (bobResume)
	// employer: tom, has a job (jobTest1)
	// application: SteveToTom, BobToTom
	
	@Test
	public void fullTest() {
		Fixtures.loadModels("data.yml");
		
		// Count things
		assertEquals(2, JobSeeker.count());
		assertEquals(3, Resume.count());
		assertEquals(1, Employer.count());
		assertEquals(1, Job.count());
		
		// Try to connect as job seekers
		JobSeeker bob = JobSeeker.connect("bob@gmail.com", "secret");
		assertNotNull(bob);
		JobSeeker steve = JobSeeker.connect("steve@gmail.com", "jobs");
		assertNotNull(steve);
		assertNull(JobSeeker.connect("jeff@gmail.com", "badpassword"));
		assertNull(Employer.connect("bob@gmail.com", "secret"));
		
		// Find all of Steve's resumes
		List<Resume> steveResumes = Resume.find("owner.email", "steve@gmail.com").fetch();
		assertEquals(2, steveResumes.size());
		assertEquals(2, steve.resumes.size());
		
		
		// Find the most recent resume
		Resume frontResume = Resume.find("order by postedAt desc").first();
		assertNotNull(frontResume);
		assertEquals("My Second Resume", frontResume.name);
		
		// Post a new resume
		bob.addResume("My Third Resume");
		assertEquals(2, bob.resumes.size());
		assertEquals(4, Resume.count());
		
		// Delete steve's 2nd resume (directly)
		Resume.deleteResume(frontResume);
		assertEquals(3, Resume.count());
		assertEquals(1, steve.resumes.size());
		
		// Delete steve's latest resume (indirectly)
		/*steve.removeResume(0);
		assertEquals(0, steve.resumes.size());
		assertEquals(1, Resume.count());
		*/
		
		// Delete a null resume
		/*Resume.deleteResume(null);
		assertEquals(0, steve.resumes.size());
		assertEquals(1, Resume.count());
		*/
		
		Employer tom = Employer.find("byEmail", "tom@gmail.com").first();
		assertNotNull(tom);
		assertEquals(tom.contactInfo.address.address, "BK");
		assertEquals(tom.contactInfo.address.city.name, "Hanoi");
		assertEquals(tom.contactInfo.contactEmail, "tom@gmail.com");		
		assertEquals(tom.description, "We do things");
		
		
	}
	
	
	//Van's test
	@Test
	public void companySizeTest() {
		Fixtures.loadModels("data.yml");

		// CompanySize test
		assertEquals(1, CompanySize.count());
		CompanySize companySize = CompanySize.all().first();
		assertEquals("< 10", companySize.size);
		
	}
	
	@Test
	public void cityTest() {
		// City test
	    // Create a new city
	    City hanoi = new City("Hanoi").save();
	 
	    // Add a first district
	    new District(hanoi, "Dong Da").save();
	    new District(hanoi, "Hai Ba Trung").save();
	 
	    // Retrieve all districts
	    List<District> hanoiDistricts = District.find("byCity", hanoi).fetch();
	 
	    // Tests
	    assertEquals(2, hanoiDistricts.size());
	 
	    District firstDistrict = hanoiDistricts.get(0);
	    assertNotNull(firstDistrict);
	    assertEquals("Dong Da", firstDistrict.name);
	    
	    District secondDistrict = hanoiDistricts.get(1);
	    assertNotNull(secondDistrict);
	    assertEquals("Hai Ba Trung", secondDistrict.name);
	}
	
	@Test
	public void cityTestWithyml() {
		Fixtures.loadModels("data.yml");
		List<District> hanoiDistricts = District.find("byName", "Dong Da").fetch();
		District d = hanoiDistricts.get(0);
		assertEquals("Hanoi", d.city.name);
	}
	
	@Test
	public void AddressTestWithyml() {
		Fixtures.loadModels("data.yml");
		List<Address> address = Address.findAll();
		assertEquals(address.size(), 3);
		Address firstAddress = address.get(1);
		assertEquals("TM", firstAddress.address);
		assertEquals("Hanoi", firstAddress.city.name);
		assertEquals("Hai Ba Trung", firstAddress.district.name);
	}
	
	@Test
	public void jobTestWithyml() {
		//Job test
		Fixtures.loadModels("data.yml");
		List <Job> job = Job.findAll();
		for (Job counter : job) {
			assertEquals("doSomething", counter.name);
			assertEquals("We do things", counter.owner.description);
		}		
		Job firstJob = job.get(0);
		assertEquals("doSomething", firstJob.name);
		assertEquals("We do things", firstJob.owner.description);
		assertEquals("tom@gmail.com", firstJob.contactInfo.contactEmail);
	
	}
	
	@Test
	public void majorTest() {
		new Major("electronic").save();
		new Major("it").save();
		List <Major> major = Major.findAll();
		assertEquals(major.size(), 2);
	}
	
	@Test
	public void majorTestWithyml() {
		Fixtures.loadModels("data.yml");
		List <Major> major = Major.findAll();
		assertEquals(major.size(), 2);
		Major firstMajor = major.get(0);
		assertEquals("computer science", firstMajor.name);
	}
	
	@Test 
	public void educationTestWithyml() {
		Fixtures.loadModels("data.yml");
		List <Education> education = Education.findAll();
		assertEquals(education.size(), 1);
		Education firstEducation = education.get(0);
		assertEquals("BK", firstEducation.college);
		assertEquals(3.4, firstEducation.gpa, 0.1);
		assertEquals("electronic", firstEducation.major.name);
	}
	
	@Test
	public void applicationTestWithyml() {
		Fixtures.loadModels("data.yml");
		List <Application> app = Application.findAll();
		assertEquals(2, app.size());
		Application firstApp = app.get(0);
		assertEquals("tom@gmail.com", firstApp.employer.email);
		assertEquals("doSomething", firstApp.job.name);
		assertEquals("steve@gmail.com", firstApp.jobSeeker.email);
		assertEquals("My First Resume", firstApp.resume.name);
	}
}
