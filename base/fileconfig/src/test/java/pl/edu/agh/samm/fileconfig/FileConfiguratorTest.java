package pl.edu.agh.samm.fileconfig;

import static org.easymock.EasyMock.anyObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;

import pl.edu.agh.samm.common.action.Action;
import pl.edu.agh.samm.common.core.ICoreManagement;
import pl.edu.agh.samm.common.core.Resource;
import pl.edu.agh.samm.common.core.ResourceAlreadyRegisteredException;
import pl.edu.agh.samm.common.core.Rule;
import pl.edu.agh.samm.common.metrics.IMetric;
import pl.edu.agh.samm.common.metrics.Metric;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class FileConfiguratorTest {

	@Test
	public void testMetricDeserializationDefaultPollTimeVal() throws Exception {
		String content = "<configuration>  <metricSet>    <metric resourceURI=\"sampleResourceUri1\" metricURI=\"sampleMetricUri1\"/>  </metricSet></configuration>";
		File tempFile = File.createTempFile("111", "222");

		Writer writer = new FileWriter(tempFile);
		writer.append(content);
		writer.close();

		System.setProperty(FileConfigurator.PROPERTIES_FILENAME_KEY,
				tempFile.getAbsolutePath());
		ICoreManagement mockCoreManagement = EasyMock
				.createMock(ICoreManagement.class);

		Capture<IMetric> captureMetric = new Capture<IMetric>();
		mockCoreManagement.startMetric(EasyMock.capture(captureMetric));

		EasyMock.replay(mockCoreManagement);
		FileConfigurator fileConfigurator = new FileConfigurator();
		fileConfigurator.setCoreManagement(mockCoreManagement);
		fileConfigurator.init();
		IMetric metric = captureMetric.getValue();
		assertNotNull(metric);
		assertEquals(20000, metric.getMetricPollTimeInterval());
		EasyMock.verify(mockCoreManagement);
	}

	@Test
	public void testEmptyConfiguration() throws Exception {
		String content = generateEmptyConfigurationXML();
		File tempFile = File.createTempFile("111", "222");

		Writer writer = new FileWriter(tempFile);
		writer.append(content);
		writer.close();

		System.setProperty(FileConfigurator.PROPERTIES_FILENAME_KEY,
				tempFile.getAbsolutePath());
		ICoreManagement mockCoreManagement = EasyMock
				.createMock(ICoreManagement.class);

		EasyMock.replay(mockCoreManagement);
		FileConfigurator fileConfigurator = new FileConfigurator();
		fileConfigurator.setCoreManagement(mockCoreManagement);
		fileConfigurator.init();
		EasyMock.verify(mockCoreManagement);
	}

	@Test
	public void testSampleDataSetInit() throws IOException,
			ResourceAlreadyRegisteredException {
		// create temp file
		File tempFile = File.createTempFile("111", "222");

		String sampleContent = generateSampleContent();

		// append content
		Writer writer = new FileWriter(tempFile);
		writer.append(sampleContent);
		writer.close();

		// set property
		System.setProperty(FileConfigurator.PROPERTIES_FILENAME_KEY,
				tempFile.getAbsolutePath());
		ICoreManagement mockCoreManagement = EasyMock
				.createMock(ICoreManagement.class);

		// captures...
		Capture<Rule> rule1c = new Capture<Rule>();
		Capture<Rule> rule2c = new Capture<Rule>();
		Capture<Rule> rule3c = new Capture<Rule>();

		Capture<Resource> res1c = new Capture<Resource>();
		Capture<Resource> res2c = new Capture<Resource>();
		Capture<Resource> res3c = new Capture<Resource>();

		mockCoreManagement.registerResource(EasyMock.capture(res1c));
		mockCoreManagement.registerResource(EasyMock.capture(res2c));
		mockCoreManagement.registerResource(EasyMock.capture(res3c));

		mockCoreManagement.addRule(EasyMock.capture(rule1c));
		mockCoreManagement.addRule(EasyMock.capture(rule2c));
		mockCoreManagement.addRule(EasyMock.capture(rule3c));

		// metrics stuff
		mockCoreManagement.startMetric(anyObject(IMetric.class));
		mockCoreManagement.startMetric(anyObject(IMetric.class));
		mockCoreManagement.startMetric(anyObject(IMetric.class));

		// start the test
		EasyMock.replay(mockCoreManagement);

		// initialization && invocation of init() method
		FileConfigurator fileConfigurator = new FileConfigurator();
		fileConfigurator.setCoreManagement(mockCoreManagement);
		fileConfigurator.init();

		assertTrue(rule1c.hasCaptured());
		assertTrue(rule2c.hasCaptured());
		assertTrue(rule3c.hasCaptured());

		assertTrue(res1c.hasCaptured());
		assertTrue(res2c.hasCaptured());
		assertTrue(res3c.hasCaptured());

		// get captured objects
		Rule rule1 = rule1c.getValue();
		Rule rule2 = rule2c.getValue();
		Rule rule3 = rule3c.getValue();

		Resource res1 = res1c.getValue();
		Resource res2 = res2c.getValue();
		Resource res3 = res3c.getValue();

		// assertopms
		assertEquals("testRule_1", rule1.getName());
		assertEquals("testRule_2", rule2.getName());
		assertEquals("testRule_3", rule3.getName());

		assertEquals("sampleUri1", res1.getUri());
		assertEquals("sampleUri2", res2.getUri());
		assertEquals("sampleUri3", res3.getUri());

		assertNotNull(rule1.getActionToExecute());
		assertNotNull(rule2.getActionToExecute());
		assertNotNull(rule3.getActionToExecute());

		assertNotNull(res1.getProperties());
		assertEquals(1, res1.getProperties().size());
		assertNotNull(res2.getProperties());
		assertEquals(1, res2.getProperties().size());
		assertNotNull(res3.getProperties());
		assertEquals(1, res3.getProperties().size());

		assertNotNull(rule1.getActionToExecute().getParameterValues());
		assertNotNull(rule2.getActionToExecute().getParameterValues());
		assertNotNull(rule3.getActionToExecute().getParameterValues());

		EasyMock.verify(mockCoreManagement);
	}

	@Test
	public void testConfigureXStream() {
		System.out.println(generateSampleContent());
	}

	private String generateEmptyConfigurationXML() {
		XStream xstream = new XStream(new DomDriver());
		FileConfigurator.configureXStream(xstream);

		Configuration configuration = new Configuration();
		return xstream.toXML(configuration);
	}

	private String generateMetricConfigurationXML() {
		XStream xstream = new XStream(new DomDriver());
		FileConfigurator.configureXStream(xstream);
		Configuration configuration = new Configuration();

		// sample metric set
		ConfigurationMetricSet metricSet = new ConfigurationMetricSet();
		metricSet.addMetric(generateSampleMetricNoPollTime(1));

		configuration.setMetricSet(metricSet);

		return xstream.toXML(configuration);
	}

	private String generateSampleContent() {
		XStream xstream = new XStream(new DomDriver());
		FileConfigurator.configureXStream(xstream);

		Configuration configuration = new Configuration();

		// sampel res set
		ConfigurationResourceSet resSet = new ConfigurationResourceSet();
		resSet.addResource(generateSampleResource(1));
		resSet.addResource(generateSampleResource(2));
		resSet.addResource(generateSampleResource(3));

		// sample rule set
		RuleSet rs = new RuleSet();
		rs.addRule(generateSampleRule(1));
		rs.addRule(generateSampleRule(2));
		rs.addRule(generateSampleRule(3));

		// sample metric set
		ConfigurationMetricSet metricSet = new ConfigurationMetricSet();
		metricSet.addMetric(generateSampleMetric(1));
		metricSet.addMetric(generateSampleMetric(2));
		metricSet.addMetric(generateSampleMetric(3));

		configuration.setMetricSet(metricSet);
		configuration.setRuleSet(rs);
		configuration.setResourceSet(resSet);

		return xstream.toXML(configuration);
	}

	private ConfigurationResource generateSampleResource(final int i) {
		ConfigurationResource r = new ConfigurationResource();
		r.setType("sampleType" + i);
		r.setUri("sampleUri" + i);
		r.addProperty(generateSampleProperty(i));
		return r;
	}

	private IMetric generateSampleMetricNoPollTime(int i) {
		IMetric metric = new Metric("sampleMetricUri" + i, "sampleResourceUri"
				+ i);
		return metric;
	}

	private IMetric generateSampleMetric(int i) {
		IMetric metric = new Metric("sampleMetricUri" + i, "sampleResourceUri"
				+ i, i);
		return metric;
	}

	private ConfigurationResourceProperty generateSampleProperty(int i) {
		ConfigurationResourceProperty p = new ConfigurationResourceProperty();
		p.setKey("sampleKey" + i);
		p.setValue("sampleValue" + i);
		return p;
	}

	private Rule generateSampleRule(int i) {
		Rule rule = new Rule("testRule_" + i);
		rule.setCustomStatement("sampleCustomStatement_" + i);
		rule.setCondition("condition" + i);
		rule.setMetricUri("metricURI" + i);
		rule.setResourceTypeUri("resourceTypeURI" + i);
		rule.setResourceUri("resourceURI" + i);
		Action action = new Action();
		action.setActionURI("actionURI" + i);
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sampleParam1" + i, "sampleValue1" + i);
		parameters.put("sampleParam2" + i, "sampleValue2" + i);
		parameters.put("sampleParam3" + i, "sampleValue3" + i);
		action.setParameterValues(parameters);
		rule.setActionToExecute(action);
		return rule;
	}

}
