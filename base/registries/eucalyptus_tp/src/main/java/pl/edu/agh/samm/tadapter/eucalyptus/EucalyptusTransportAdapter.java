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
import pl.edu.agh.samm.common.core.ResourceAlreadyRegisteredException;
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
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class EucalyptusTransportAdapter extends AbstractTransportAdapter {

	private static final Logger logger = LoggerFactory
			.getLogger(EucalyptusTransportAdapter.class);

	// transport adapter resource parameters
	public static final String EUCALYPTUS_KEY_NAME = "EUCALYPTUSKEYNAME";
	public static final String EUCALYPTUS_TRANSPORT_PROPERTY_KEY = "EUCALYPTUSENDPOINT";
	public static final String EUCALYPTUS_ACCESS_KEY = "EUCALYPTUSACCESSKEY";
	public static final String EUCALYPTUS_SECRET_KEY = "EUCALYPTUSSECRETKEY";
	
	// action parameters 
	public static final String EUCALYPTUS_CLUSTER_PARAMETER = "EUCALYPTUS_CLUSTER_RESOURCE";
	public static final String EUCALYPTUS_IMAGE_ID = "EUCALYPTUS_IMAGE_ID";
	public static final String EUCALYPTUS_INSTANCE_TYPE = "EUCALYPTUS_INSTANCE_TYPE";
	
	public static final String EUCALYPTUS_INSTANCE_ID = "EUCALYPTUS_INSTANCE_ID";
	
	// other contants
	private static final String VIRTUAL_NODE_TYPE = "http://www.icsr.agh.edu.pl/samm_1.owl#VirtualNode";
	private static final String VIRTUAL_NODE_NAME_PREFIX = "Instance_";
	private static final String IMAGE_TYPE_MACHINE = "machine";
	private static final String IMAGE_STATE_AVAILABLE = "available";

	private static final String ACTION_START_MVCBASIC_VM = "http://www.icsr.agh.edu.pl/samm_1.owl#StartMVCBasicVMAction";
	private static final String ACTION_START_VM = "http://www.icsr.agh.edu.pl/samm_1.owl#StartVMAction";
	private static final String ACTION_STOP_VM = "http://www.icsr.agh.edu.pl/samm_1.owl#StopVMAction";

	private static Set<String> supportedActions;

	static {
		supportedActions = new HashSet<String>();
		supportedActions.add(ACTION_START_MVCBASIC_VM);
		supportedActions.add(ACTION_START_VM);
		supportedActions.add(ACTION_STOP_VM);
	}

	private ICoreManagement coreManagement;

	private String mvcBasicImageId;

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
	public void executeAction(Action actionToExecute) throws Exception {
		if (!supportedActions.contains(actionToExecute.getActionURI())) {
			throw new ActionNotSupportedException();
		}

		logger.info("Executing: " + ACTION_START_VM);
		if (ACTION_START_MVCBASIC_VM.equalsIgnoreCase(actionToExecute
				.getActionURI())) {
			Resource resource = getClusterResource(actionToExecute);
			String instanceType = getInstanceType(actionToExecute);
			try {
				startMVCBasicTomcatInstanceAction(resource, mvcBasicImageId,
						instanceType);
			} catch (Exception e) {
				logger.error(e.toString(), e);
				throw new RuntimeException(e);
			}
		} else if (ACTION_START_VM.equalsIgnoreCase(actionToExecute
				.getActionURI())) {
			Resource clusterResource = getClusterResource(actionToExecute);
			String imageId = getImageId(actionToExecute);
			String instanceType = getInstanceType(actionToExecute);
			startOneInstanceAction(clusterResource, imageId, instanceType);
		} else if (ACTION_STOP_VM.equalsIgnoreCase(actionToExecute
				.getActionURI())) {
			Resource clusterResource = getClusterResource(actionToExecute);
			// String instanceId = getInstanceId(actionToExecute);
			String imageId = (String) clusterResource.getProperty(EUCALYPTUS_IMAGE_ID);
			String instanceId = getRandomInstance(clusterResource, imageId);
			if (instanceId == null) {
				logger.warn("Can't stop any instance! There are no instances running with imageId = '"
						+ imageId + "'");
			} else {
				stopOneInstanceAction(clusterResource, instanceId);
			}
		}
		logger.info("Executed: " + ACTION_START_VM);
	}

	private String getRandomInstance(Resource clusterResource, String imageId) {
		AmazonEC2Client client = this.ec2Clients.get(clusterResource);

		DescribeInstancesResult result = client.describeInstances();
		List<Instance> instances = getInstancesByImageId(result, imageId);

		String instanceId = null;

		if (instances.size() > 0) {
			int idx = (int) Math
					.round((Math.random() * (instances.size() - 1)));
			Instance instance = instances.get(idx);
			instanceId = instance.getInstanceId();
		}

		return instanceId;
	}

	private List<Instance> getInstancesByImageId(
			DescribeInstancesResult result, String imageId) {
		List<Instance> instances = new LinkedList<Instance>();
		for (Reservation reservation : result.getReservations()) {
			for (Instance instance : reservation.getInstances()) {
				if (imageId.equals(instance.getImageId())) {
					instances.add(instance);
				}
			}
		}
		return instances;
	}

	private String getInstanceId(Action actionToExecute) {
		String instanceId = actionToExecute.getParameterValues().get(
				EUCALYPTUS_INSTANCE_ID);
		return instanceId;
	}

	private void stopOneInstanceAction(Resource clusterResource,
			String instanceId) {
		AmazonEC2Client client = ec2Clients.get(clusterResource);

		logger.info("Stopping instance: ");
		StopInstancesRequest stopInstancesRequest = new StopInstancesRequest();
		List<String> instancesToStop = new LinkedList<String>();
		instancesToStop.add(instanceId);
		stopInstancesRequest.setInstanceIds(instancesToStop);

		StopInstancesResult result = client.stopInstances(stopInstancesRequest);
		logger.info("Instances stopped: " + instancesToStop);
	}

	private String getInstanceType(Action actionToExecute) {
		String instanceType = actionToExecute.getParameterValues().get(
				EUCALYPTUS_INSTANCE_TYPE);
		return instanceType;
	}

	private String getImageId(Action actionToExecute) {
		String imageId = actionToExecute.getParameterValues().get(EUCALYPTUS_IMAGE_ID);
		return imageId;
	}

	private Resource getClusterResource(Action actionToExecute) {
		String clusterInstanceUri = actionToExecute.getParameterValues().get(
				EUCALYPTUS_CLUSTER_PARAMETER);
		Resource resource = null;
		for (Map.Entry<Resource, AmazonEC2Client> entry : ec2Clients.entrySet()) {
			if (entry.getKey().getUri().equalsIgnoreCase(clusterInstanceUri)) {
				resource = entry.getKey();
				break;
			}
		}

		if (resource == null) {
			String msg = "Tried to execute " + actionToExecute.getActionURI()
					+ " action but node " + clusterInstanceUri
					+ " is not Eucalyptus-enabled!";
			logger.info(msg);
			throw new IllegalArgumentException(msg);
		}

		return resource;
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
		registerNewInstance(resource, instance);
	}

	private void registerNewInstance(Resource clusterResource, Instance instance)
			throws ResourceAlreadyRegisteredException {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("JMXURL",
				"service:jmx:rmi://" + instance.getPublicDnsName()
						+ ":9999/jndi/rmi://" + instance.getPublicDnsName()
						+ ":9999/jmxrmi");

		parameters.put(EUCALYPTUS_INSTANCE_ID, instance.getInstanceId());

		coreManagement.registerResource(new Resource(clusterResource.getUri()
				+ "/" + VIRTUAL_NODE_NAME_PREFIX + instance.getInstanceId(),
				VIRTUAL_NODE_TYPE, parameters));
	}

	/**
	 * starts new VM instance, <b>does not add new resource to core</b>
	 * 
	 * @param resource
	 * @param imageId
	 * @param instanceType
	 * @return
	 * @throws Exception
	 */
	private Instance startOneInstanceAction(Resource resource,
			final String imageId, String instanceType) throws Exception {
		AmazonEC2Client client = ec2Clients.get(resource);

		Image image = getImageByImageID(client, imageId);
		if (!IMAGE_TYPE_MACHINE.equals(image.getImageType())) {
			throw new RuntimeException("Provided image type is not machine!");
		}
		if (!IMAGE_STATE_AVAILABLE.equals(image.getState())) {
			throw new RuntimeException("Provided image state is not "
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

		logger.info("Started new instance of image " + imageId
				+ "! InstanceId = " + instance.getInstanceId());
		instance = EC2Util.waitForPublicDNS(client, instance);
		logger.info("Instance IP address is: " + instance.getPublicDnsName());
		return instance;
	}

	private Image getImageByImageID(AmazonEC2Client client, String imageId) {
		DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest();
		List<String> imageIds = new LinkedList<String>();
		imageIds.add(imageId);
		describeImagesRequest.setImageIds(imageIds);
		List<Image> images = client.describeImages(describeImagesRequest)
				.getImages();
		if (images.size() != 1) {
			throw new RuntimeException("Found " + images.size()
					+ " images of id: " + imageId);
		}
		Image image = images.get(0);
		return image;
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
