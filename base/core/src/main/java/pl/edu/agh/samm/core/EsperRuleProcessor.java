/**
 * 
 */
package pl.edu.agh.samm.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.action.ActionExecution;
import pl.edu.agh.samm.common.core.IActionExecutionListener;
import pl.edu.agh.samm.common.core.IAlarm;
import pl.edu.agh.samm.common.core.IAlarmListener;
import pl.edu.agh.samm.common.core.Rule;
import pl.edu.agh.samm.common.metrics.IMetric;
import pl.edu.agh.samm.common.metrics.IMetricEvent;
import pl.edu.agh.samm.common.sla.IServiceLevelAgreement;
import pl.edu.agh.samm.common.tadapter.IMeasurementEvent;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * @author koperek
 * 
 */
public class EsperRuleProcessor implements IRuleProcessor,
		IActionExecutionListener {

	private static final Logger logger = LoggerFactory
			.getLogger(EsperRuleProcessor.class);

	private EPServiceProvider epService = null;
	private List<IAlarmListener> alarmListeners = new CopyOnWriteArrayList<IAlarmListener>();
	private EPRuntime runtime = null;
	private EPAdministrator administrator = null;
	private IActionExecutor actionExecutor = null;
	private int gracePeriod = -1;
	private long lastActionExecutionEndTime = -1;

	public static Configuration getDefaultConfiguration() {
		Configuration configuration = new Configuration();
		configuration.addEventType(IMeasurementEvent.class);
		configuration.addEventType(IMetricEvent.class);
		return configuration;
	}

	public void setActionExecutor(IActionExecutor actionExecutor) {
		this.actionExecutor = actionExecutor;
	}

	public void setEpService(EPServiceProvider epService) {
		this.epService = epService;
		this.runtime = epService.getEPRuntime();
		this.administrator = epService.getEPAdministrator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * pl.edu.agh.samm.core.IRuleProcessor#setupSLA(pl.edu.agh.samm.common.sla
	 * .IServiceLevelAgreement)
	 */
	@Override
	public void setupSLA(IServiceLevelAgreement serviceLevelAgreement) {
		logger.debug("Setting up SLA");
		// TODO Auto-generated method stub

	}

	@Override
	public void addRule(final Rule rule) {
		// get all info from rule
		final String ruleName = rule.getName();

		String statementString = null;
		String customStatement = rule.getCustomStatement();
		if (customStatement != null) {
			statementString = customStatement;
		} else {
			statementString = "select metric, value from IMetricEvent";
			String resourceURI = rule.getResourceUri();
			String resourceTypeURI = rule.getResourceTypeUri();
			String metricURI = rule.getMetricUri();
			String condition = rule.getCondition();

			// create the filter
			String filter = "";

			if (resourceURI != null) {
				filter += "metric.resourceURI like '" + resourceURI + "'";
			}

			if (metricURI != null) {
				if (!filter.equals("")) {
					filter += " and ";
				}
				filter += "metric.metricURI like '" + metricURI + "'";
			}

			if (resourceTypeURI != null) {
				if (!filter.equals("")) {
					filter += " and ";
				}
				filter += "resourceType like '" + resourceTypeURI + "'";
			}

			if (!filter.equals("")) {
				statementString += "(" + filter + ")";
			}

			// where clause
			if (condition != null) {
				statementString += " where " + condition;
			}
		}

		logger.debug("Adding rule: " + statementString);

		// create the statement and add a listener
		EPStatement statement = administrator.createEPL(statementString,
				ruleName);
		statement.addListener(new UpdateListener() {

			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				// according to javadoc
				// http://esper.codehaus.org/esper-4.1.0/doc/api/com/espertech/esper/client/UpdateListener.html#update%28com.espertech.esper.client.EventBean[],%20com.espertech.esper.client.EventBean[]%29
				// newEvents may be null!
				if (newEvents != null) {
					for (EventBean event : newEvents) {
						// log all info
						if (logger.isDebugEnabled()) {
							logger.debug("Event: " + event);
							logger.debug("Event rule: " + ruleName);
							logger.debug("Event metric: " + event.get("metric"));
							logger.debug("Event value: " + event.get("value"));
						}

						Number value = (Number) event.get("value");
						IMetric metric = (IMetric) event.get("metric");
						IAlarm alarm = new Alarm(metric, ruleName, value);
						fireAlarm(alarm);

						if (rule.getActionToExecute() != null) {
							// gracePeriod and lastActionExecutionEndTime are <
							// 0 by default - currentTimeMillis
							// - lastActionExecutionEndTime should be always
							// positive - so event if no values are provided it
							// should work out of the box
							long now = System.currentTimeMillis();
							if (gracePeriod < 0
									|| lastActionExecutionEndTime < 0
									|| now - lastActionExecutionEndTime >= gracePeriod * 1000) {
								actionExecutor.executeRequest(rule
										.getActionToExecute());
							} else {
								logger.info("Omitting action execution: gracePeriod: "
										+ gracePeriod
										+ " lastActionExecutionEndTime: "
										+ lastActionExecutionEndTime
										+ " now: "
										+ now);
							}
						}
					}
				}
			}
		});
	}

	@Override
	public void clearRules() {
		administrator.destroyAllStatements();
	}

	@Override
	public void removeRule(String ruleName) {
		EPStatement statement = administrator.getStatement(ruleName);
		statement.destroy();
	}

	protected void processEvent(Object event) {
		runtime.sendEvent(event);
	}

	protected void fireAlarm(IAlarm alarm) {
		for (IAlarmListener alarmListener : alarmListeners) {
			try {
				alarmListener.handleAlarm(alarm);
			} catch (Exception e) {
				logger.error("SuggestedMetricsAlarm Listener: " + alarmListener
						+ " thrown an exception!", e);
			}
		}
	}

	@Override
	public void addAlarmListener(IAlarmListener alarmListener) {
		this.alarmListeners.add(alarmListener);
	}

	@Override
	public void removeAlarmListener(IAlarmListener alarmListener) {
		this.alarmListeners.remove(alarmListener);
	}

	@Override
	public void processMetricEvent(IMetricEvent event) throws Exception {
		processEvent(event);
	}

	@Override
	public void processMeasurementEvent(IMeasurementEvent event) {
		processEvent(event);
	}

	@Override
	public void setActionGracePeriod(int gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	@Override
	public void notifyActionExecution(ActionExecution actionExecution)
			throws Exception {
		this.lastActionExecutionEndTime = System.currentTimeMillis();
	}

}
