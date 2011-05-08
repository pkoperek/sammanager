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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.Instance;

/**
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class EC2Util {

	private static final Logger logger = LoggerFactory.getLogger(EC2Util.class);

	private static final long SLEEP_CHECK_TIME = 60 * 1000;

	private static final String INSTANCE_DEFAULT_PUBLIC_DNS = "0.0.0.0";

	private static final String INSTANCE_STATE_RUNNING = "running";
	private static final String INSTANCE_STATE_PENDING = "pending";
	private static final String INSTANCE_STATE_SHUTTING_DOWN = "shutting-down";
	private static final Object INSTANCE_STATE_STOPPED = "terminated";

	public static void waitForURL(String hostname, int port, String path)
			throws MalformedURLException, InterruptedException {
		boolean tomcatStarted = false;
		URL url = new URL("http", hostname, port, path);

		while (!tomcatStarted) {
			logger.debug("Waiting for URL to be accessible on VM");
			try {
				HttpURLConnection c = (HttpURLConnection) url.openConnection();
				c.connect();
				tomcatStarted = true;
				c.disconnect();
			} catch (IOException e) {
				logger.debug("URL not yet accessible");
				Thread.sleep(SLEEP_CHECK_TIME);
			}
		}

	}

	public static Instance waitForPublicDNS(AmazonEC2Client ec2Client,
			Instance instance) throws Exception {
		instance = getEc2Instance(ec2Client, instance.getInstanceId());
		while (null == instance.getPublicDnsName()
				|| INSTANCE_DEFAULT_PUBLIC_DNS.equalsIgnoreCase(instance
						.getPublicDnsName())) {
			logger.debug("Waiting for VM to get public IP address");
			Thread.sleep(SLEEP_CHECK_TIME);
			instance = getEc2Instance(ec2Client, instance.getInstanceId());
		}
		return getEc2Instance(ec2Client, instance.getInstanceId());
	}
	
	public static boolean isInstanceRunning(Instance instance) {
		return instance.getState().getName().equals(INSTANCE_STATE_RUNNING);
	}

	public static void waitForStoppedState(AmazonEC2Client ec2Client,
			Instance instance) throws Exception {
		while (INSTANCE_STATE_SHUTTING_DOWN.equals(getEc2Instance(ec2Client,
				instance.getInstanceId()).getState().getName())) {
			logger.debug("Waiting on VM to stop");
			Thread.sleep(SLEEP_CHECK_TIME);
		}
		if (INSTANCE_STATE_RUNNING.equals(getEc2Instance(ec2Client,
				instance.getInstanceId()).getState().getName())) {
			logger.error("VM failed to stop: "
					+ getEc2Instance(ec2Client, instance.getInstanceId())
							.getStateReason().getMessage());
			throw new RuntimeException(getEc2Instance(ec2Client,
					instance.getInstanceId()).getStateReason().getMessage());
		}
	}

	public static Instance waitForRunningState(AmazonEC2Client ec2Client,
			Instance instance) throws Exception {
		while (INSTANCE_STATE_PENDING.equals(getEc2Instance(ec2Client,
				instance.getInstanceId()).getState().getName())) {
			logger.debug("Waiting on VM to start");
			Thread.sleep(SLEEP_CHECK_TIME);
		}
		if (!INSTANCE_STATE_RUNNING.equals(getEc2Instance(ec2Client,
				instance.getInstanceId()).getState().getName())) {
			logger.error("VM failed to run: "
					+ getEc2Instance(ec2Client, instance.getInstanceId())
							.getStateReason().getMessage());
			throw new RuntimeException(getEc2Instance(ec2Client,
					instance.getInstanceId()).getStateReason().getMessage());
		}
		return getEc2Instance(ec2Client, instance.getInstanceId());
	}

	public static Instance getEc2Instance(AmazonEC2Client client,
			String instanceId) throws Exception {
		DescribeInstancesRequest describeInstancesRequest = new DescribeInstancesRequest();
		Collection<String> instanceIds = new HashSet<String>();
		instanceIds.add(instanceId);
		describeInstancesRequest.setInstanceIds(instanceIds);
		return client.describeInstances(describeInstancesRequest)
				.getReservations().get(0).getInstances().get(0);
	}
}
