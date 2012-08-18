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

package pl.edu.agh.samm.core;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.samm.api.core.Resource;
import pl.edu.agh.samm.api.core.ResourceAlreadyRegisteredException;

public class ResourceInstancesManagerImplTest {

	private ResourceInstancesManagerImpl impl = null;

	@Before
	public void setUp() throws Exception {
		impl = new ResourceInstancesManagerImpl();
	}

	@After
	public void tearDown() throws Exception {
		impl = null;
	}

	@Test
	public void testGetResourcesForRegex()
			throws ResourceAlreadyRegisteredException {
		// what happens when no resources are available
		impl = new ResourceInstancesManagerImpl();
		List<Resource> res = impl.getResourcesForRegex("/u.*");
		assertNotNull(res);
		assertEquals(0, res.size());
		impl = null;

		// some content
		impl = new ResourceInstancesManagerImpl();

		Resource r1 = new Resource("/uri1", "type1",
				new HashMap<String, Object>());
		Resource r2 = new Resource("/uri2", "type1",
				new HashMap<String, Object>());
		Resource r3 = new Resource("/uri3", "type1",
				new HashMap<String, Object>());
		impl.addResource(r1);
		impl.addResource(r2);
		impl.addResource(r3);

		res = impl.getResourcesForRegex("/u.*");

		assertEquals(3, res.size());
	}

}
