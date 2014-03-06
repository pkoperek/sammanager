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

import pl.edu.agh.samm.api.action.Action;
import pl.edu.agh.samm.api.core.Resource;
import pl.edu.agh.samm.api.core.ResourceAlreadyRegisteredException;
import pl.edu.agh.samm.api.core.ResourceNotRegisteredException;
import pl.edu.agh.samm.api.tadapter.AbstractTransportAdapter;
import pl.edu.agh.samm.api.tadapter.ActionNotSupportedException;

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
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 */
public class EucalyptusTransportAdapter extends AbstractTransportAdapter {

    private static final Logger logger = LoggerFactory.getLogger(EucalyptusTransportAdapter.class);

    // transport adapter resource parameters
    public static final String EUCALYPTUS_KEY_NAME = "EUCALYPTUSKEYNAME";
    public static final String EUCALYPTUS_TRANSPORT_PROPERTY_KEY = "EUCALYPTUSENDPOINT";
    public static final String EUCALYPTUS_ACCESS_KEY = "EUCALYPTUSACCESSKEY";
    public static final String EUCALYPTUS_SECRET_KEY = "EUCALYPTUSSECRETKEY";
    public static final String EUCALYPTUS_IMAGE_TO_OBSERVE_ID = "EUCALYPTUS_IMAGE_TO_OBSERVE_ID";

    // start action parameters
    public static final String EUCALYPTUS_CLUSTER_PARAMETER = "EUCALYPTUS_CLUSTER_RESOURCE";
    public static final String EUCALYPTUS_IMAGE_ID = "EUCALYPTUS_IMAGE_ID";
    public static final String EUCALYPTUS_INSTANCE_TYPE = "EUCALYPTUS_INSTANCE_TYPE";
    public static final String EUCALYPTUS_STARTVM_USERDATA = "EUCALYPTUS_STARTVM_USERDATA";
    public static final String EUCALYPTUS_MAX_VMS = "EUCALYPTUS_MAX_VMS";

    // stop action parameters
    public static final String EUCALYPTUS_INSTANCE_ID = "EUCALYPTUS_INSTANCE_ID";
    public static final String EUCALYPTUS_MIN_VMS = "EUCALYPTUS_MIN_VMS";

    // other contants
    private static final String SLAVE_NODE_TYPE = "http://www.icsr.agh.edu.pl/samm_1.owl#SlaveNode";
    private static final String VIRTUAL_NODE_TYPE = "http://www.icsr.agh.edu.pl/samm_1.owl#VirtualNode";
    private static final String VIRTUAL_NODE_NAME_PREFIX = "Instance_";
    private static final String IMAGE_TYPE_MACHINE = "machine";
    private static final String IMAGE_STATE_AVAILABLE = "available";

    private static final String ACTION_START_MVCBASIC_VM = "http://www.icsr.agh.edu.pl/samm_1.owl#StartMVCBasicVMAction";
    private static final String ACTION_START_VM = "http://www.icsr.agh.edu.pl/samm_1.owl#StartVMAction";
    private static final String ACTION_STOP_VM = "http://www.icsr.agh.edu.pl/samm_1.owl#StopVMAction";

    private static final String IMAGE_INSTANCES_CAPABILITY = "http://www.icsr.agh.edu.pl/samm_1.owl#ImageInstancesCapability";

    private static final String EUCALYPTUS_CLUSTER_TYPE = "http://www.icsr.agh.edu.pl/samm_1.owl#EucalyptusCluster";

    private static Set<String> supportedActions;

    static {
        supportedActions = new HashSet<String>();
        supportedActions.add(ACTION_START_MVCBASIC_VM);
        supportedActions.add(ACTION_START_VM);
        supportedActions.add(ACTION_STOP_VM);
    }

    private String mvcBasicImageId;

    private Map<Resource, AmazonEC2Client> ec2Clients = new HashMap<Resource, AmazonEC2Client>();

    /**
     * Find children of cluster controller in fact... Reacts only when in
     * <code>types</code> list the type of VirtualNode appears.
     *
     * @see pl.edu.agh.samm.api.tadapter.ITransportAdapter#discoverChildren(pl.edu.agh.samm.api.core.Resource,
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
                List<Instance> instancesFromReservation = reservation.getInstances();
                for (Instance instance : instancesFromReservation) {
                    if (instance.getState().getName().equals(InstanceStateName.Running.toString())) {
                        instances.add(instance);
                    }
                }
            }

            Map<String, String> childrenTypes = new HashMap<String, String>();
            Map<String, Map<String, Object>> childrenProperties = new HashMap<String, Map<String, Object>>();

            for (Instance instance : instances) {
                String child = resource.getUri() + "/" + VIRTUAL_NODE_NAME_PREFIX + instance.getInstanceId();
                Map<String, Object> newProperties = new HashMap<String, Object>(resource.getProperties());
                childrenProperties.put(child, newProperties);
                childrenTypes.put(child, VIRTUAL_NODE_TYPE);
            }
            fireNewResourcesEvent(resource.getUri(), childrenTypes, childrenProperties);
        }
    }

    @Override
    public void executeAction(Action actionToExecute) throws Exception {
        if (!supportedActions.contains(actionToExecute.getActionURI())) {
            throw new ActionNotSupportedException();
        }

        logger.info("Executing: " + actionToExecute.getActionURI());
        if (ACTION_START_MVCBASIC_VM.equalsIgnoreCase(actionToExecute.getActionURI())) {
            Resource resource = getClusterResource(actionToExecute);
            String instanceType = getInstanceType(actionToExecute);
            try {
                startMVCBasicTomcatInstanceAction(resource, mvcBasicImageId, instanceType);
            } catch (Exception e) {
                logger.error(e.toString(), e);
                throw new RuntimeException(e);
            }
        } else if (ACTION_START_VM.equalsIgnoreCase(actionToExecute.getActionURI())) {
            Resource clusterResource = getClusterResource(actionToExecute);
            String imageId = getImageId(actionToExecute);
            String instanceType = getInstanceType(actionToExecute);
            String userData = getUserData(actionToExecute);

            Integer maxVMs = getMaxVMs(actionToExecute);
            int instancesNumber = getInstancesNumber(clusterResource, imageId);

            Instance instance = null;
            if (maxVMs != null) {
                logger.info("Currently running instances: " + instancesNumber + " max: " + maxVMs);
                if (instancesNumber < maxVMs) {
                    logger.info("Starting new instance!");
                    instance = startOneInstanceAction(clusterResource, imageId, instanceType, userData);
                } else {
                    logger.info("Too much instances running! Can't start a new one!");
                }
            } else {
                logger.info("Maximum number of instances not defined!");
                instance = startOneInstanceAction(clusterResource, imageId, instanceType, userData);
            }

            if (instance != null) {
                logger.info("Registering instance: " + instance);
                registerNewInstance(clusterResource, instance, SLAVE_NODE_TYPE);
            } else {
                logger.info("No instance to register!");
            }

        } else if (ACTION_STOP_VM.equalsIgnoreCase(actionToExecute
                .getActionURI())) {
            Resource clusterResource = getClusterResource(actionToExecute);
            // String instanceId = getInstanceId(actionToExecute);
            String imageId = getImageId(actionToExecute);
            String instanceId = getRandomInstance(clusterResource, imageId);
            if (instanceId == null) {
                logger.warn("Can't stop any instance! There are no instances running with imageId = '" + imageId + "'");
            } else {

                Integer minVMs = getMinVMs(actionToExecute);
                int instancesNumber = getInstancesNumber(clusterResource, imageId);

                if (minVMs != null) {
                    if (instancesNumber > minVMs) {
                        logger.info("Currently running instances: " + instancesNumber + " min: " + minVMs + " Stopping!");
                        stopOneInstanceAction(clusterResource, instanceId);
                    } else {
                        logger.info("Can't stop more instances! (currently runnning: " + instancesNumber + " min: " + minVMs + ")");
                    }
                } else {
                    logger.info("No minimum number of instances defined! Stopping...");
                    stopOneInstanceAction(clusterResource, instanceId);
                }
            }
        }
        logger.info("Executed: " + actionToExecute.getActionURI());
    }

    private Integer getMinVMs(Action actionToExecute) {
        String minVMs = actionToExecute.getParameterValues().get(EUCALYPTUS_MIN_VMS);
        Integer retVal = null;
        if (minVMs != null) {
            retVal = Integer.valueOf(minVMs);
        }
        return retVal;
    }

    private Integer getMaxVMs(Action actionToExecute) {
        String maxVMs = actionToExecute.getParameterValues().get(EUCALYPTUS_MAX_VMS);
        Integer retVal = null;
        if (maxVMs != null) {
            retVal = Integer.valueOf(maxVMs);
        }
        return retVal;
    }

    private String getUserData(Action actionToExecute) {
        return actionToExecute.getParameterValues().get(EUCALYPTUS_STARTVM_USERDATA);
    }

    private int getInstancesNumber(Resource clusterResource, String imageId) {
        AmazonEC2Client client = this.ec2Clients.get(clusterResource);

        DescribeInstancesResult result = client.describeInstances();
        List<Instance> instances = getRunningInstancesByImageId(result, imageId);

        return instances.size();
    }

    private String getRandomInstance(Resource clusterResource, String imageId) {
        AmazonEC2Client client = this.ec2Clients.get(clusterResource);

        DescribeInstancesResult result = client.describeInstances();
        List<Instance> instances = getRunningInstancesByImageId(result, imageId);

        String instanceId = null;

        if (instances.size() > 0) {
            int idx = (int) Math.round((Math.random() * (instances.size() - 1)));
            Instance instance = instances.get(idx);
            instanceId = instance.getInstanceId();
        }

        return instanceId;
    }

    private List<Instance> getRunningInstancesByImageId(DescribeInstancesResult result, String imageId) {
        List<Instance> instances = new LinkedList<Instance>();
        for (Reservation reservation : result.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                if (imageId.equals(instance.getImageId()) && EC2Util.isInstanceRunning(instance)) {
                    instances.add(instance);
                }
            }
        }
        return instances;
    }

    private String getInstanceId(Action actionToExecute) {
        return actionToExecute.getParameterValues().get(EUCALYPTUS_INSTANCE_ID);
    }

    private void stopOneInstanceAction(Resource clusterResource,
                                       String instanceId) {
        AmazonEC2Client client = ec2Clients.get(clusterResource);

        logger.info("Terminating instance: ");
        TerminateInstancesRequest stopInstancesRequest = new TerminateInstancesRequest();
        List<String> instancesToStop = new LinkedList<String>();
        instancesToStop.add(instanceId);
        stopInstancesRequest.setInstanceIds(instancesToStop);

        TerminateInstancesResult result = client.terminateInstances(stopInstancesRequest);
        logger.info("Instances terminated: " + instancesToStop);
    }

    private String getInstanceType(Action actionToExecute) {
        return actionToExecute.getParameterValues().get(EUCALYPTUS_INSTANCE_TYPE);
    }

    private String getImageId(Action actionToExecute) {
        return actionToExecute.getParameterValues().get(EUCALYPTUS_IMAGE_ID);
    }

    private Resource getClusterResource(Action actionToExecute) {
        String clusterInstanceUri = actionToExecute.getParameterValues().get(EUCALYPTUS_CLUSTER_PARAMETER);
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
    public Object getCapabilityValue(Resource resource, String capabilityType) throws Exception {
        Object retVal = null;
        if (capabilityType.equals(IMAGE_INSTANCES_CAPABILITY)) {
            if (resource.getType().equals(EUCALYPTUS_CLUSTER_TYPE)) {
                AmazonEC2Client ec2Client = ec2Clients.get(resource);
                String imageId = (String) resource.getProperty(EUCALYPTUS_IMAGE_TO_OBSERVE_ID);
                if (imageId == null) {
                    throw new IllegalArgumentException("Provided resource doesn't have a defined image ID to observe!");
                }
                retVal = new Integer(getImageInstancesCapability(ec2Client, imageId));
            }
        }
        return retVal;
    }

    private int getImageInstancesCapability(AmazonEC2Client ec2Client, String imageId) {
        int imageCount = 0;

        DescribeInstancesResult result = ec2Client.describeInstances();
        for (Reservation reservation : result.getReservations()) {
            for (Instance instance : reservation.getInstances()) {
                if (instance.getImageId().equals(imageId)
                        && EC2Util.isInstanceRunning(instance)) {
                    imageCount++;
                }
            }
        }

        return imageCount;
    }

    @Override
    public boolean hasCapability(Resource resource, String capabilityType)
            throws Exception {

        if (resource.getType().equals(EUCALYPTUS_CLUSTER_TYPE)) {
            if (capabilityType.equals(IMAGE_INSTANCES_CAPABILITY)) {
                return true;
            }
        }

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
        Object endpointURL = resource.getProperty(EUCALYPTUS_TRANSPORT_PROPERTY_KEY);

        Object accessKey = resource.getProperty(EUCALYPTUS_ACCESS_KEY);
        Object secretKey = resource.getProperty(EUCALYPTUS_SECRET_KEY);

        AWSCredentials ec2Credentials = new BasicAWSCredentials(accessKey.toString(), secretKey.toString());

        AmazonEC2Client ec2Client = new AddressTypeAddingAmazonEC2Client(ec2Credentials);
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

    private void startMVCBasicTomcatInstanceAction(Resource resource, String machineInstanceId, String instanceType) throws Exception {
        Instance instance = startOneInstanceAction(resource, machineInstanceId, instanceType, null);
        EC2Util.waitForURL(instance.getPublicDnsName(), 8080, "/mvc-basic");
        logger.info("Tomcat started on instance " + instance.getInstanceId());
        registerNewInstance(resource, instance, VIRTUAL_NODE_TYPE);
    }

    private void registerNewInstance(Resource clusterResource,
                                     Instance instance, String type)
            throws ResourceAlreadyRegisteredException {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("JMXURL", "service:jmx:rmi://" + instance.getPublicDnsName()
                + ":60001/jndi/rmi://" + instance.getPublicDnsName()
                + ":60000/jmxrmi");

        parameters.put(EUCALYPTUS_INSTANCE_ID, instance.getInstanceId());

        String childName = VIRTUAL_NODE_NAME_PREFIX + instance.getInstanceId();
        Map<String, String> types = new HashMap<String, String>();
        types.put(childName, type);
        Map<String, Map<String, Object>> childrenProperties = new HashMap<String, Map<String, Object>>();
        childrenProperties.put(childName, parameters);
        fireNewResourcesEvent(clusterResource.getUri(), types, childrenProperties);
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
    private Instance startOneInstanceAction(Resource resource, final String imageId, String instanceType, String userData) throws Exception {
        AmazonEC2Client client = ec2Clients.get(resource);

        Image image = getImageByImageID(client, imageId);
        if (!IMAGE_TYPE_MACHINE.equals(image.getImageType())) {
            throw new RuntimeException("Provided image type is not machine!");
        }
        if (!IMAGE_STATE_AVAILABLE.equals(image.getState())) {
            throw new RuntimeException("Provided image state is not " + IMAGE_STATE_AVAILABLE);
        }

        RunInstancesRequest command = new RunInstancesRequest();

        if (userData != null) {
            command.setUserData(userData);
        }

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

        logger.info("Started new instance of image " + imageId + "! InstanceId = " + instance.getInstanceId());
        instance = EC2Util.waitForPublicDNS(client, instance);
        logger.info("Instance IP address is: " + instance.getPublicDnsName());
        return instance;
    }

    private Image getImageByImageID(AmazonEC2Client client, String imageId) {
        DescribeImagesRequest describeImagesRequest = new DescribeImagesRequest();
        List<String> imageIds = new LinkedList<String>();
        imageIds.add(imageId);
        describeImagesRequest.setImageIds(imageIds);
        List<Image> images = client.describeImages(describeImagesRequest).getImages();
        if (images.size() != 1) {
            throw new RuntimeException("Found " + images.size() + " images of id: " + imageId);
        }
        return images.get(0);
    }

    /**
     * @param mvcBasicImageId the petClinicImageId to set
     */
    public void setMVCBasicImageId(String mvcBasicImageId) {
        this.mvcBasicImageId = mvcBasicImageId;
    }

    @Override
    public boolean isResourceRegistered(Resource resource) {
        return ec2Clients.containsKey(resource);
    }
}
