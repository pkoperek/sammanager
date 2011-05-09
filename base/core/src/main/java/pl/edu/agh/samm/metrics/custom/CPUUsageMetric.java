package pl.edu.agh.samm.metrics.custom;

import java.util.Map;

import pl.edu.agh.samm.common.metrics.ICustomMetric;

/**
 * Based on:
 * http://knight76.blogspot.com/2009/05/how-to-get-java-cpu-usage-jvm-instance
 * .html
 * 
 * @author koperek
 * 
 */
public class CPUUsageMetric implements ICustomMetric {

	public static final String UPTIME_CAPABILITY = "http://www.icsr.agh.edu.pl/samm_1.owl#UptimeTypeCapability";
	public static final String JVM_TOTAL_CPU_TIME_USAGE = "http://www.icsr.agh.edu.pl/samm_1.owl#JVMTotalCPUTimeTypeCapability";

	private long prevUptime = -1;
	private long prevProcessCpuTime = -1;

	@Override
	public Number computeValue(Map<String, Number> capabilitiesValues) {
		long uptime = capabilitiesValues.get(UPTIME_CAPABILITY).longValue();
		long processCpuTime = capabilitiesValues.get(JVM_TOTAL_CPU_TIME_USAGE)
				.longValue();

		double retVal = 0;

		if (prevUptime > -1 && prevProcessCpuTime > -1) {
			double uptimeDiff = (uptime - prevUptime) * 10000;
			double processCpuTimeDiff = (processCpuTime - prevProcessCpuTime);
			// if we didn't spent any time on computations - there was a 0 usage
			retVal = (uptimeDiff == 0.0) ? 0.0 : processCpuTimeDiff
					/ uptimeDiff;
		}

		prevUptime = uptime;
		prevProcessCpuTime = processCpuTime;

		return retVal;
	}
}
