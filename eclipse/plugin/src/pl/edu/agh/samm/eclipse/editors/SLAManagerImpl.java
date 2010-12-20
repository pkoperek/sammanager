/**
 * 
 */
package pl.edu.agh.samm.eclipse.editors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import pl.edu.agh.samm.common.decision.IServiceLevelAgreement;
import pl.edu.agh.samm.common.decision.ServiceLevelAgreement;
import pl.edu.agh.samm.common.knowledge.ICriterion;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class SLAManagerImpl implements ISLAManager {

	private ServiceLevelAgreement serviceLevelAgreement = new ServiceLevelAgreement();
	private List<ISLAChangesListener> listeners = new CopyOnWriteArrayList<ISLAChangesListener>();

	protected void fireSLAChanged(SLAChangeRange changeRange) {
		for (ISLAChangesListener listener : listeners) {
			listener.slaChanged(changeRange);
		}
	}

	/**
	 * @param resourceURI
	 * @param metricURI
	 * @return
	 * @see pl.edu.agh.samm.common.decision.IServiceLevelAgreement#getCriterionForResourceMetric(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public ICriterion getCriterionForResourceMetric(String resourceURI, String metricURI) {
		return serviceLevelAgreement.getCriterionForResourceMetric(resourceURI, metricURI);
	}

	/**
	 * @param resourceURI
	 * @param metricURI
	 * @return
	 * @see pl.edu.agh.samm.common.decision.IServiceLevelAgreement#getMetricCost(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public Number getMetricCost(String resourceURI, String metricURI) {
		return serviceLevelAgreement.getMetricCost(resourceURI, metricURI);
	}

	/**
	 * @param resourceURI
	 * @return
	 * @see pl.edu.agh.samm.common.decision.IServiceLevelAgreement#getMetricsForResource(java.lang.String)
	 */
	@Override
	public List<String> getMetricsForResource(String resourceURI) {
		return serviceLevelAgreement.getMetricsForResource(resourceURI);
	}

	/**
	 * @param resourceURI
	 * @return
	 * @see pl.edu.agh.samm.common.decision.IServiceLevelAgreement#getParameters(java.lang.String)
	 */
	@Override
	public Map<String, Object> getParameters(String resourceURI) {
		return serviceLevelAgreement.getParameters(resourceURI);
	}

	/**
	 * @param resource
	 * @return
	 * @see pl.edu.agh.samm.common.decision.IServiceLevelAgreement#getResourceType(java.lang.String)
	 */
	@Override
	public String getResourceType(String resource) {
		return serviceLevelAgreement.getResourceType(resource);
	}

	@Override
	public void addInvolvedResource(String uri, String resourceType) {
		serviceLevelAgreement.addInvolvedResource(uri, resourceType);
		fireSLAChanged(SLAChangeRange.RESOURCES);
	}

	@Override
	public void addSLAChangesListener(ISLAChangesListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public IServiceLevelAgreement getServiceLevelAgreement() {
		return serviceLevelAgreement;
	}

	@Override
	public void removeSLAChangesListener(ISLAChangesListener listener) {
		this.listeners.remove(listener);
	}

	@Override
	public void setResourceParameters(String uri, Map<String, Object> parameters) {
		serviceLevelAgreement.addResourceParameters(uri, parameters);
		fireSLAChanged(SLAChangeRange.RESOURCES);
	}

	@Override
	public void removeInvolvedResource(String uri) {
		serviceLevelAgreement.removeInvolvedResource(uri);
		fireSLAChanged(SLAChangeRange.RESOURCES);
	}

	@Override
	public void setCriterionForResourceMetric(String selectedResource, String selectedMetric,
			ICriterion criterion) {
		serviceLevelAgreement.addCriterionForResourceMetric(selectedResource, selectedMetric, criterion);
		fireSLAChanged(SLAChangeRange.CRITERIA);
	}

	@Override
	public void removeCriterionForResourceMetric(String selectedResource, String selectedMetric) {
		serviceLevelAgreement.removeCriterionForResourceMetric(selectedResource, selectedMetric);
		fireSLAChanged(SLAChangeRange.CRITERIA);
	}

	@Override
	public void setResourceMetricCost(String selectedResource, String selectedMetric, Number cost) {
		serviceLevelAgreement.setResourceMetricCost(selectedResource, selectedMetric, cost);
	}

	@Override
	public void setServiceLevelAgreement(IServiceLevelAgreement newServiceLevelAgreement) {

		copyNewSLAIntoOld(newServiceLevelAgreement);
		fireSLAChanged(SLAChangeRange.WHOLE);
	}

	private void copyNewSLAIntoOld(IServiceLevelAgreement newServiceLevelAgreement) {
		this.serviceLevelAgreement.clear();
		for (String resourceURI : newServiceLevelAgreement.getInvolvedPatterns()) {
			this.addInvolvedResource(resourceURI, newServiceLevelAgreement.getResourceType(resourceURI));
			this.setResourceParameters(resourceURI, newServiceLevelAgreement.getParameters(resourceURI));

			List<String> metricsForResource = newServiceLevelAgreement.getMetricsForResource(resourceURI);
			for (String metricForResource : metricsForResource) {
				this.setCriterionForResourceMetric(resourceURI, metricForResource, newServiceLevelAgreement
						.getCriterionForResourceMetric(resourceURI, metricForResource));

				this.setResourceMetricCost(resourceURI, metricForResource,
						newServiceLevelAgreement.getMetricCost(resourceURI, metricForResource));
			}
		}
	}

	@Override
	public List<String> getInvolvedPatterns() {
		return serviceLevelAgreement.getInvolvedPatterns();
	}
}
