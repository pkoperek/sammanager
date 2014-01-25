package pl.edu.agh.samm.testapp.core;

import java.io.IOException;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

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
public class MasterMain extends LoggingClass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3164759548204962804L;
	public static final String EXPRESSIONGENERATOR_OBJECT_NAME = "pl.edu.agh.samm.test:type=ExpressionGenerator";
	public static final String SLAVERESOLVER_OBJECT_NAME = "pl.edu.agh.samm.test:type=SlaveResolver";

	public static final String RMI_REGISTRY_PORT = "rmiregistry.port";
	public static final String RMI_SERVER_CONNECTION_PORT = "rmiserver.port";

	public static final String FILENAME_KEY = "exprfile";

	private static final int MAX_LVL = 25;

	/**
	 * @param args
	 * @throws NullPointerException
	 * @throws MalformedObjectNameException
	 * @throws NotCompliantMBeanException
	 * @throws MBeanRegistrationException
	 * @throws InstanceAlreadyExistsException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws Throwable {
		configureJMX();
		if (args.length > 1) {
			System.out.println("Usage: java ... MasterMain <MaxIntLevel>");
			System.exit(1);
		}

		int maxLvl = MAX_LVL;

		if (args.length > 0) {
			try {
				maxLvl = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				logMessage("ERROR",
						"Input error: expected a number! Passed argument: "
								+ args[0]);
			}
		}

		logMessage("Starting Master Node...");
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

		// object names
		ObjectName expressionGeneratorObjectName = new ObjectName(
				EXPRESSIONGENERATOR_OBJECT_NAME);
		ObjectName slaveResolverObjectName = new ObjectName(
				SLAVERESOLVER_OBJECT_NAME);

		// create objects
		String exprFilePath = System.getProperty(FILENAME_KEY);
		ExpressionGenerator exprGen = new ExpressionGenerator(maxLvl,
				exprFilePath);
		SlaveResolver slaveResolver = new SlaveResolver();
		SlaveDispatcher slaveDispatcher = new SlaveDispatcher(exprGen,
				slaveResolver);
		Thread exprGenThread = new Thread(exprGen);
		Thread slaveDispatcherThread = new Thread(slaveDispatcher);

		// starting threads
		exprGenThread.start();
		slaveDispatcherThread.start();

		// register mbeans
		mBeanServer.registerMBean(exprGen, expressionGeneratorObjectName);
		mBeanServer.registerMBean(slaveResolver, slaveResolverObjectName);

		// waiting for user's sign to end
		logMessage("To STOP press <Enter>");
		System.in.read();
		logMessage("Stopping expression generation...");
		exprGen.stopExecution();
		exprGenThread.join(5000);

		logMessage("Stopping slave dispatcher...");
		slaveDispatcher.stopExecution();
		slaveDispatcherThread.join(5000);

		logMessage("Finishing execution: Back to OS!");
	}

	/**
	 * http://olegz.wordpress.com/2009/03/23/jmx-connectivity-through-the-
	 * firewall/
	 * 
	 * @param agentArgs
	 */
	public static void configureJMX() throws Throwable {
		RMISocketFactory.setSocketFactory(new RMISocketFactory() {
			public Socket createSocket(String host, int port)
					throws IOException {
				Socket socket = new Socket();
				socket.setSoTimeout(10000);
				socket.setSoLinger(false, 0);
				socket.connect(new InetSocketAddress(host, port), 10000);
				return socket;
			}

			public ServerSocket createServerSocket(int port) throws IOException {
				return new ServerSocket(port);
			}
		});

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
