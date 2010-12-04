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

package pl.edu.agh.samm.estimator;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.db.IStorageService;
import pl.edu.agh.samm.common.estimation.IEstimator;
import pl.edu.agh.samm.common.metrics.MeasurementValue;
import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class Estimator implements IEstimator {

	private static final Logger logger = LoggerFactory.getLogger(Estimator.class);
	private static final String ATTRIBUTE_NAME_PREFIX = "t0+";
	private static final String CLASS_ATTRIBUTE_NAME = "future";
	private IStorageService storageService = null;
	private int windowSize;
	private String algorithm;

	/**
	 * @return the storageService
	 */
	public IStorageService getStorageService() {
		return storageService;
	}

	/**
	 * @param storageService
	 *            the storageService to set
	 */
	public void setStorageService(IStorageService storageService) {
		this.storageService = storageService;
	}

	/**
	 * @return the windowSize
	 */
	public int getWindowSize() {
		return windowSize;
	}

	/**
	 * @param windowSize
	 *            the windowSize to set
	 */
	public void setWindowSize(int windowSize) {
		this.windowSize = windowSize;
	}

	/**
	 * @return the algorithm
	 */
	public String getAlgorithm() {
		return algorithm;
	}

	/**
	 * @param algorithm
	 *            the algorithm to set
	 */
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public Map<String, Map<String, Number>> estimateAllMeasurements(long timeOffset) {
		Map<String, Map<String, Number>> estimations = new HashMap<String, Map<String, Number>>();

		Set<String> allKnownResources = storageService.getAllKnownResources();
		logger.debug("Starting estimating");
		long startTime = System.currentTimeMillis();
		for (String resourceURI : allKnownResources) {

			Map<String, Number> resourceEstimations = new HashMap<String, Number>();
			estimateMeasurementsForResource(resourceURI, resourceEstimations, timeOffset);
			estimations.put(resourceURI, resourceEstimations);
		}
		long endTime = System.currentTimeMillis();
		logger.info("Estimating all measurements took: " + ((endTime - startTime) / 1000) + "s");
		return estimations;
	}

	private void estimateMeasurementsForResource(String resourceURI, Map<String, Number> estimations,
			long timeOffset) {
		Set<String> capabilites = storageService.getResourceCapabilites(resourceURI);
		for (String capabilityURI : capabilites) {
			try {
				logger.debug("Starting estimation for resource " + resourceURI + " capability "
						+ capabilityURI);
				long startTime = System.currentTimeMillis();
				Number estimation = estimateMeasurementsForResourceCapability(resourceURI, capabilityURI,
						timeOffset);
				long endTime = System.currentTimeMillis();
				logger.info("Estimating capability value took " + ((endTime - startTime) / 1000) + "s");
				estimations.put(capabilityURI, estimation);
			} catch (NumberFormatException e) {
				logger.info("Capability: " + capabilityURI + " for resource: " + resourceURI
						+ " doesn't have numeric values! Can't estimate it!", e);
			} catch (MissingValuesException e) {
				logger.info("No values for capability: " + capabilityURI + "@" + resourceURI);
			} catch (Exception e) {
				logger.error("Error ocurred during estimation!", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private Number estimateMeasurementsForResourceCapability(String resourceURI, String capabilityURI,
			long timeOffset) throws Exception {
		// get measurement values
		// List<MeasurementValue> measurementValues = storageService
		// .getHistoricalMeasurementValues(resourceURI, capabilityURI);
		Calendar cal = Calendar.getInstance();
		// TODO parameterize how far back
		cal.add(Calendar.MINUTE, -5);
		List<MeasurementValue> measurementValues = storageService.getHistoricalMeasurementValues(resourceURI,
				capabilityURI, cal.getTime(), new Date());
		logger.debug("Number of measurements: " + measurementValues.size());

		// create instances of values ready to be classified (conversion to weka
		// input format)
		if (measurementValues.size() == 0) {
			throw new MissingValuesException();
		}

		Double singleValue = getValueIfOnlyOneInSeries(measurementValues);
		double retVal = 0.0;

		if (singleValue != null) {
			retVal = singleValue;
		} else {
			Instances toClassify = convertInputToSingleAttributesSerie(measurementValues, capabilityURI);

			int lastAttr = toClassify.numAttributes() - 1;
			toClassify.setClassIndex(lastAttr);

			// create classifier
			Class<? extends Classifier> classifierClass = (Class<? extends Classifier>) Class
					.forName(algorithm);

			Classifier classifier = classifierClass.newInstance();
			classifier.buildClassifier(toClassify);
			Instance instance = toClassify.lastInstance();

			// find interesting value
			// generate an estimation for each second
			for (long i = 0; i < timeOffset; i++) {
				retVal = classifier.classifyInstance(instance);
				shiftInstanceValuesAddOnRight(instance, retVal);
			}
		}

		return retVal;
	}

	private Instances convertInputToSingleAttributesSerie(List<MeasurementValue> measurementValues,
			String capabilityURI) throws NumberFormatException {

		int numInstances = measurementValues.size();

		// for each attribute create a set of instances
		int dataSetAttributesNum = windowSize + 1;

		// create attribute description
		FastVector attributes = new FastVector(dataSetAttributesNum);
		for (int i = 0; i < windowSize; i++) {
			attributes.addElement(new Attribute(ATTRIBUTE_NAME_PREFIX + i));
		}
		attributes.addElement(new Attribute(CLASS_ATTRIBUTE_NAME));

		// create instances collection
		Instances retVal = new Instances(capabilityURI, attributes, 0);

		// create instances
		int startInstance = 0;
		while (startInstance + dataSetAttributesNum <= numInstances) {

			// fill instances with data
			Instance newInstance = new Instance(dataSetAttributesNum);
			for (int j = 0; j < dataSetAttributesNum; j++) {
				MeasurementValue measurementValue = measurementValues.get(j + startInstance);
				double value = Double.parseDouble(measurementValue.getValue().toString());
				newInstance.setValue(j, value);
			}

			retVal.add(newInstance);
			startInstance++;
		}

		return retVal;
	}

	private void shiftInstanceValuesAddOnRight(Instance instance, double result) {
		for (int i = 0; i < instance.numAttributes() - 1; i++) {
			double valueToShift = instance.value(i + 1);
			instance.setValue(i, valueToShift);
		}

		instance.setValue(instance.numAttributes() - 1, result);
	}

	private Double getValueIfOnlyOneInSeries(List<MeasurementValue> measurements) {
		Double prevValue = null;
		for (MeasurementValue measurementValue : measurements) {
			Double currentValue = Double.parseDouble(measurementValue.getValue().toString());
			if (prevValue != null) {
				if (!prevValue.equals(currentValue)) {
					prevValue = null;
					break;
				}
			}

			prevValue = currentValue;
		}
		return prevValue;
	}

	/**
	 * Marker exception - thrown when there are no measurements for particular
	 * capability
	 * 
	 * @author Pawel Koperek <pkoperek@gmail.com>
	 * @author Mateusz Kupisz <mkupisz@gmail.com>
	 * 
	 */
	private class EstimatorException extends Exception {
		private static final long serialVersionUID = -5248720987758373978L;
	}

	private class MissingValuesException extends EstimatorException {
		private static final long serialVersionUID = 2071654541661245662L;
	}
}
