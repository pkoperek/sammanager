/**
 * 
 */
package pl.edu.agh.samm.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@Override
	public void addAlarmListener(IAlarmListener alarmListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeAlarmListener(IAlarmListener alarmListener) {
		// TODO Auto-generated method stub

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
