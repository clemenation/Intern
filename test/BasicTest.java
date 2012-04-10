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
		// Logging
		System.out.println();
		System.out.println("Running fullTest");

		// Load
		Fixtures.loadModels("data.yml");

		// Count things
		assertEquals(2, InternJobSeeker.count());
		assertEquals(4, InternResume.count());
		assertEquals(1, InternEmployer.count());
		assertEquals(1, InternJob.count());

		// Try to connect as user
		InternUser user = InternJobSeeker.connect("bob@gmail.com", "secret");
		assertNotNull(user);
		assertEquals(user.email, "bob@gmail.com");
		assertEquals(user.userType, "Job Seeker");
		
		// Try to connect as job seekers
		InternJobSeeker bob = InternJobSeeker.connect("bob@gmail.com", "secret");
		assertNotNull(bob);
		InternJobSeeker steve = InternJobSeeker.connect("steve@gmail.com", "jobs");
		assertNotNull(steve);
		assertNull(InternJobSeeker.connect("jeff@gmail.com", "badpassword"));
		assertNull(InternEmployer.connect("bob@gmail.com", "secret"));

		// Find all of Steve's resumes
		List<InternResume> steveResumes = InternResume.find("owner.email", "steve@gmail.com").fetch();
		assertEquals(3, steveResumes.size());
		assertEquals(3, steve.resumes.size());


		// Find the most recent resume
		InternResume frontResume = InternResume.find("order by postedAt desc").first();
		assertNotNull(frontResume);
		assertEquals("My Third Resume", frontResume.name);

		// Post a new resume
		bob.addResume("My Fourth Resume");
		assertEquals(2, bob.resumes.size());
		assertEquals(5, InternResume.count());

		/*
		// Delete steve's latest resume
		steveResumes.get(2).delete();
		steve.resumes.remove(0);
		assertEquals(2, steve.resumes.size());
		assertEquals(4, Resume.count());
		assertEquals(2, ContactInfo.count());

		
		// Delete a null resume
		Resume.deleteResume(null);
		assertEquals(0, steve.resumes.size());
		assertEquals(1, Resume.count());
		 */

		InternEmployer tom = InternEmployer.find("byEmail", "tom@gmail.com").first();
		assertNotNull(tom);
		assertEquals(tom.userType, "Employer");
		assertEquals(tom.contactInfo.address.address, "BK");
		assertEquals(tom.contactInfo.address.city.name, "Hanoi");
		assertEquals(tom.contactInfo.contactEmail, "tom@gmail.com");		
		assertEquals(tom.description, "We do things");


	}

	@Test
	public void jobSeekerTest() {
		InternJobSeeker tom = new InternJobSeeker("tom@gmail.com", "secret").save();
		assertNotNull(tom);
		tom.addResume("Tom's 1st resume");
		tom.addResume("Tom's 2nd resume");
		
		// Counting
		assertEquals(1, InternJobSeeker.count());
		assertEquals(2, InternResume.count());
		assertEquals(2, tom.resumes.size());
		
		// Getting tom's resume from Resume table
		InternResume tomResume = InternResume.find("byOwner", tom).first();
		assertNotNull(tomResume);
		
		// Removing 1 resume of tom
		tom.removeResume(0);
		assertEquals(1, InternResume.count());
		assertEquals(1, tom.resumes.size());
		
		// Deleting tom
		InternJobSeeker.deleteJobSeeker(tom);
		assertEquals(0, InternJobSeeker.count());
		assertEquals(0, InternResume.count());
		
	}
	
	@Test
	public void employerTest() {
		InternEmployer tom = new InternEmployer("tom@gmail.com", "secret").save();
		assertNotNull(tom);
		tom.addJob("Tom's 1st job");
		tom.addJob("Tom's 2nd job");
		
		// Counting
		assertEquals(1, InternEmployer.count());
		assertEquals(2, InternJob.count());
		assertEquals(2, tom.jobs.size());
		
		// Getting tom's job from Job table
		InternJob tomJob = InternJob.find("byOwner", tom).first();
		assertNotNull(tomJob);
		
		// Removing 1 job of tom
		tom.removeJob(0);
		assertEquals(1, InternJob.count());
		assertEquals(1, tom.jobs.size());
		
		// Deleting tom
		InternEmployer.deleteEmployer(tom);
		assertEquals(0, InternEmployer.count());
		assertEquals(0, InternJob.count());
		
	}

	@Test
	public void companySizeTest() {
		// Logging
		System.out.println();
		System.out.println("Running companySizeTest");

		// Load
		Fixtures.loadModels("data.yml");

		// CompanySize test
		assertEquals(1, InternCompanySize.count());
		InternCompanySize companySize = InternCompanySize.all().first();
		assertEquals("< 10", companySize.size);

	}

	@Test
	public void cityTest() {
		// Logging
		System.out.println();
		System.out.println("Running cityTest");

		// City test
		// Create a new city
		InternCity hanoi = new InternCity("Hanoi").save();

		// Add a first district
		hanoi.addDistrict("Dong Da");
		hanoi.addDistrict("Hai Ba Trung");

		// Retrieve all districts (from District table)
		List<InternDistrict> hanoiDistricts = InternDistrict.find("byCity", hanoi).fetch();

		// Tests
		assertEquals(2, hanoiDistricts.size());

		InternDistrict firstDistrict = hanoiDistricts.get(0);
		assertNotNull(firstDistrict);
		assertEquals("Dong Da", firstDistrict.name);

		InternDistrict secondDistrict = hanoiDistricts.get(1);
		assertNotNull(secondDistrict);
		assertEquals("Hai Ba Trung", secondDistrict.name);



		// Retrieve Hanoi's districts (directly from hanoi entity)
		hanoiDistricts = null;
		firstDistrict = null;
		secondDistrict = null;

		hanoiDistricts = hanoi.districts; 

		// Tests
		assertEquals(2, hanoiDistricts.size());

		firstDistrict = hanoiDistricts.get(0);
		assertNotNull(firstDistrict);
		assertEquals("Dong Da", firstDistrict.name);

		secondDistrict = hanoiDistricts.get(1);
		assertNotNull(secondDistrict);
		assertEquals("Hai Ba Trung", secondDistrict.name);


		// Removing a Dong Da from Hanoi
		hanoi.removeDistrict(firstDistrict);
		assertEquals(1, hanoi.districts.size());
		assertEquals(1, InternDistrict.count());
		assertEquals("Hai Ba Trung", hanoi.districts.get(0).name);

		hanoi.removeDistrict(0);
		assertEquals(0, hanoi.districts.size());
		assertEquals(0, InternDistrict.count());
		
		
		InternCity haiphong = new InternCity("Hai Phong").save();
		haiphong.addDistrict("Hai An");
		assertEquals(2, InternCity.count());
		assertEquals(1, InternDistrict.count());
		InternCity.deleteCity(haiphong);
		assertEquals(1, InternCity.count());
		assertEquals(0, InternDistrict.count());
	}

	@Test
	public void cityTestWithYml() {

		// Logging
		System.out.println();
		System.out.println("Running cityTestWithYml");

		Fixtures.loadModels("data.yml");
		List<InternDistrict> hanoiDistricts = InternDistrict.find("byName", "Dong Da").fetch();
		InternDistrict d = hanoiDistricts.get(0);
		assertEquals("Hanoi", d.city.name);

		// Find City Hanoi
		InternCity hanoi = InternCity.find("byName", "Hanoi").first();
		assertNotNull(hanoi);
		assertEquals(2, hanoi.districts.size());
		assertEquals(2, InternDistrict.count());

		// Remove a district from Hanoi
		InternDistrict.deleteDistrict(hanoi.districts.get(0));
		assertEquals(2, hanoi.districts.size());
		assertEquals(2, InternDistrict.count());
	}

	@Test
	public void addressTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running addressTestWithYml");

		Fixtures.loadModels("data.yml");
		List<InternAddress> address = InternAddress.findAll();
		assertEquals(address.size(), 3);
		InternAddress firstAddress = address.get(1);
		assertEquals("TM", firstAddress.address);
		assertEquals("Hanoi", firstAddress.city.name);
		assertEquals("Hai Ba Trung", firstAddress.district.name);
		
		// Deleting while the address is linked with its contactInfo
		InternContactInfo contactInfo = firstAddress.contactInfo;
		assertEquals(true, InternAddress.deleteAddress(firstAddress));
		assertEquals(InternAddress.count(), 2);
		assertNull(contactInfo.address);
		
		// Deleting null address
		assertEquals(false, InternAddress.deleteAddress(null));
	}
	
	@Test
	public void contactInfoTest() {
		// Logging
		System.out.println();
		System.out.println("Running contactInfoTest");
		
		InternContactInfo contactInfo = new InternContactInfo("dung@gmail.com").save();
		InternCity hanoi = new InternCity("Hanoi").save();
		contactInfo.address = new InternAddress(contactInfo, hanoi).save();
		
		assertEquals(1, InternContactInfo.count());
		assertEquals(1, InternCity.count());
		assertEquals(1, InternAddress.count());
		
		assertEquals(true, InternContactInfo.deleteContactInfo(contactInfo));
		assertEquals(0, InternContactInfo.count());
		assertEquals(1, InternCity.count());
		assertEquals(0, InternAddress.count());
	}
	
	@Test
	public void contactInfoTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running contactInfoTestWithYml");
		
		Fixtures.loadModels("data.yml");
		
		// Counting
		assertEquals(3, InternContactInfo.count());
		
		InternContactInfo steveContactInfo = InternContactInfo.find("byContactEmail", "steve@gmail.com").first();
		assertNotNull(steveContactInfo);
		
		InternJobSeeker steve = InternJobSeeker.find("byEmail", "steve@gmail.com").first();
		steveContactInfo = steve.contactInfo;
		assertNotNull(steveContactInfo);
		
		// Deleting steveContactInfo
		assertEquals(false, InternContactInfo.deleteContactInfo(steveContactInfo));
		assertEquals(3, InternContactInfo.count());
	}

	@Test
	public void jobTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running jobTestWithYml");

		//Job test
		Fixtures.loadModels("data.yml");
		List <InternJob> job = InternJob.findAll();
		for (InternJob counter : job) {
			assertEquals("doSomething", counter.name);
			assertEquals("We do things", counter.owner.description);
		}		
		InternJob firstJob = job.get(0);
		assertEquals("doSomething", firstJob.name);
		assertEquals("We do things", firstJob.owner.description);
		assertEquals("tom@gmail.com", firstJob.contactInfo.contactEmail);

	}

	@Test
	public void majorTest() {
		// Logging
		System.out.println();
		System.out.println("Running majorTest");

		new InternMajor("electronic").save();
		new InternMajor("it").save();
		List <InternMajor> major = InternMajor.findAll();
		assertEquals(major.size(), 2);
	}

	@Test
	public void majorTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running majorTestWithYml");

		Fixtures.loadModels("data.yml");
		List <InternMajor> major = InternMajor.findAll();
		assertEquals(major.size(), 2);
		InternMajor firstMajor = major.get(0);
		assertEquals("computer science", firstMajor.name);
	}

	@Test 
	public void educationTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running educationTestWithYml");

		// Loading
		Fixtures.loadModels("data.yml");
		
		List <InternEducation> education = InternEducation.findAll();
		assertEquals(education.size(), 1);
		InternEducation firstEducation = education.get(0);
		assertEquals("BK", firstEducation.college);
		assertEquals(3.4, firstEducation.gpa, 0.1);
		assertEquals("electronic", firstEducation.major.name);
		
		InternJob job = InternJob.find("byRequiredEducation", firstEducation).first();
		assertNotNull(job);
		assertEquals("tom@gmail.com", job.owner.email);
	}

	@Test
	public void applicationTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running applicationTestWithYml");

		// Loading
		Fixtures.loadModels("dungnguyendata.yml");
		
		// Counting
		assertEquals(1, InternJobSeeker.count());
		assertEquals(1, InternEmployer.count());
		assertEquals(1, InternApplication.count());
		assertEquals(1, InternResume.count());
		assertEquals(1, InternJob.count());		
		
		// Getting the application
		InternApplication application = InternApplication.all().first();
		assertNotNull(application);
		assertEquals(application.resume.name, "Steve's 1st resume");
		assertEquals(application.job.name, "Tom's 1st job");
		assertEquals(application.jobSeeker.email, "steve@gmail.com");
		assertEquals(application.employer.email, "tom@gmail.com");
		assertEquals(1, application.jobSeeker.applications.size());
		assertEquals(1, application.employer.applications.size());
		
		// Deleting the application's resume
		assertEquals(false, InternResume.deleteResume(application.resume));
		assertEquals(false, InternJob.deleteJob(application.job));
		assertEquals(false, InternJobSeeker.deleteJobSeeker(application.jobSeeker));
		assertEquals(false, InternEmployer.deleteEmployer(application.employer));
	}
	
	@Test
	public void languageTestWithYml() {
		// Logging
		System.out.println();
		System.out.println("Running languageTestWithYml");
		
		// Loading
		Fixtures.loadModels("data.yml");
		
		// Counting
		assertEquals(2, InternLanguage.count());
		
		// Getting english
		InternLanguage english = InternLanguage.find("byLanguage", "English").first();
		assertNotNull(english);
		assertEquals(0, english.useCount);
		
		InternResume resume = InternResume.all().first();
		resume.addLanguage(english);
		assertEquals(1, english.useCount);
		assertEquals(1, resume.languages.size());
		
		// Deleting english
		assertEquals(false, InternLanguage.deleteLanguage(english));
		
		resume.removeLanguage(english);
		assertEquals(0, english.useCount);
		assertEquals(0, resume.languages.size());
		
		// Deleting english again
		assertEquals(true, InternLanguage.deleteLanguage(english));
		assertEquals(1, InternLanguage.count());
	}
}
