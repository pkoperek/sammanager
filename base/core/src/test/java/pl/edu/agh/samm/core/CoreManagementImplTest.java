package pl.edu.agh.samm.core;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.edu.agh.samm.common.core.IResourceInstancesManager;
import pl.edu.agh.samm.common.core.IResourceListener;
import pl.edu.agh.samm.common.metrics.IMetricsManagerListener;
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
