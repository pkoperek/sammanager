package pl.edu.agh.samm.api.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RuleTest {

	@Test
	public void testHashCode() {
		String name = "testName";
		Rule rule = new Rule(name);
		assertEquals(name.hashCode(), rule.hashCode());
	}

	@Test
	public void testEqualsObject() {
		Rule rule1 = new Rule("tes1");
		Rule rule2 = new Rule("tes2");
		Rule rule3 = new Rule("tes1");
		rule3.setMetricUri("aaa");
		rule3.setResourceTypeUri("aaa");
		rule3.setResourceUri("aaa");
		rule3.setCondition("aaa");

		assertTrue(rule1.equals(rule1));
		assertTrue(rule1.equals(rule3));
		assertFalse(rule1.equals(rule2));
	}

	@Test
	public void testRule() {
		Rule r = new Rule("111");

		try {
			Rule r2 = new Rule(null);
			fail("NPE not thrown!");
		} catch (IllegalArgumentException e) {
			// everything is ok!
		}
	}

}
