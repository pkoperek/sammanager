package pl.edu.agh.samm.core;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.samm.common.core.IResourceInstancesManager;

public class CoreManagementImplTest {

	private CoreManagementImpl impl = null;

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
		IResourceInstancesManager manager = createMock(IResourceInstancesManager.class);
		IResourceDiscoveryAgent agent = createMock(IResourceDiscoveryAgent.class);
		manager.addResourceListener(agent);
		replay(manager);
		replay(agent);

		impl.setResourceDiscoveryAgent(agent);
		impl.setResourceInstancesManager(manager);
		impl.init();

		verify(manager);
		verify(agent);
	}

	@Test
	public void testDestroy() {
		IResourceInstancesManager manager = createMock(IResourceInstancesManager.class);
		IResourceDiscoveryAgent agent = createMock(IResourceDiscoveryAgent.class);
		manager.removeResourceListener(agent);
		replay(manager);
		replay(agent);

		impl.setResourceDiscoveryAgent(agent);
		impl.setResourceInstancesManager(manager);
		impl.destroy();

		verify(manager);
		verify(agent);
	}

	@Test
	public void testStartMetric() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testStopMetric() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAddRunningMetricListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAddRunningMetricsManagerListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRemoveRunningMetricListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRemoveRunningMetricsManagerListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testStartMetricAndAddRunningMetricListenerIConfiguredMetricCollectionOfIMetricListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testStartMetricAndAddRunningMetricListenerIConfiguredMetricIMetricListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testIsMetricRunning() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testIsResourceRegistered() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetAllRegisteredResources() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetResourceType() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRegisterResource() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAddResourceParameters() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testProcessEvent() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testCreateMetricInstance() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetResourceCapabilities() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testUpdateMetricPollTimeInterval() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAddAlarmListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRemoveAlarmListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testStartSLAValidation() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testUpdateSLA() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testStopSLAValidation() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testIsSLAValidationRunning() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testUnregisterResource() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testGetCurrentCostEvaluator() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testSetCurrentCostEvaluator() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRetrieveCurrentSLA() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAddActionExecutorListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRemoveActionExecutorListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testAddRule() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testClearRules() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRemoveRule() {
		fail("Not yet implemented"); // TODO
	}

}
