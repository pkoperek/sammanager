package pl.edu.agh.samm.core;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.fail;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.IAlarm;
import pl.edu.agh.samm.common.core.IAlarmListener;
import pl.edu.agh.samm.common.core.Rule;
import pl.edu.agh.samm.common.metrics.IMetric;
import pl.edu.agh.samm.common.metrics.IMetricEvent;
import pl.edu.agh.samm.common.metrics.MetricEvent;
import pl.edu.agh.samm.common.metrics.MetricImpl;
import pl.edu.agh.samm.common.tadapter.IMeasurementEvent;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class EsperRuleProcessorTest {

	private EsperRuleProcessor impl = null;
	private IMocksControl mocksControl = null;
	private IActionExecutor mockActionExecutor = null;

	@Before
	public void setUp() throws Exception {
		mocksControl = EasyMock.createControl();
		mockActionExecutor = mocksControl.createMock(IActionExecutor.class);
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
	public void testAddRuleStatement() {
		EPServiceProvider mockEPservice = mocksControl
				.createMock(EPServiceProvider.class);
		EPRuntime mockEpRuntime = mocksControl.createMock(EPRuntime.class);
		EPAdministrator mockEpAdmin = mocksControl
				.createMock(EPAdministrator.class);
		EPStatement mockStatement = mocksControl.createMock(EPStatement.class);
		Rule rule = mocksControl.createMock(Rule.class);

		// standard piece of code in setEpService
		expect(mockEPservice.getEPRuntime()).andReturn(mockEpRuntime);
		expect(mockEPservice.getEPAdministrator()).andReturn(mockEpAdmin);

		// addRule
		String RULE_NAME = "TestRule1";
		expect(rule.getName()).andReturn(RULE_NAME);
		expect(rule.getResourceTypeUri()).andReturn("resourceTypeURI");
		expect(rule.getResourceUri()).andReturn("testURI");
		expect(rule.getMetricUri()).andReturn("metrictestURI");
		expect(rule.getCondition()).andReturn("value > 10.0");
		expect(
				mockEpAdmin
						.createEPL(
								"select metric, value from IMetricEvent(metric.resourceURI = 'testURI' and metric.metricURI = 'metrictestURI' and resourceType = 'resourceTypeURI') where value > 10.0",
								RULE_NAME)).andReturn(mockStatement);

		// catch the update listener
		mockStatement.addListener(anyObject(UpdateListener.class));

		// replay
		mocksControl.replay();

		// scenario
		impl.setEpService(mockEPservice);
		impl.addRule(rule);

		// verify
		mocksControl.verify();
	}

	@Test
	public void testAddRuleWithAction() throws Exception {
		EPServiceProvider mockEPservice = mocksControl
				.createMock(EPServiceProvider.class);
		EPRuntime mockEpRuntime = mocksControl.createMock(EPRuntime.class);
		EPAdministrator mockEpAdmin = mocksControl
				.createMock(EPAdministrator.class);
		EPStatement mockStatement = mocksControl.createMock(EPStatement.class);
		Rule rule = mocksControl.createMock(Rule.class);

		// standard piece of code in setEpService
		expect(mockEPservice.getEPRuntime()).andReturn(mockEpRuntime);
		expect(mockEPservice.getEPAdministrator()).andReturn(mockEpAdmin);

		// addRule
		String RULE_NAME = "TestRule1";
		expect(rule.getName()).andReturn(RULE_NAME);
		expect(rule.getResourceTypeUri()).andReturn(null);
		expect(rule.getResourceUri()).andReturn(null);
		expect(rule.getMetricUri()).andReturn(null);
		expect(rule.getCondition()).andReturn(null);
		expect(
				mockEpAdmin.createEPL("select metric, value from IMetricEvent",
						RULE_NAME)).andReturn(mockStatement);

		// capture the created update listener
		Capture<UpdateListener> updateListenerCapture = new Capture<UpdateListener>();
		mockStatement.addListener(EasyMock.capture(updateListenerCapture));

		// mock metric event
		IMetricEvent mockEvent = mocksControl.createMock(IMetricEvent.class);
		// sending the event to the runtime
		mockEpRuntime.sendEvent(mockEvent);

		// mock metric
		IMetric mockMetric = mocksControl.createMock(IMetric.class);

		// mock EventBean
		EventBean mockEventBean = mocksControl.createMock(EventBean.class);
		expect(mockEventBean.get("value")).andReturn(1.0);
		expect(mockEventBean.get("metric")).andReturn(mockMetric);

		// action to execute
		Action mockAction = mocksControl.createMock(Action.class);
		expect(rule.getActionToExecute()).andReturn(mockAction).times(2);
		mockActionExecutor.executeRequest(mockAction);

		// replay
		mocksControl.replay();

		// scenario
		impl.setActionExecutor(mockActionExecutor);
		impl.setEpService(mockEPservice);
		impl.addRule(rule);
		impl.processMetricEvent(mockEvent);
		// the EP engine fires an event for update listener...
		UpdateListener updateListener = updateListenerCapture.getValue();
		updateListener.update(new EventBean[] { mockEventBean }, null);

		// verify
		mocksControl.verify();
	}

	@Test
	public void testAddRule() {
		EPServiceProvider mockEPservice = mocksControl
				.createMock(EPServiceProvider.class);
		EPRuntime mockEpRuntime = mocksControl.createMock(EPRuntime.class);
		EPAdministrator mockEpAdmin = mocksControl
				.createMock(EPAdministrator.class);
		EPStatement mockStatement = mocksControl.createMock(EPStatement.class);
		Rule rule = mocksControl.createMock(Rule.class);

		// standard piece of code in setEpService
		expect(mockEPservice.getEPRuntime()).andReturn(mockEpRuntime);
		expect(mockEPservice.getEPAdministrator()).andReturn(mockEpAdmin);

		// addRule
		String RULE_NAME = "TestRule1";
		expect(rule.getName()).andReturn(RULE_NAME);
		expect(rule.getResourceTypeUri()).andReturn(null);
		expect(rule.getResourceUri()).andReturn(null);
		expect(rule.getMetricUri()).andReturn(null);
		expect(rule.getCondition()).andReturn(null);
		expect(
				mockEpAdmin.createEPL("select metric, value from IMetricEvent",
						RULE_NAME)).andReturn(mockStatement);

		mockStatement.addListener(anyObject(UpdateListener.class));

		// replay
		mocksControl.replay();

		// scenario
		impl.setEpService(mockEPservice);
		impl.addRule(rule);

		// verify
		mocksControl.verify();
	}

	@Test
	public void testClearRules() {
		EPServiceProvider mockEPservice = mocksControl
				.createMock(EPServiceProvider.class);
		EPRuntime mockEpRuntime = mocksControl.createMock(EPRuntime.class);
		EPAdministrator mockEpAdmin = mocksControl
				.createMock(EPAdministrator.class);

		// standard piece of code in setEpService
		expect(mockEPservice.getEPRuntime()).andReturn(mockEpRuntime);
		expect(mockEPservice.getEPAdministrator()).andReturn(mockEpAdmin);
		mockEpAdmin.destroyAllStatements();

		// replay
		mocksControl.replay();

		// scenario
		impl.setEpService(mockEPservice);
		impl.clearRules();

		// verify
		mocksControl.verify();
	}

	@Test
	public void testRemoveRule() {
		EPServiceProvider mockEPservice = mocksControl
				.createMock(EPServiceProvider.class);
		EPRuntime mockEpRuntime = mocksControl.createMock(EPRuntime.class);
		EPAdministrator mockEpAdmin = mocksControl
				.createMock(EPAdministrator.class);
		EPStatement mockStatement = mocksControl.createMock(EPStatement.class);

		// standard piece of code in setEpService
		expect(mockEPservice.getEPRuntime()).andReturn(mockEpRuntime);
		expect(mockEPservice.getEPAdministrator()).andReturn(mockEpAdmin);

		String RULE_NAME = "RuleName1";
		expect(mockEpAdmin.getStatement(RULE_NAME)).andReturn(mockStatement);
		mockStatement.destroy();

		// replay
		mocksControl.replay();

		// scenario
		impl.setEpService(mockEPservice);
		impl.removeRule(RULE_NAME);

		// verify
		mocksControl.verify();
	}

	@Test
	public void testAlarmListener() throws Exception {
		IAlarmListener mockListener = mocksControl
				.createMock(IAlarmListener.class);
		IAlarm mockAlarm = mocksControl.createMock(IAlarm.class);
		mockListener.handleAlarm(mockAlarm);

		mocksControl.replay();
		impl.addAlarmListener(mockListener);
		impl.fireAlarm(mockAlarm);
		impl.removeAlarmListener(mockListener);
		impl.fireAlarm(mockAlarm);
		mocksControl.verify();
	}

	@Test
	public void testProcessing() throws Exception {
		IAlarmListener mockAlarmListener = mocksControl
				.createMock(IAlarmListener.class);
		mockAlarmListener.handleAlarm(anyObject(IAlarm.class));

		Configuration config = new Configuration();
		config.addEventType(IMeasurementEvent.class);
		config.addEventType(IMetricEvent.class);

		EPServiceProvider service = EPServiceProviderManager
				.getDefaultProvider(config);

		Rule rule = new Rule("testRuleName");
		rule.setResourceUri("resourceURI");
		rule.setMetricUri("metricURI");
		rule.setCondition("metric.metricPollTimeInterval > 10");
		rule.setResourceTypeUri("resourceTypeURI");

		MetricImpl mi = new MetricImpl("metricURI", "resourceURI");
		mi.setMetricPollTimeInterval(1000);
		MetricEvent e = new MetricEvent(mi, 1.0, "resourceTypeURI");

		// scenario
		mocksControl.replay();
		impl.setEpService(service);
		impl.addRule(rule);
		impl.addAlarmListener(mockAlarmListener);
		impl.processMetricEvent(e);
		mocksControl.verify();
	}

	@Test
	public void testProcessMetricEvent() throws Exception {
		// proper invocation of EPServiceProvider
		IMetricEvent event = mocksControl.createMock(IMetricEvent.class);
		EPServiceProvider serviceP = mocksControl
				.createMock(EPServiceProvider.class);
		EPRuntime epRuntime = mocksControl.createMock(EPRuntime.class);
		EPAdministrator epAdmin = mocksControl
				.createMock(EPAdministrator.class);
		expect(serviceP.getEPRuntime()).andReturn(epRuntime);
		expect(serviceP.getEPAdministrator()).andReturn(epAdmin);
		epRuntime.sendEvent(event);

		mocksControl.replay();
		impl.setEpService(serviceP);
		impl.processMetricEvent(event);
		mocksControl.verify();
	}

	@Test
	public void testProcessMeasurementEvent() {
		IMeasurementEvent event = mocksControl
				.createMock(IMeasurementEvent.class);
		EPServiceProvider serviceP = mocksControl
				.createMock(EPServiceProvider.class);
		EPRuntime epRuntime = mocksControl.createMock(EPRuntime.class);
		EPAdministrator epAdmin = mocksControl
				.createMock(EPAdministrator.class);
		expect(serviceP.getEPRuntime()).andReturn(epRuntime);
		expect(serviceP.getEPAdministrator()).andReturn(epAdmin);
		epRuntime.sendEvent(event);

		mocksControl.replay();
		impl.setEpService(serviceP);
		impl.processMeasurementEvent(event);
		mocksControl.verify();
	}
}
