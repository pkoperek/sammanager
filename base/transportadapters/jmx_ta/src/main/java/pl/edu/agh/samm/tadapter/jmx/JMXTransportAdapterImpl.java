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

package pl.edu.agh.samm.tadapter.jmx;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.Resource;
import pl.edu.agh.samm.common.core.ResourceNotRegisteredException;
import pl.edu.agh.samm.common.impl.StringHelper;
import pl.edu.agh.samm.common.tadapter.AbstractTransportAdapter;
import pl.edu.agh.samm.common.tadapter.ActionNotSupportedException;

/**
 * Engine for the JMX client communication.
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class JMXTransportAdapterImpl extends AbstractTransportAdapter {

	private static final String CAPABILITY_KEY_PART = ".capability.";

	private static final String INSTANCE_NAME_PREFIX = ".instance.prefix";

	private static final String INSTANCE_QUERY_SUFFIX = ".instance.name";

	public static final String JMX_TRANSPORT_PROPERTY_KEY = "JMXURL";

	private static final Logger logger = LoggerFactory
			.getLogger(JMXTransportAdapterImpl.class);

	private Map<String, MBeanServerConnection> connections = new HashMap<String, MBeanServerConnection>();
	private ExecutorService executorService;

	private JMXAdapterConfigurator jmxAdapterConfigurator;

	public JMXAdapterConfigurator getJmxAdapterConfigurator() {
		return jmxAdapterConfigurator;
	}

	public void setJmxAdapterConfigurator(
			JMXAdapterConfigurator jmxAdapterConfigurator) {
		this.jmxAdapterConfigurator = jmxAdapterConfigurator;
	}

	public void destroy() {
		executorService.shutdown();
	}

	public void init() throws FileNotFoundException, IOException {
		executorService = Executors.newCachedThreadPool();
	}

	private String getProperty(String propertyKey) {
		return jmxAdapterConfigurator.getProperty(propertyKey);
	}

	private Object getAttribute(MBeanServerConnection beanServerConnection,
			String query, String uri) throws Exception {
		return getAttribute(beanServerConnection, query, uri, null);
	}

	private Object getAttribute(MBeanServerConnection beanServerConnection,
			String query, String uri, String prefixToRemove) throws Exception {

		Object attributeValue = null;

		String[] queryElements = query.split("\\|");
		ObjectName name = new ObjectName(queryElements[0]);

		if (queryElements[1].contains("(") && queryElements[1].contains(")")) {
			// use invoke(...) rather than getAttribute(...)
			int lparenIdx = queryElements[1].indexOf('(');
			int rparenIdx = queryElements[1].indexOf(')', lparenIdx);

			if (lparenIdx < rparenIdx && rparenIdx < queryElements[1].length()) {
				String argumentsPart = queryElements[1].substring(
						lparenIdx + 1, rparenIdx);
				String formalPart = queryElements[1].substring(rparenIdx + 2); // skip
				// rparen
				// and
				// trailing comma

				String operationName = queryElements[1].substring(0, lparenIdx);
				String[] argumentsStr = argumentsPart.split(",");
				List<Object> arguments = new ArrayList<Object>();

				// cut off the last part of uri - this is our instance name
				int instanceIdx = uri.lastIndexOf('/');
				String instanceName = uri.substring(instanceIdx + 1);

				// if there was a prefix added - remove it first
				if (prefixToRemove != null) {
					instanceName = instanceName.replaceAll(prefixToRemove, "");
				}

				for (String argument : argumentsStr) {
					// trim
					argument = argument.trim();

					// if @instance is on the list - substitute it
					argument = argument.replaceAll("@instance", instanceName);

					// check if the argument is a letter
					try {
						Long longValue = Long.valueOf(argument);
						arguments.add(longValue);
					} catch (NumberFormatException e) {
						arguments.add(argument);
					}
				}
				String[] signature = formalPart.split(",");

				if (logger.isDebugEnabled()) {
					logger.info("About to invoke: " + operationName + " "
							+ arguments + " " + Arrays.toString(signature)
							+ " for instance: " + uri);
				}
				attributeValue = beanServerConnection.invoke(name,
						operationName, arguments.toArray(), signature);
			}

		} else {
			// name == queryElements[0]
			attributeValue = beanServerConnection.getAttribute(name,
					queryElements[1]);
		}

		attributeValue = getScalarValue(attributeValue, query, 2);

		return attributeValue;
	}

	/**
	 * Drills down through returned attributes from JMX system using more
	 * specific query elements. For example if value returned from JMX is of
	 * type {@link TabularData} then <code>query[queryLevel]</code> is used to
	 * obtain more specific value and calls this method recursively until no
	 * more specific value can be found.
	 * 
	 * @param attributeValue
	 * @param query
	 * @param queryLevel
	 * @return
	 */
	private Object getScalarValue(Object attributeValue, String query,
			int queryLevel) {
		final String[] queryElements = query.split("\\|");
		Object retVal = attributeValue;

		if ((attributeValue != null) && (queryElements.length > queryLevel)) {
			String queryElement = queryElements[queryLevel];
			Object value = null;

			if (attributeValue instanceof CompositeData) {
				CompositeData compositeData = (CompositeData) attributeValue;
				value = compositeData.get(queryElement);
			} else if (attributeValue instanceof TabularData) {
				TabularData tabularData = (TabularData) attributeValue;
				value = tabularData.get(new Object[] { queryElement });
			} else if (attributeValue instanceof Map) {
				Map map = (Map) attributeValue;
				value = map.get(new Object[] { queryElement });
			} else if (attributeValue.getClass().isArray()) {
				int index = -1;
				try {
					index = Integer.parseInt(queryElement);
				} catch (NumberFormatException e) {
					logger.error("Invalid value of array index: "
							+ queryElement + " in query: " + query);
				}
				if (index != -1) {
					try {
						value = Array.get(attributeValue, index);
					} catch (ArrayIndexOutOfBoundsException e) {
						logger.error("", e);
					}
				}
			}
			if (value != null) {
				retVal = getScalarValue(value, query, queryLevel + 1);
			}
		}

		return retVal;
	}

	/**
	 * In case type = jvm fullInstance should be e.g.: cluster1.node2.jvm1. In
	 * all other cases only cluster name and node name are required.
	 * 
	 * @param fullInstance
	 * @param type
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object getCapabilityValue(Resource resource, String capabilityType)
			throws Exception {

		String capabilityName = StringHelper.getNameFromURI(capabilityType);

		logger.info("Get capability value of instance: " + resource.getUri()
				+ " type: " + resource.getType() + " name: " + capabilityName);

		String lcType = StringHelper.getNameFromURI(resource.getType())
				.toLowerCase();
		String query = getProperty(lcType + CAPABILITY_KEY_PART
				+ capabilityName);

		if (query == null) {
			throw new RuntimeException("No query defined for: " + lcType + " "
					+ capabilityName);
		}

		String prefixToRemove = getProperty(lcType + INSTANCE_NAME_PREFIX);

		MBeanServerConnection connection = getConnectionForResource(resource);
		Object retVal = getAttribute(connection, query, resource.getUri(),
				prefixToRemove);

		logger.info("Returning value: " + retVal);

		fireNewCapabilityValueEvent(capabilityType, resource.getUri(),
				resource.getType(), retVal);

		return retVal;
	}

	@Override
	public String toString() {
		return "JMX Transport Adapter";
	}

	@Override
	public boolean hasCapability(Resource resource, String capabilityType)
			throws Exception {
		Object retVal = getCapabilityValue(resource, capabilityType);
		return retVal != null;
	}

	@Override
	public void unregisterResource(Resource resource) {
		logger.info("Unregistering URI: " + resource.getUri());
		Object transportUri = resource.getProperty(JMX_TRANSPORT_PROPERTY_KEY);

		connections.remove(transportUri);
	}

	@Override
	public void registerResource(Resource resource) throws Exception {
		logger.info("Registering URI: " + resource.getUri() + " "
				+ resource.getProperties().values().toString());

		Object transportUri = resource.getProperty(JMX_TRANSPORT_PROPERTY_KEY);

		if (!connections.containsKey(transportUri)) {
			JMXServiceURL url = new JMXServiceURL(transportUri.toString());
			final JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

			final MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			connections.put(transportUri.toString(), mbsc);

			try {
				mbsc.createMBean(
						"org.crossgrid.wp3.monitoring.jims.mbeans.Linux.SystemInformation",
						new ObjectName(
								"linuxMonitoringExtension:type=SystemInformation"));
			} catch (InstanceAlreadyExistsException e) {
				// fall-through - if this mbean already exists, everything is ok
			} catch (ReflectionException e) {
				logger.warn("Error creating jims mBean, some information will be unavailable");
			}
		}
	}

	@Override
	public boolean isURISupported(Resource resource) {
		return resource.hasProperty(JMX_TRANSPORT_PROPERTY_KEY);
	}

	private MBeanServerConnection getConnectionForResource(Resource resource)
			throws ResourceNotRegisteredException {
		Object transportUri = resource.getProperty(JMX_TRANSPORT_PROPERTY_KEY);
		MBeanServerConnection mBeanServerConnection = connections
				.get(transportUri);

		if (mBeanServerConnection == null) {
			throw new ResourceNotRegisteredException(resource.getUri());
		}

		return mBeanServerConnection;
	}

	@Override
	public void discoverChildren(Resource resource, List<String> types)
			throws Exception {
		logger.info("Discover children of: " + resource);

		MBeanServerConnection mBeanServerConnection = getConnectionForResource(resource);

		Map<String, String> childrenTypes = new HashMap<String, String>();
		Map<String, Map<String, Object>> childrenProperties = new HashMap<String, Map<String, Object>>();

		for (String type : types) {
			List<String> childrenOfType = discoverChildrenOfType(
					mBeanServerConnection, resource.getUri(),
					type.toLowerCase());
			for (String child : childrenOfType) {
				child = resource.getUri() + "/" + child;
				childrenTypes.put(child, type);
				Map<String, Object> newProperties = new HashMap<String, Object>(
						resource.getProperties());
				childrenProperties.put(child, newProperties);
			}
		}

		fireNewResourcesEvent(resource.getUri(), childrenTypes,
				childrenProperties);
	}

	// @Override
	// public Collection<? extends String> discoverResourceCapabilities(
	// Resource resource, List<String> capabilitiesTypes)
	// throws ResourceNotRegisteredException {
	//
	// MBeanServerConnection mBeanServerConnection =
	// getConnectionForResource(resource);
	//
	// return null;
	// }

	private List<String> discoverChildrenOfType(
			MBeanServerConnection mBeanServerConnection, String uri, String type)
			throws Exception {

		logger.info("Discovering for type: " + type);

		String nameOfType = StringHelper.getNameFromURI(type);

		List<String> instances = new ArrayList<String>();

		// query for names
		String query = getProperty(nameOfType + INSTANCE_QUERY_SUFFIX);

		if (query == null) {
			return instances;
			// throw new RuntimeException("No query for: " + type);
		}

		// prefix of instance name
		String instanceNamePrefix = getProperty(nameOfType
				+ INSTANCE_NAME_PREFIX);

		instanceNamePrefix = (instanceNamePrefix == null) ? ""
				: instanceNamePrefix;
		Object instanceNameObject = null;
		try {
			instanceNameObject = getAttribute(mBeanServerConnection, query, uri);

			// if returned value is an array ...
			// if (instanceNameObject != null) {
			if (instanceNameObject.getClass().isArray()) {
				int size = Array.getLength(instanceNameObject);
				for (int i = 0; i < size; i++) {
					Object instanceObj = Array.get(instanceNameObject, i);
					instances.add(instanceNamePrefix + instanceObj);
				}
			} else {
				// ... otherwise ...
				instances.add(instanceNamePrefix
						+ instanceNameObject.toString());
			}
			// }
		} catch (Exception e) {
			logger.warn("Exception thrown when discovering: " + type
					+ ". No instances of this type will be found.");
			logger.debug("Exception thrown when discovering: " + type
					+ ". No instances of this type will be found.", e);
		}

		return instances;
	}

	@Override
	public void executeAction(Action actionToExecute)
			throws ActionNotSupportedException {
		throw new ActionNotSupportedException();
	}

	@Override
	public boolean isActionSupported(String actionUri) {
		return false;
	}

	@Override
	public boolean isResourceRegistered(Resource resource) {
		return connections.containsKey(resource
				.getProperty(JMX_TRANSPORT_PROPERTY_KEY));
	}

}
