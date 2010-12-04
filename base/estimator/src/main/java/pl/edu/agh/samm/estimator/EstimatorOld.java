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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Discretize;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class EstimatorOld {
	private enum Mode {
		SINGLE, MULTIPLE
	};

	private static final String INFO_MSG_PREFIX = "INFO";
	private static final String ERROR_MSG_PREFIX = "ERROR";
	private static final String INPUT_COMMENT = "INPUT";
	private static final String CONVERTED_COMMENT = "CONVERTED";
	private static final String RELATION_NAME_SUFFIX = ".relation";
	private static final String ATTRIBUTE_NAME_PREFIX = "t0+";
	private static final String UNDERSCORE = "_";
	private static final String CLASS_ATTRIBUTE_NAME = "future";

	private String inputFileName;
	private int windowSize;
	private Mode mode;
	private int instancesToPredict = 1;
	private int numOfAttrToPredict;
	private boolean discretize;
	private String algorithm;
	private static boolean log = true;

	public EstimatorOld(String inputFileName, int windowSize, int instancesToPredict, int numOfAttrToPredict,
			boolean discretize, String algorithm, Mode mode) {
		this.windowSize = windowSize;
		this.inputFileName = inputFileName;
		this.mode = mode;
		this.instancesToPredict = instancesToPredict;
		this.numOfAttrToPredict = numOfAttrToPredict;
		this.discretize = discretize;
		this.algorithm = algorithm;
	}

	public static void main(String[] args) {
		if (args.length < 8) {
			logErr("Improper number of arguments!");
			System.exit(1);
		}

		log = Boolean.parseBoolean(args[7]);
		logInfo("Using: logging: " + log);

		String fileName = args[0];
		logInfo("Using: " + fileName);

		int windowSize = -1;

		try {
			windowSize = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			logErr(e);
			System.exit(1);
		}

		if (windowSize < 1) {
			logErr("Windows size has to be greater than 1!");
			System.exit(1);
		}

		logInfo("Using: window size " + windowSize);

		int instancesToPredict = -1;

		try {
			instancesToPredict = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			logErr(e);
			System.exit(1);
		}

		if (instancesToPredict < 0) {
			logErr("Number of instances to predict has to be greater than 0!");
			System.exit(1);
		}

		Mode mode = args[3].equals("m") ? Mode.MULTIPLE : Mode.SINGLE;

		logInfo("Using: mode: " + mode);

		int numOfAttrToPredict = 0;
		try {
			numOfAttrToPredict = Integer.parseInt(args[4]);
		} catch (NumberFormatException e) {
			logErr(e);
			System.exit(1);
		}

		logInfo("Using: numOfAttrToPredict: " + numOfAttrToPredict);
		numOfAttrToPredict--; // we use the array indexing

		boolean discretize = Boolean.parseBoolean(args[5]);

		String algorithm = args[6];

		EstimatorOld estimator = new EstimatorOld(fileName, windowSize, instancesToPredict,
				numOfAttrToPredict, discretize, algorithm, mode);
		estimator.estimate();
	}

	public void estimate() {
		BufferedReader bufferedReader = null;
		ArffReader arffReader = null;
		try {
			// read instances
			bufferedReader = new BufferedReader(new FileReader(inputFileName));
			arffReader = new ArffReader(bufferedReader);

			Instances instances = arffReader.getData();

			// print instances
			printInstances(instances, INPUT_COMMENT);

			// generate input for classifiers
			Instances[] outputInstances = null;
			if (mode.equals(Mode.SINGLE)) {
				outputInstances = convertInputToSingleAttributesSeries(instances);
			} else {
				outputInstances = convertInputToMultipleAttributesSeries(instances);
			}

			printInstances(outputInstances, CONVERTED_COMMENT);

			Instances toClassify = null;

			if (discretize) {
				Discretize filter = new Discretize();
				filter.setAttributeIndices("first-last");
				filter.setIgnoreClass(false);
				filter.setInputFormat(outputInstances[0]);
				toClassify = Filter.useFilter(outputInstances[numOfAttrToPredict], filter);
			} else {
				toClassify = outputInstances[numOfAttrToPredict];
			}

			// printInstances(toClassify, "after discretization");

			// ArffSaver saver = new ArffSaver();
			// File dest = new File("/home/koperek/test.arff");
			// saver.setInstances(toClassify);
			// saver.setFile(dest);
			// saver.setDestination(dest);
			// saver.writeBatch();

			// classify

			// ***********************************************************************
			// EVALUATION CODE
			// ***********************************************************************

			int lastAttr = toClassify.numAttributes() - 1;
			toClassify.setClassIndex(lastAttr);

			Class<? extends Classifier> classifierClass = (Class<? extends Classifier>) Class
					.forName(algorithm);

			Classifier classifier = classifierClass.newInstance();
			classifier.buildClassifier(toClassify);
			Instance instance = toClassify.lastInstance();

			Attribute classAttribute = toClassify.classAttribute();

			for (int i = 0; i < instancesToPredict; i++) {
				double result = classifier.classifyInstance(instance);
				if (!discretize) {
					System.out.println(result);
				} else {
					String estimatedClass = classAttribute.value((int) result);
					String[] range = estimatedClass.replaceAll("\\(|\\[|\\]|\\)|\\'", "").split("-");
					double upper = Double.parseDouble(range[0]);
					double lower = Double.parseDouble(range[1]);
					System.out.println((upper + lower) / 2);
				}
				// logInfo("Predicted " + i + ": " + result + " instance: " +
				// instance);
				shiftInstanceValuesAddOnRight(instance, result);
			}

			// for (int i = 0; i < outputInstances[0].numInstances(); i++) {
			// double result = classifier.classifyInstance(outputInstances[0]
			// .instance(i));
			// logInfo("Classification: " + i + " should be: "
			// + outputInstances[0].instance(i).value(lastAttr)
			// + " got: " + result);
			// }

			// ***********************************************************************
			// EVALUATION CODE
			// ***********************************************************************
		} catch (Exception e) {
			logErr(e);
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logErr(e);
				}
			}
		}

	}

	private void shiftInstanceValuesAddOnRight(Instance instance, double result) {
		for (int i = 0; i < instance.numAttributes() - 1; i++) {
			double valueToShift = instance.value(i + 1);
			instance.setValue(i, valueToShift);
		}

		instance.setValue(instance.numAttributes() - 1, result);
	}

	public static void log(String level, String message, PrintStream output) {
		if (log) {
			Throwable t = new Throwable();
			StackTraceElement e = t.getStackTrace()[1];
			String[] classNameElements = e.getClassName().split("\\.");
			String className = classNameElements[classNameElements.length - 1];
			output.println(className + "." + e.getMethodName() + "(" + e.getFileName() + ":"
					+ e.getLineNumber() + ")" + " " + level + ": " + message);
		}
	}

	public static void logErr(Throwable t) {
		log(ERROR_MSG_PREFIX, t.getMessage(), System.err);
	}

	public static void logErr(String message) {
		log(ERROR_MSG_PREFIX, message, System.err);
	}

	public static void logInfo(String message) {
		log(INFO_MSG_PREFIX, message, System.out);
	}

	private Instances[] convertInputToSingleAttributesSeries(Instances inputInstances) {

		int numInstances = inputInstances.numInstances();
		int attributesNum = inputInstances.numAttributes();

		Instances[] retVal = new Instances[attributesNum];

		// for each attribute create a set of instances
		int dataSetAttributesNum = windowSize + 1;
		for (int attributeIdx = 0; attributeIdx < attributesNum; attributeIdx++) {

			// create attribute description
			FastVector attributes = new FastVector(dataSetAttributesNum);
			for (int i = 0; i < windowSize; i++) {
				attributes.addElement(new Attribute(ATTRIBUTE_NAME_PREFIX + i));
			}
			attributes.addElement(new Attribute(CLASS_ATTRIBUTE_NAME));

			// create instances collection
			Instances instancesForAttr = new Instances(inputInstances.attribute(attributeIdx).name()
					+ RELATION_NAME_SUFFIX, attributes, 0);

			// create instances
			int startInstance = 0;
			while (startInstance + dataSetAttributesNum <= numInstances) {

				// fill instances with data
				Instance newInstance = new Instance(dataSetAttributesNum);
				for (int j = 0; j < dataSetAttributesNum; j++) {
					Instance inputInstance = inputInstances.instance(j + startInstance);
					newInstance.setValue(j, inputInstance.value(attributeIdx));
				}

				instancesForAttr.add(newInstance);
				startInstance++;
			}

			retVal[attributeIdx] = instancesForAttr;
		}
		return retVal;
	}

	private Instances[] convertInputToMultipleAttributesSeries(Instances inputInstances) {
		int numInstances = inputInstances.numInstances();
		int attributesNum = inputInstances.numAttributes();

		Instances[] retVal = new Instances[attributesNum];

		// for each attribute create a set of instances
		int dataSetAttributesNum = windowSize * attributesNum + 1;
		for (int attributeIdx = 0; attributeIdx < attributesNum; attributeIdx++) {

			// create attribute description
			FastVector attributes = new FastVector(dataSetAttributesNum);
			for (int i = 0; i < dataSetAttributesNum - 1; i++) {
				attributes.addElement(new Attribute(inputInstances.attribute(i % windowSize).name()
						+ UNDERSCORE + (i / windowSize)));
			}
			attributes.addElement(new Attribute(CLASS_ATTRIBUTE_NAME));

			// create instances collection
			Instances instancesForAttr = new Instances(inputInstances.attribute(attributeIdx).name()
					+ RELATION_NAME_SUFFIX, attributes, 0);

			// create instances
			int startInstance = 0;
			while (startInstance + windowSize < numInstances) {

				// fill instances with data
				Instance newInstance = new Instance(dataSetAttributesNum);
				for (int j = 0; j < windowSize; j++) {
					Instance inputInstance = inputInstances.instance(j + startInstance);
					for (int jj = 0; jj < attributesNum; jj++) {
						newInstance.setValue(j * attributesNum + jj, inputInstance.value(jj));
					}
				}

				newInstance.setValue(dataSetAttributesNum - 1,
						inputInstances.instance(startInstance + windowSize).value(attributeIdx));
				instancesForAttr.add(newInstance);

				startInstance++;
			}

			retVal[attributeIdx] = instancesForAttr;
		}
		return retVal;
	}

	private static void printInstances(Instances[] instancesToPrint, String comment) {
		for (int i = 0; i < instancesToPrint.length; i++) {
			printInstances(instancesToPrint[i], comment + " " + i);
		}
	}

	private static void printInstances(Instances instancesToPrint, String comment) {
		if (instancesToPrint != null) {
			int numInstances = instancesToPrint.numInstances();
			logInfo("*********************************************************************");
			logInfo(comment + ": ");
			logInfo("---------------------------------------------------------------------");
			for (int i = 0; i < numInstances; i++) {
				logInfo("Instance: " + instancesToPrint.instance(i).toString());
			}
			logInfo("*********************************************************************");
		} else {
			logInfo("*********************************************************************");
			logInfo(comment + ": NULL");
			logInfo("*********************************************************************");
		}
	}
}
