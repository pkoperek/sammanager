package pl.edu.agh.samm.core;

import static org.junit.Assert.fail;

import static org.easymock.EasyMock.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;

import pl.edu.agh.samm.common.metrics.IMetricEvent;
import pl.edu.agh.samm.common.tadapter.IMeasurementEvent;

public class EsperRuleProcessorTest {

	private EsperRuleProcessor impl = null;

	@Before
	public void setUp() throws Exception {
		impl = new EsperRuleProcessor();
	}

	@After
	public void tearDown() throws Exception {
		impl = null;
	}

	@Test
	public void testSetupSLA() {
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

	@Test
	public void testAddAlarmListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testRemoveAlarmListener() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testNotifyMetricValue() {
		fail("Not yet implemented"); // TODO
	}

	@Test
	public void testProcessMetricEvent() throws Exception {
		IMetricEvent event = createMock(IMetricEvent.class);
		EPServiceProvider serviceP = createMock(EPServiceProvider.class);
		EPRuntime epRuntime = createMock(EPRuntime.class);
		expect(serviceP.getEPRuntime()).andReturn(epRuntime);
		epRuntime.sendEvent(event);

		replay(epRuntime);
		replay(event);
		replay(serviceP);
		impl.setEpService(serviceP);
		impl.processMetricEvent(event);
		verify(serviceP);
		verify(epRuntime);
		verify(event);
	}

	@Test
	public void testProcessMeasurementEvent() {
		IMeasurementEvent event = createMock(IMeasurementEvent.class);
		EPServiceProvider serviceP = createMock(EPServiceProvider.class);
		EPRuntime epRuntime = createMock(EPRuntime.class);
		expect(serviceP.getEPRuntime()).andReturn(epRuntime);
		epRuntime.sendEvent(event);

		replay(epRuntime);
		replay(event);
		replay(serviceP);
		impl.setEpService(serviceP);
		impl.processMeasurementEvent(event);
		verify(serviceP);
		verify(epRuntime);
		verify(event);
	}
}
