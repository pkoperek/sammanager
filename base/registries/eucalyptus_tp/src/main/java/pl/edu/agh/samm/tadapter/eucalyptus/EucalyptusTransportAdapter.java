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

package pl.edu.agh.samm.tadapter.eucalyptus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.ICoreManagement;
import pl.edu.agh.samm.common.core.Resource;
import pl.edu.agh.samm.common.core.ResourceNotRegisteredException;
import pl.edu.agh.samm.common.tadapter.AbstractTransportAdapter;
import pl.edu.agh.samm.common.tadapter.ActionNotSupportedException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeImagesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Image;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceStateName;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class EucalyptusTransportAdapter extends AbstractTransportAdapter {

	private static final Logger logger = LoggerFactory
			.getLogger(EucalyptusTransportAdapter.class);

	public static final String EUCALYPTUS_TRANSPORT_PROPERTY_KEY = "EUCALYPTUSENDPOINT";
	public static final String EUCALYPTUS_ACCESS_KEY = "EUCALYPTUSACCESSKEY";
	public static final String EUCALYPTUS_SECRET_KEY = "EUCALYPTUSSECRETKEY";
	public static final String EUCALYPTUS_KEY_NAME = "EUCALYPTUSKEYNAME";

	private static final String VIRTUAL_NODE_TYPE = "http://www.icsr.agh.edu.pl/samm_1.owl#VirtualNode";
	private static final String CLUSTER_PARAMETER_TYPE = "http://www.icsr.agh.edu.pl/samm_1.owl#ClusterParameter";
	private static final String VIRTUAL_NODE_NAME_PREFIX = "Instance_";

	private static final String IMAGE_TYPE_MACHINE = "machine";
	private static final String IMAGE_STATE_AVAILABLE = "available";

	private static final String ACTION_START_MVCBASIC_VM = "http://www.icsr.agh.edu.pl/samm_1.owl#StartMVCBasicVMAction";
	private static final String ACTION_START_VM = "http://www.icsr.agh.edu.pl/samm_1.owl#StartVMAction";

	private static Set<String> supportedActions;

	static {
		supportedActions = new HashSet<String>();
		supportedActions.add(ACTION_START_MVCBASIC_VM);
		supportedActions.add(ACTION_START_VM);
	}

	private ICoreManagement coreManagement;

	private String mvcBasicImageId;
	private String startVMActionImageId;

	public String getStartVMActionImageId() {
		return startVMActionImageId;
	}

	public void setStartVMActionImageId(String startVMActionImageId) {
		this.startVMActionImageId = startVMActionImageId;
	}

	private Map<Resource, AmazonEC2Client> ec2Clients = new HashMap<Resource, AmazonEC2Client>();

	/**
	 * Find children of cluster controller in fact... Reacts only when in
	 * <code>types</code> list the type of VirtualNode appears.
	 * 
	 * @see pl.edu.agh.samm.common.tadapter.ITransportAdapter#discoverChildren(pl.edu.agh.samm.common.core.Resource,
	 *      java.util.List)
	 */
	@Override
	public void discoverChildren(Resource resource, List<String> types)
			throws Exception {

		if (types.contains(VIRTUAL_NODE_TYPE)) {
			AmazonEC2Client ec2Client = ec2Clients.get(resource);
			if (ec2Client == null) {
				throw new ResourceNotRegisteredException(resource.getUri());
			}

			DescribeInstancesResult result = ec2Client.describeInstances();
			List<Reservation> reservations = result.getReservations();
			Set<Instance> instances = new HashSet<Instance>();

			for (Reservation reservation : reservations) {
				List<Instance> instancesFromReservation = reservation
						.getInstances();
				for (Instance instance : instancesFromReservation) {
					if (instance.getState().getName()
							.equals(InstanceStateName.Running.toString())) {
						instances.add(instance);
					}
				}
			}

			Map<String, String> childrenTypes = new HashMap<String, String>();
			Map<String, Map<String, Object>> childrenProperties = new HashMap<String, Map<String, Object>>();

			for (Instance instance : instances) {
				String child = resource.getUri() + "/"
						+ VIRTUAL_NODE_NAME_PREFIX + instance.getInstanceId();
				Map<String, Object> newProperties = new HashMap<String, Object>(
						resource.getProperties());
				childrenProperties.put(child, newProperties);
				childrenTypes.put(child, VIRTUAL_NODE_TYPE);
			}
			fireNewResourcesEvent(resource.getUri(), childrenTypes,
					childrenProperties);
		}
	}

	@Override
	public void executeAction(Action actionToExecute)
			throws ActionNotSupportedException {
		if (!supportedActions.contains(actionToExecute.getActionURI())) {
			throw new ActionNotSupportedException();
		}

		if (ACTION_START_MVCBASIC_VM.equalsIgnoreCase(actionToExecute
				.getActionURI())) {
			String clusterInstanceUri = actionToExecute.getParameterValues()
					.get(CLUSTER_PARAMETER_TYPE);
			Resource resource = null;
			for (Map.Entry<Resource, AmazonEC2Client> entry : ec2Clients
					.entrySet()) {
				if (entry.getKey().getUri()
						.equalsIgnoreCase(clusterInstanceUri)) {
					resource = entry.getKey();
					break;
				}
			}
			if (resource == null) {
				logger.info("Tried to execute " + ACTION_START_MVCBASIC_VM
						+ " action but node " + clusterInstanceUri
						+ " is not Eucalyptus-enabled");
				throw new ActionNotSupportedException();
			}
			try {
				startMVCBasicTomcatInstanceAction(resource, mvcBasicImageId,
						"c1.medium");
			} catch (Exception e) {
				logger.error(e.toString(), e);
				throw new RuntimeException(e);
			}
		} else if (ACTION_START_VM.equalsIgnoreCase(actionToExecute
				.getActionURI())) {
			logger.info("Executing: " + ACTION_START_VM + " image id: "
					+ startVMActionImageId);
		}

	}

	@Override
	public Object getCapabilityValue(Resource resource, String capabilityType)
			throws Exception {
		// not monitoring eucalyptus
		return null;
	}

	@Override
	public boolean hasCapability(Resource resource, String capabilityType)
			throws Exception {
		// not monitoring eucalyptus
		return false;
	}

	@Override
	public boolean isActionSupported(String actionUri) {
		return supportedActions.contains(actionUri);
	}

	@Override
	public boolean isURISupported(Resource resource) {
		return resource.hasProperty(EUCALYPTUS_TRANSPORT_PROPERTY_KEY);
	}

	@Override
	public void registerResource(Resource resource) throws Exception {
		Object endpointURL = resource
				.getProperty(EUCALYPTUS_TRANSPORT_PROPERTY_KEY);

		Object accessKey = resource.getProperty(EUCALYPTUS_ACCESS_KEY);
		Object secretKey = resource.getProperty(EUCALYPTUS_SECRET_KEY);

		AWSCredentials ec2Credentials = new BasicAWSCredentials(
				accessKey.toString(), secretKey.toString());

		AmazonEC2Client ec2Client = new AmazonEC2Client(ec2Credentials);
		ec2Client.setEndpoint(endpointURL.toString());

		ec2Clients.put(resource, ec2Client);
	}

	@Override
	public void unregisterResource(Resource resource) {
		AmazonEC2Client ec2Client = ec2Clients.get(resource);
		if (ec2Client != null) {
			// not expected to be invoked ;)
			ec2Client.shutdown();
		}
		ec2Clients.remove(resource);
	}

	private void startMVCBasicTomcatInstanceAction(Resource resource,
			String machineInstanceId, String instanceType) throws Exception {
		Instance instance = startOneInstanceAction(resource, machineInstanceId,
				instanceType);
		EC2Util.waitForURL(instance.getPublicDnsName(), 8080, "/mvc-basic");
		logger.info("Tomcat started on instance " + instance.getInstanceId());
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JMXURL",
				"service:jmx:rmi://" + instance.getPublicDnsName()
						+ ":9999/jndi/rmi://" + instance.getPublicDnsName()
						+ ":9999/jmxrmi");

		coreManagement.registerResource(resource.getUri() + "/"
				+ VIRTUAL_NODE_NAME_PREFIX + instance.getInstanceId(),
				VIRTUAL_NODE_TYPE, parameters);
	}

	/**
	 * starts new VM instance, <b>does not add new resource to core</b>
	 * 
	 * @param resource
	 * @param machineInstanceId
	 * @param instanceType
	 * @return
	 * @throws Exception
	 */
	private Instance startOneInstanceAction(Resource resource,
			final String machineInstanceId, String instanceType)
			throws Exception {
		AmazonEC2Client client = ec2Clients.get(resource);

		DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest();
		List<String> imageIds = new LinkedList<String>();
		imageIds.add(machineInstanceId);
		describeImagesRequest.setImageIds(imageIds);
		List<Image> images = client.describeImages(describeImagesRequest)
				.getImages();
		if (images.size() != 1) {
			throw new Exception("Found " + images.size() + " images of id: "
					+ machineInstanceId);
		}
		Image image = images.get(0);
		if (!IMAGE_TYPE_MACHINE.equals(image.getImageType())) {
			throw new Exception("Provided image type is not machine!");
		}
		if (!IMAGE_STATE_AVAILABLE.equals(image.getState())) {
			throw new Exception("Provided image state is not "
					+ IMAGE_STATE_AVAILABLE);
		}

		RunInstancesRequest command = new RunInstancesRequest();
		command.setImageId(image.getImageId());
		command.setInstanceType(instanceType);
		command.setKernelId(image.getKernelId());
		command.setMaxCount(1);
		command.setMinCount(1);
		command.setRamdiskId(image.getRamdiskId());
		command.setKeyName(resource.getProperty(EUCALYPTUS_KEY_NAME).toString());
		RunInstancesResult result = client.runInstances(command);
		List<Instance> instances = result.getReservation().getInstances();
		if (instances.size() < 1) {
			logger.error("Something bad happend while running VM instance");
		}
		Instance instance = instances.get(0);
		instance = EC2Util.waitForRunningState(client, instance);

		logger.info("Started new instance of image " + machineInstanceId
				+ "! InstanceId = " + instance.getInstanceId());
		instance = EC2Util.waitForPublicDNS(client, instance);
		logger.info("Instance IP address is: " + instance.getPublicDnsName());
		return instance;
	}

	/**
	 * @param coreManagement
	 *            the coreManagement to set
	 */
	public void setCoreManagement(ICoreManagement coreManagement) {
		this.coreManagement = coreManagement;
	}

	/**
	 * @param mvcBasicImageId
	 *            the petClinicImageId to set
	 */
	public void setMVCBasicImageId(String mvcBasicImageId) {
		this.mvcBasicImageId = mvcBasicImageId;
	}

	@Override
	public boolean isResourceRegistered(Resource resource) {
		return ec2Clients.containsKey(resource);
	}
}
