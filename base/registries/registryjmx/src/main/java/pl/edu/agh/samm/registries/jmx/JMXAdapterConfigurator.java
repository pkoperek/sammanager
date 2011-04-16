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

package pl.edu.agh.samm.registries.jmx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Holds configuration for JMX Adapter
 * 
 * @author Pawel Koperek <pkoperek@gmail.com>
 * @author Mateusz Kupisz <mkupisz@gmail.com>
 * 
 */
public class JMXAdapterConfigurator {

	public static final String ENV_PROPERTIES_FILE = "jmxAdapter.propertiesFile";

	private static final Properties DEFAULT_PROPERTIES = new Properties();
	private Properties properties;

	public JMXAdapterConfigurator() {
		// master application
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"masterapplication.instance.name",
				"pl.edu.agh.samm.test:type=ExpressionGenerator|Name");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"masterapplication.capability.MasterQueueLengthCapability",
				"pl.edu.agh.samm.test:type=ExpressionGenerator|QueueLength");

		// slave application

		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"slaveapplication.instance.prefix", "SlaveNodeApp_");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"slaveapplication.instance.name",
				"pl.edu.agh.samm.test:type=Slave|Id");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"slaveapplication.capability.SlaveQueueLengthCapability",
				"pl.edu.agh.samm.test:type=Slave|QueueLength");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"slaveapplication.capability.SlaveProcessedCountCapability",
				"pl.edu.agh.samm.test:type=Slave|ProcessedCount");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"slaveapplication.capability.SlaveSumProcessingTimeCapability",
				"pl.edu.agh.samm.test:type=Slave|SumProcessingTime");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"slaveapplication.capability.SlaveAvgProcessingTimeCapability",
				"pl.edu.agh.samm.test:type=Slave|AvgProcessTime");

		// general
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put("jvm.instance.prefix",
		"JVM_");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put("jvm.instance.name",
				"java.lang:type=Runtime|Name");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"operatingsystem.instance.name",
				"java.lang:type=OperatingSystem|Name");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put("cpu.instance.name",
				"java.lang:type=OperatingSystem|Arch");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put("cpu.instance.prefix",
				"cpu_");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"harddisk.instance.prefix", "hd1");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put("memory.instance.prefix",
				"currentMemory");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put("thread.instance.name",
				"java.lang:type=Threading|AllThreadIds");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put("thread.instance.prefix",
				"Thread_");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put("tomcat.instance.name",
				"Catalina:type=Engine|name");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put("tomcat.instance.prefix",
				"");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES
				.put("tomcat.capability.RequestCountTypeCapability",
						"Catalina:type=GlobalRequestProcessor,name=http-8080|requestCount");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES
				.put("tomcat.capability.ProcessingTimeTypeCapability",
						"Catalina:type=GlobalRequestProcessor,name=http-8080|processingTime");

		// jvm
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.UptimeTypeCapability",
				"java.lang:type=Runtime|Uptime");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.TotalUnloadedClassesTypeCapability",
				"java.lang:type=ClassLoading|UnloadedClassCount");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.TotalLoadedClassesTypeCapability",
				"java.lang:type=ClassLoading|TotalLoadedClassCount");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.TotalStartedThreadsCountTypeCapability",
				"java.lang:type=Threading|TotalStartedThreadCount");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.LiveThreadsCountTypeCapability",
				"java.lang:type=Threading|ThreadCount");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.CurrentLoadedClassesTypeCapability",
				"java.lang:type=ClassLoading|LoadedClassCount");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.HeapUsageTypeCapability",
				"java.lang:type=Memory|HeapMemoryUsage|used");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.NonHeapUsageTypeCapability",
				"java.lang:type=Memory|NonHeapMemoryUsage|used");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.PermMemoryUsageTypeCapability",
				"java.lang:type=MemoryPool,name=Perm Gen|Usage|used");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES
				.put("jvm.capability.MinorGCTotalCountTypeCapability",
						"java.lang:type=GarbageCollector,name=PS Scavenge|CollectionCount");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES
				.put("jvm.capability.MinorGCTotalTimeTypeCapability",
						"java.lang:type=GarbageCollector,name=PS Scavenge|CollectionTime");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES
				.put("jvm.capability.MajorGCTotalCountTypeCapability",
						"java.lang:type=GarbageCollector,name=PS MarkSweep|CollectionCount");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES
				.put("jvm.capability.MajorGCTotalTimeTypeCapability",
						"java.lang:type=GarbageCollector,name=PS MarkSweep|CollectionTime");

		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.TotalCompilationTimeTypeCapability",
				"java.lang:type=Compilation|TotalCompilationTime");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.OpenFileDescriptorCountTypeCapability",
				"java.lang:type=OperatingSystem|OpenFileDescriptorCount");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"jvm.capability.JVMTotalCPUTimeTypeCapability",
				"java.lang:type=OperatingSystem|ProcessCpuTime");

		// thread
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"thread.capability.ThreadCPUTimeTypeCapability",
				"java.lang:type=Threading|getThreadCpuTime(@instance),long");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"thread.capability.ThreadUserTimeTypeCapability",
				"java.lang:type=Threading|getThreadUserTime(@instance),long");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES
				.put("thread.capability.ThreadBlockedCountTypeCapability",
						"java.lang:type=Threading|getThreadInfo(@instance),long|blockedCount");

		// operating system
		JMXAdapterConfigurator.DEFAULT_PROPERTIES
				.put("operatingsystem.capability.AvailableVirtualMemoryTypeCapability",
						"java.lang:type=OperatingSystem|FreeSwapSpaceSize");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"operatingsystem.capability.UptimeTypeCapability",
				"linuxMonitoringExtension:type=SystemInformation|Uptime");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"operatingsystem.capability.Load15minTypeCapability",
				"linuxMonitoringExtension:type=SystemInformation|L15m");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"operatingsystem.capability.Load5minTypeCapability",
				"linuxMonitoringExtension:type=SystemInformation|L5m");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES.put(
				"operatingsystem.capability.Load1minTypeCapability",
				"linuxMonitoringExtension:type=SystemInformation|L1m");
		JMXAdapterConfigurator.DEFAULT_PROPERTIES
				.put("operatingsystem.capability.AvailablePhysicalMemoryTypeCapability",
						"java.lang:type=OperatingSystem|FreePhysicalMemorySize");
	}

	public void init() throws FileNotFoundException, IOException {
		String propertiesFileName = System.getProperty(ENV_PROPERTIES_FILE);
		if (propertiesFileName != null) {
			loadConfiguration(propertiesFileName);
		} else {
			properties = DEFAULT_PROPERTIES;
		}
	}

	/**
	 * Loads configuration file.
	 */
	public void loadConfiguration(final File file)
			throws FileNotFoundException, IOException {
		properties = new Properties();
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			properties.load(inputStream);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

	}

	public void loadConfiguration(String fileName)
			throws FileNotFoundException, IOException {
		loadConfiguration(new File(fileName));
	}

	public String getProperty(String key) {
		return properties.getProperty(key);
	}

}
