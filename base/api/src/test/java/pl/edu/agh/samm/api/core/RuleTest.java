/**
 * This file is part of SAMM.
 *
 * SAMM is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SAMM is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SAMM.  If not, see <http://www.gnu.org/licenses/>.
 */

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
