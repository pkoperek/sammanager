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

package pl.edu.agh.samm.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.ICoreManagement;
import pl.edu.agh.samm.common.core.IResourceEvent;
import pl.edu.agh.samm.common.core.IResourceListener;
import pl.edu.agh.samm.common.core.Resource;
import pl.edu.agh.samm.common.core.ResourceAlreadyRegisteredException;
import pl.edu.agh.samm.common.core.Rule;
import pl.edu.agh.samm.common.knowledge.IKnowledge;
import pl.edu.agh.samm.common.metrics.IMetric;
import pl.edu.agh.samm.common.metrics.IMetricEvent;
import pl.edu.agh.samm.common.metrics.IMetricListener;
import pl.edu.agh.samm.common.metrics.IMetricsManagerListener;
import pl.edu.agh.samm.common.metrics.MetricNotRunningException;
import pl.edu.agh.samm.common.metrics.ResourceEventType;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class TestBean implements IResourceListener, IMetricsManagerListener,
		IMetricListener {
	private ICoreManagement coreManagement;
	private Logger logger = LoggerFactory.getLogger(TestBean.class);
	private boolean registered = false;
	private IKnowledge knowledge = null;

	public IKnowledge getKnowledge() {
		return knowledge;
	}

	public void setKnowledge(IKnowledge knowledge) {
		this.knowledge = knowledge;
	}

	public ICoreManagement getCoreManagement() {
		return coreManagement;
	}

	public void setCoreManagement(ICoreManagement coreManagement) {
		this.coreManagement = coreManagement;
	}

	public void init() throws ResourceAlreadyRegisteredException {
		logger.info("INITIALIZING TEST");
		coreManagement.addResourceListener(this);
		coreManagement.addRunningMetricsManagerListener(this);
		Map<String, Object> params = new HashMap<String, Object>();
		// coreManagement.registerNode("jmx://cluster01", params);
		params = new HashMap<String, Object>();
		params.put("JMXURL",
				"service:jmx:rmi:///jndi/rmi://localhost:60000/jmxrmi");
		Rule r = new Rule("testRule_1");
		Action action = new Action();
		action.setActionURI("http://www.icsr.agh.edu.pl/samm_1.owl#StartVMAction");
		r.setActionToExecute(action);

		coreManagement.addRule(r);

		coreManagement.registerResource(new Resource("jmx://cluster01/node01",
				"http://www.icsr.agh.edu.pl/samm_1.owl#Node", params));

		logger.info("END OF TEST INITIALIZATION");
	}

	@Override
	public void processEvent(IResourceEvent event) {
		logger.info("*** Got: " + event);

		if (event.getType().equals(ResourceEventType.RESOURCES_ADDED)
				&& !registered) {
			String resourceString = (String) event.getAttachment();
			String[] strings = resourceString.split(" ");
			if (strings[1].contains("Thread")) {
				Set<String> metrics = knowledge
						.getMetricsForResourceType(strings[1]);
				IMetric metric = coreManagement.createMetricInstance(
						metrics.toArray(new String[0])[0], strings[0]);
				coreManagement.startMetric(metric);
				registered = true;
			}
		}
	}

	@Override
	public void notifyMetricsHasStopped(Collection<IMetric> stoppedMetrics) {
		// TODO Auto-generated method stub

	}

	@Override
	public void notifyNewMetricsStarted(Collection<IMetric> startedMetrics) {
		logger.info("***** Metric has started! " + startedMetrics);

		for (IMetric metric : startedMetrics) {
			try {
				coreManagement.addRunningMetricListener(metric, this);
			} catch (MetricNotRunningException e) {
				logger.error("Exception!", e);
			}
		}

	}

	@Override
	public void processMetricEvent(IMetricEvent event) {
		logger.info("***** GOT VALUE ***** " + event.getMetric() + " : "
				+ event.getValue());
	}

}
