/**
 * 
 */
package pl.edu.agh.samm.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.core.IAlarm;
import pl.edu.agh.samm.common.core.IAlarmListener;
import pl.edu.agh.samm.common.core.Rule;
import pl.edu.agh.samm.common.metrics.IMetricEvent;
import pl.edu.agh.samm.common.sla.IServiceLevelAgreement;
import pl.edu.agh.samm.common.tadapter.IMeasurementEvent;

import com.espertech.esper.client.EPServiceProvider;

/**
 * @author koperek
 * 
 */
public class EsperRuleProcessor implements IRuleProcessor {

	private static final Logger logger = LoggerFactory
			.getLogger(EsperRuleProcessor.class);

	private EPServiceProvider epService = null;
	private List<IAlarmListener> alarmListeners = new CopyOnWriteArrayList<IAlarmListener>();

	public void setEpService(EPServiceProvider epService) {
		this.epService = epService;
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
	public void addRule(Rule rule) {
		// TODO Auto-generated method stub
	}

	@Override
	public void clearRules() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeRule(String ruleName) {
		// TODO Auto-generated method stub

	}

	protected void processEvent(Object event) {
		epService.getEPRuntime().sendEvent(event);
	}

	protected void fireAlarm(IAlarm alarm) {
		for (IAlarmListener alarmListener : alarmListeners) {
			try {
				alarmListener.handleAlarm(alarm);
			} catch (Exception e) {
				logger.error("Alarm Listener: " + alarmListener
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
		epService.getEPRuntime().sendEvent(event);
	}

	@Override
	public void processMeasurementEvent(IMeasurementEvent event) {
		epService.getEPRuntime().sendEvent(event);
	}

}
