package pl.edu.agh.samm.metrics.custom;

import java.util.Map;

import pl.edu.agh.samm.api.metrics.ICustomMetric;

/**
 * 
 * @author koperek
 * 
 */
public class AvgWaitTimeMetric implements ICustomMetric {

	public static final String SERVED_COUNT_CAPABILITY = "http://www.icsr.agh.edu.pl/samm_1.owl#MasterServedCountCapability";
	public static final String SUM_WAIT_TIME_CAPABILITY = "http://www.icsr.agh.edu.pl/samm_1.owl#MasterSumWaitTimeCapability";

	private long prevServedCount = 0;
	private long prevSumWaitTime = 0;

	@Override
	public Number computeValue(Map<String, Number> capabilitiesValues) {
		long servedCount = capabilitiesValues.get(SERVED_COUNT_CAPABILITY)
				.longValue();
		long sumWaitTime = capabilitiesValues.get(SUM_WAIT_TIME_CAPABILITY)
				.longValue();

		double retVal = 0;

		double servedCountDiff = servedCount - prevServedCount;
		double sumWaitTimeDiff = sumWaitTime - prevSumWaitTime;

		if (sumWaitTimeDiff == 0 || servedCountDiff == 0) {
			return 0;
		}
		
		retVal = sumWaitTimeDiff / servedCountDiff;

		prevSumWaitTime = sumWaitTime;
		prevServedCount = servedCount;

		return retVal;
	}
}
