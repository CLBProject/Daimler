package groovy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import templates.Groovy;

public class GroovyTest {

	private static final char STRING_CHAR = '"';

	private static final String GROOVY_SCRIPT_GET_1 = "import com.daimler.Car\r\n" + 
			"def id = " + STRING_CHAR + "1" + STRING_CHAR + "\r\n" +
			"car = Car.lookup(id)";

	private static final String GROOVY_SCRIPT_GET_2 = "import com.daimler.Car\r\n" + 
			"def id = " + STRING_CHAR + "2" + STRING_CHAR + "\r\n" +
			"car = Car.lookup(id)";

	private static final String GROOVY_SCRIPT_GET_3 = "import com.daimler.Car\r\n" + 
			"def id = " + STRING_CHAR + "3" + STRING_CHAR + "\r\n" +
			"car = Car.lookup(id)";
	
	private static final String GROOVY_SCRIPT_GET_4 = "import com.daimler.Car\r\n" + 
			"def id = " + STRING_CHAR + "4" + STRING_CHAR + "\r\n" +
			"car = Car.lookup(id)";

	private Groovy groovy;
	private Groovy groovy2;
	private Groovy groovy3;
	private Groovy groovy4;

	@Before
	public void init() {
		groovy = new Groovy(GROOVY_SCRIPT_GET_1, new HashMap<String,Object>());
		groovy2 = new Groovy(GROOVY_SCRIPT_GET_2, new HashMap<String,Object>());
		groovy3 = new Groovy(GROOVY_SCRIPT_GET_3, new HashMap<String,Object>());
		groovy4 = new Groovy(GROOVY_SCRIPT_GET_4, new HashMap<String,Object>());
	}


	@Test
	public void testGetExpression() {

		assertEquals(groovy.getExpValue("car", "brand"),"Smart");
		assertEquals(groovy2.getExpValue("car", "brand"),"Mercedes-Benz");
		assertEquals(groovy3.getExpValue("car", "brand"),"Mercedes-AMG");
		assertEquals(groovy4.getExpValue("car", "brand"),"Empty Brand");
		
		assertEquals(groovy.getExpValue("car", "fuelType"),"Diesel");
		assertEquals(groovy2.getExpValue("car", "fuelType"),"Hybrid");
		assertEquals(groovy3.getExpValue("car", "fuelType"),"Electric");
		assertEquals(groovy4.getExpValue("car", "fuelType"),"Empty fuelType");

	}

	@Test(expected = Exception.class)
	public void testGetExpressionDoesntExist() {
		assertEquals(groovy3.getExpValue("car", "brand2"),"Mercedes-AMG");
	}

	@Test
	public void testIsExpression() {

		assertFalse(groovy.isExpValue("car", "ecoFriendly"));
		assertTrue(groovy2.isExpValue("car", "ecoFriendly"));
		assertTrue(groovy3.isExpValue("car", "ecoFriendly"));
		assertFalse(groovy4.isExpValue("car", "ecoFriendly"));

	}

	@Test(expected = Exception.class)
	public void testIsExpressionDoesntExist() {
		assertTrue(groovy3.isExpValue("car", "ecoFriendly2"));
	}


	@Test
	public void testCollections() {
		assertEquals(groovy.getExpCollection("car", "models").size(),1);
		assertEquals(groovy2.getExpCollection("car", "models").size(),3);
		assertEquals(groovy3.getExpCollection("car", "models").size(), 0);
		assertEquals(groovy4.getExpCollection("car", "models").size(), 0);
	}
	
	@Test(expected = Exception.class)
	public void testCollectionDoesntExist() {
		assertEquals(groovy3.getExpCollection("car", "models2").size(), 0);
	}
}
