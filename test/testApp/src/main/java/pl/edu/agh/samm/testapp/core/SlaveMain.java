package pl.edu.agh.samm.testapp.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.JMX;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

/**
 * @author koperek
 * 
 */
public class SlaveMain extends LoggingClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2211493884302975210L;
	public static final String RMI_REGISTRY_PORT = "rmiregistry.port";
	public static final String RMI_SERVER_CONNECTION_PORT = "rmiserver.port";
	public static final String SLAVE_OBJECT_NAME = "pl.edu.agh.samm.test:type=Slave";

	private static void disableSSLCertVerification() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}

	}

	/**
	 * @param args
	 * @throws NotCompliantMBeanException
	 * @throws MBeanRegistrationException
	 * @throws InstanceAlreadyExistsException
	 * @throws NullPointerException
	 * @throws MalformedObjectNameException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InstanceNotFoundException
	 */
	public static void main(String[] args) throws Throwable {
		configureJMX();
		disableSSLCertVerification();
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("koperek", ""
						.toCharArray());
			}
		});

		logMessage("Starting Slave Node...");
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

		// object names
		ObjectName slaveObjectName = new ObjectName(SLAVE_OBJECT_NAME);

		// create objects
		Slave slave = new Slave();

		Thread slaveThread = new Thread(slave);

		Properties properties = new Properties();
		properties.load(new FileInputStream("slave.properties"));

		String masterURI = properties.getProperty("masteruri");
		String rendezvousURL = properties.getProperty("rendezvous");

		if (rendezvousURL != null && masterURI == null) {
			logMessage("Rendezvous URL: " + rendezvousURL);

			InputStream is = new URL(rendezvousURL).openStream();
			Properties rendezvousProperties = new Properties();
			rendezvousProperties.load(is);

			masterURI = rendezvousProperties.getProperty("MASTER");
		}

		if (masterURI == null) {
			System.out.println("Can't obtain Master's address!");
			System.exit(1);
		}

		logMessage("Master URL: " + masterURI);

		JMXServiceURL url = new JMXServiceURL(masterURI);
		JMXConnector jmxc = JMXConnectorFactory.connect(url, null);

		MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();

		ObjectName slaveSolver = new ObjectName(
				MasterMain.SLAVERESOLVER_OBJECT_NAME);

		logMessage("Registering...");
		ISlaveResolver slaveResolverProxy = JMX.newMBeanProxy(mbsc,
				slaveSolver, ISlaveResolver.class);

		RemoteSlaveProxy remoteSlaveProxy = new RemoteSlaveProxy(slave);
		IRemoteSlave proxySlave = (IRemoteSlave) UnicastRemoteObject
				.exportObject(remoteSlaveProxy, 0);

		String id = slaveResolverProxy.registerSlave(proxySlave);
		slave.setId(id);

		// starting thread
		slaveThread.start();

		// register mbeans
		mBeanServer.registerMBean(slave, slaveObjectName);

		logMessage("Waiting for slave to stop");
		slaveThread.join();

		logMessage("Finishing execution: Back to OS!");
	}

	/**
	 * http://olegz.wordpress.com/2009/03/23/jmx-connectivity-through-the-
	 * firewall/
	 * 
	 * @param agentArgs
	 */
	public static void configureJMX() throws Throwable {
		final int rmiRegistryPort = Integer.parseInt(System.getProperty(
				RMI_REGISTRY_PORT, "60000"));
		final int rmiServerPort = Integer.parseInt(System.getProperty(
				RMI_SERVER_CONNECTION_PORT, (rmiRegistryPort + 1) + ""));
		final String hostname = InetAddress.getLocalHost().getHostName();
		final String publicHostName = System.getProperty(
				"java.rmi.server.hostname", hostname);

		System.out.println(RMI_REGISTRY_PORT + ":" + rmiRegistryPort);
		System.out.println(RMI_SERVER_CONNECTION_PORT + ":" + rmiServerPort);

		// -Dcom.sun.management.jmxremote.port=60000
		LocateRegistry.createRegistry(rmiRegistryPort);

		System.out.println("Getting the platform's MBean Server");
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

		Map<String, Object> env = new HashMap<String, Object>();
		// Provide SSL-based RMI socket factories.
		//
		// The protocol and cipher suites to be enabled will be the ones
		// defined by the default JSSE implementation and only server
		// authentication will be required.
		//
		// SslRMIClientSocketFactory csf = new SslRMIClientSocketFactory();
		// SslRMIServerSocketFactory ssf = new SslRMIServerSocketFactory();
		// env.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, csf);
		// env.put(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE, ssf);

		// Provide the password file used by the connector server to
		// perform user authentication. The password file is a properties
		// based text file specifying username/password pairs.
		JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://" + hostname
				+ ":" + rmiServerPort + "/jndi/rmi://" + hostname + ":"
				+ rmiRegistryPort + "/jmxrmi");

		// Used only to dosplay what the public address should be
		JMXServiceURL publicUrl = new JMXServiceURL("service:jmx:rmi://"
				+ publicHostName + ":" + rmiServerPort + "/jndi/rmi://"
				+ publicHostName + ":" + rmiRegistryPort + "/jmxrmi");

		System.out.println("Local Connection URL: " + url);
		System.out.println("Public Connection URL: " + publicUrl);
		System.out.println("Creating RMI connector server");
		final JMXConnectorServer cs = JMXConnectorServerFactory
				.newJMXConnectorServer(url, env, mbs);
		System.out.println("Created RMI connector server");

		cs.start();

		System.out.println("Started RMI connector server");
	}
}
