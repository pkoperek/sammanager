package pl.edu.agh.samm.common.metrics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MetricImplTest {

	@Test
	public void testMetricImplStringString() {
		String resUri = "resUri";
		String metricUri = "metricUri";
		Metric impl = new Metric(metricUri, resUri);

		assertEquals(metricUri, impl.getMetricURI());
		assertEquals(resUri, impl.getResourceURI());
		assertEquals(Metric.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());
	}

	@Test
	public void testMetricImplStringStringLong() {
		String resUri = "resUri";
		String metricUri = "metricUri";
		Metric impl = new Metric(metricUri, resUri, 0);

		assertEquals(metricUri, impl.getMetricURI());
		assertEquals(resUri, impl.getResourceURI());
		assertEquals(Metric.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());

		impl = new Metric(metricUri, resUri, -1);

		assertEquals(metricUri, impl.getMetricURI());
		assertEquals(resUri, impl.getResourceURI());
		assertEquals(Metric.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());

		impl = new Metric(metricUri, resUri, 10);

		assertEquals(metricUri, impl.getMetricURI());
		assertEquals(resUri, impl.getResourceURI());
		assertEquals(10, impl.getMetricPollTimeInterval());
	}

	@Test
	public void testSetMetricPollTimeInterval() {
		String resUri = "resUri";
		String metricUri = "metricUri";
		Metric impl = new Metric(metricUri, resUri);

		assertEquals(Metric.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());

		long INTERVAL = 10;

		assertEquals(Metric.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.setMetricPollTimeInterval(INTERVAL));
		assertEquals(INTERVAL, impl.getMetricPollTimeInterval());
		assertEquals(
				INTERVAL,
				impl.setMetricPollTimeInterval(Metric.DEFAULT_METRIC_POLL_TIME_INTERVAL));

		// trying to assign 0
		assertEquals(Metric.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.setMetricPollTimeInterval(0));
		assertEquals(Metric.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());

		// trying to assign -1
		assertEquals(Metric.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.setMetricPollTimeInterval(-1));
		assertEquals(Metric.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());

	}

	@Test
	public void testEqualsObject() {
		String resUri = "resUri";
		String metricUri = "metricUri";
		Metric impl1 = new Metric(metricUri, resUri);
		Metric impl2 = new Metric(metricUri, resUri);
		Metric impl3 = new Metric(metricUri, resUri);
		impl3.setMetricPollTimeInterval(10);

		assertTrue(impl1.equals(impl1));
		assertTrue(impl1.equals(impl2));
		assertTrue(impl1.equals(impl3));
		assertTrue(impl2.equals(impl3));

		Metric impl4 = new Metric(metricUri, resUri + "111");
		Metric impl5 = new Metric(metricUri + "111", resUri);

		assertFalse(impl1.equals(impl4));
		assertFalse(impl1.equals(impl5));
		assertFalse(impl3.equals(impl4));
		assertFalse(impl3.equals(impl5));
		assertFalse(impl4.equals(impl5));
	}

	@Test
	public void testIsPatternMetric() {
		Metric metric = new Metric("sampleMUri", "/sampleUri");
		assertFalse(metric.isPatternMetric());
		metric = new Metric("sampleMUri", "/sampleUri*");
		assertTrue(metric.isPatternMetric());
		metric = new Metric("sampleMUri", "/sampleUri?");
		assertTrue(metric.isPatternMetric());
	}
}
