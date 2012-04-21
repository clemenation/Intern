import org.junit.*;

import java.util.*;
import play.test.*;
import models.*;

public class InternResumeTest extends UnitTest {
	
	@Before
	public void setup() {
		Fixtures.deleteDatabase();
	}
	
	@Test 
	public void basicTest() {
		// Logging
		System.out.println();
		System.out.println("Running basicTest");

		// Loading
		Fixtures.loadModels("dungnguyendata.yml");

		// Counting
		assertEquals(3, InternResume.count());
	}
	
	@Test
	public void deleteTest() {
		// Logging
		System.out.println();
		System.out.println("Running deleteTest");
		
		// Loading
		Fixtures.loadModels("dungnguyendata.yml");
		
		InternResume resume = InternResume.find("byName", "Dung Nguyen's 2nd Resume").first();
		assertNotNull(resume);
		assertEquals(1, resume.applications.size());
		
		assertTrue(InternResume.deleteResume(resume));
		assertNull(InternResume.find("byName", "Dung Nguyen's 2nd Resume").first());
		assertEquals(2, InternResume.count());
		assertEquals(0, InternApplication.count());
		
		//==== Deleting null resume
		assertFalse(InternResume.deleteResume(null));
	}
}
