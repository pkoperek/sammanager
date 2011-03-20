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
		MetricImpl impl = new MetricImpl(metricUri, resUri);

		assertEquals(metricUri, impl.getMetricURI());
		assertEquals(resUri, impl.getResourceURI());
		assertEquals(MetricImpl.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());
	}

	@Test
	public void testMetricImplStringStringLong() {
		String resUri = "resUri";
		String metricUri = "metricUri";
		MetricImpl impl = new MetricImpl(metricUri, resUri, 0);

		assertEquals(metricUri, impl.getMetricURI());
		assertEquals(resUri, impl.getResourceURI());
		assertEquals(MetricImpl.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());

		impl = new MetricImpl(metricUri, resUri, -1);

		assertEquals(metricUri, impl.getMetricURI());
		assertEquals(resUri, impl.getResourceURI());
		assertEquals(MetricImpl.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());

		impl = new MetricImpl(metricUri, resUri, 10);

		assertEquals(metricUri, impl.getMetricURI());
		assertEquals(resUri, impl.getResourceURI());
		assertEquals(10, impl.getMetricPollTimeInterval());
	}

	@Test
	public void testSetMetricPollTimeInterval() {
		String resUri = "resUri";
		String metricUri = "metricUri";
		MetricImpl impl = new MetricImpl(metricUri, resUri);

		assertEquals(MetricImpl.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());

		long INTERVAL = 10;

		assertEquals(MetricImpl.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.setMetricPollTimeInterval(INTERVAL));
		assertEquals(INTERVAL, impl.getMetricPollTimeInterval());
		assertEquals(
				INTERVAL,
				impl.setMetricPollTimeInterval(MetricImpl.DEFAULT_METRIC_POLL_TIME_INTERVAL));

		// trying to assign 0
		assertEquals(MetricImpl.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.setMetricPollTimeInterval(0));
		assertEquals(MetricImpl.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());

		// trying to assign -1
		assertEquals(MetricImpl.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.setMetricPollTimeInterval(-1));
		assertEquals(MetricImpl.DEFAULT_METRIC_POLL_TIME_INTERVAL,
				impl.getMetricPollTimeInterval());

	}

	@Test
	public void testEqualsObject() {
		String resUri = "resUri";
		String metricUri = "metricUri";
		MetricImpl impl1 = new MetricImpl(metricUri, resUri);
		MetricImpl impl2 = new MetricImpl(metricUri, resUri);
		MetricImpl impl3 = new MetricImpl(metricUri, resUri);
		impl3.setMetricPollTimeInterval(10);

		assertTrue(impl1.equals(impl1));
		assertTrue(impl1.equals(impl2));
		assertTrue(impl1.equals(impl3));
		assertTrue(impl2.equals(impl3));

		MetricImpl impl4 = new MetricImpl(metricUri, resUri + "111");
		MetricImpl impl5 = new MetricImpl(metricUri + "111", resUri);

		assertFalse(impl1.equals(impl4));
		assertFalse(impl1.equals(impl5));
		assertFalse(impl3.equals(impl4));
		assertFalse(impl3.equals(impl5));
		assertFalse(impl4.equals(impl5));
	}

}
