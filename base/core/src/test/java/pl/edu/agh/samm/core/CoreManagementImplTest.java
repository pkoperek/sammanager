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

import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.edu.agh.samm.api.core.IResourceInstancesManager;
import pl.edu.agh.samm.api.metrics.IMetricsManagerListener;
import pl.edu.agh.samm.metrics.IMetricsManager;

@RunWith(MockitoJUnitRunner.class)
public class CoreManagementImplTest {

	private CoreManagementImpl impl = null;

	@Mock
	private IResourceDiscoveryAgent discoveryAgent;
	
	@Mock
	private IResourceInstancesManager resourceInstancesManager;
	
	@Mock
	private IRuleProcessor ruleProcessor;

	@Mock
	private IMetricsManager manager;

	@Before
	public void setUp() throws Exception {
		impl = new CoreManagementImpl();
	}

	@After
	public void tearDown() throws Exception {
		impl = null;
	}

	@Test
	public void testInit() {
		// Given
		impl.setResourceDiscoveryAgent(discoveryAgent);
		impl.setResourceInstancesManager(resourceInstancesManager);
		impl.setRuleProcessor(ruleProcessor);
		impl.setRunningMetricsManager(manager);
		
		// When
		impl.init();

		// Then
		verify(resourceInstancesManager).addResourceListener(discoveryAgent);
		verify(manager).addMetricsManagerListener(
				any(IMetricsManagerListener.class));
	}

	@Test
	public void testDestroy() {
		// Given
		impl.setResourceDiscoveryAgent(discoveryAgent);
		impl.setResourceInstancesManager(resourceInstancesManager);
		impl.setRuleProcessor(ruleProcessor);
		impl.setRunningMetricsManager(manager);
		
		// When
		impl.init();
		impl.destroy();

		// Then
		verify(resourceInstancesManager).removeResourceListener(discoveryAgent);
		verify(manager).removeMetricsManagerListener(
				any(IMetricsManagerListener.class));
	}

}
