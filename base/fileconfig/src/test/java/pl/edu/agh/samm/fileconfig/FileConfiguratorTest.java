package pl.edu.agh.samm.fileconfig;

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
import pl.edu.agh.samm.common.core.Rule;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class FileConfiguratorTest {

	private static final String SAMPLE_XML_FILE_CONTENT = "<ruleset>"
			+ "  <rule name=\"testRule_1\">"
			+ "    <resourceTypeUri>resourceTypeURI1</resourceTypeUri>"
			+ "    <resourceUri>resourceURI1</resourceUri>"
			+ "    <metricUri>metricURI1</metricUri>"
			+ "    <condition>condition1</condition>" + "    <actionToExecute>"
			+ "      <actionURI>actionURI1</actionURI>"
			+ "      <parameterValues>" + "        <entry>"
			+ "          <string>sampleParam31</string>"
			+ "          <string>sampleValue31</string>" + "        </entry>"
			+ "        <entry>" + "          <string>sampleParam21</string>"
			+ "          <string>sampleValue21</string>" + "        </entry>"
			+ "        <entry>" + "          <string>sampleParam11</string>"
			+ "          <string>sampleValue11</string>" + "        </entry>"
			+ "      </parameterValues>" + "    </actionToExecute>"
			+ "  </rule>" + "  <rule name=\"testRule_2\">"
			+ "    <resourceTypeUri>resourceTypeURI2</resourceTypeUri>"
			+ "    <resourceUri>resourceURI2</resourceUri>"
			+ "    <metricUri>metricURI2</metricUri>"
			+ "    <condition>condition2</condition>" + "    <actionToExecute>"
			+ "      <actionURI>actionURI2</actionURI>"
			+ "      <parameterValues>" + "        <entry>"
			+ "          <string>sampleParam22</string>"
			+ "          <string>sampleValue22</string>" + "        </entry>"
			+ "        <entry>" + "          <string>sampleParam32</string>"
			+ "          <string>sampleValue32</string>" + "        </entry>"
			+ "        <entry>" + "          <string>sampleParam12</string>"
			+ "          <string>sampleValue12</string>" + "        </entry>"
			+ "      </parameterValues>" + "    </actionToExecute>"
			+ "  </rule>" + "  <rule name=\"testRule_3\">"
			+ "    <resourceTypeUri>resourceTypeURI3</resourceTypeUri>"
			+ "    <resourceUri>resourceURI3</resourceUri>"
			+ "    <metricUri>metricURI3</metricUri>"
			+ "    <condition>condition3</condition>" + "    <actionToExecute>"
			+ "      <actionURI>actionURI3</actionURI>"
			+ "      <parameterValues>" + "        <entry>"
			+ "          <string>sampleParam33</string>"
			+ "          <string>sampleValue33</string>" + "        </entry>"
			+ "        <entry>" + "          <string>sampleParam23</string>"
			+ "          <string>sampleValue23</string>" + "        </entry>"
			+ "        <entry>" + "          <string>sampleParam13</string>"
			+ "          <string>sampleValue13</string>" + "        </entry>"
			+ "      </parameterValues>" + "    </actionToExecute>"
			+ "  </rule>" + "</ruleset>";

	@Test
	public void testInit() throws IOException {
		// create temp file
		File tempFile = File.createTempFile("111", "222");

		// append content
		Writer writer = new FileWriter(tempFile);
		writer.append(SAMPLE_XML_FILE_CONTENT);
		writer.close();

		// set property
		System.setProperty(FileConfigurator.PROPERTIES_FILENAME_KEY,
				tempFile.getAbsolutePath());
		ICoreManagement mockCoreManagement = EasyMock
				.createMock(ICoreManagement.class);

		Capture<Rule> rule1c = new Capture<Rule>();
		Capture<Rule> rule2c = new Capture<Rule>();
		Capture<Rule> rule3c = new Capture<Rule>();
		mockCoreManagement.addRule(EasyMock.capture(rule1c));
		mockCoreManagement.addRule(EasyMock.capture(rule2c));
		mockCoreManagement.addRule(EasyMock.capture(rule3c));

		EasyMock.replay(mockCoreManagement);
		FileConfigurator fileConfigurator = new FileConfigurator();
		fileConfigurator.setCoreManagement(mockCoreManagement);
		fileConfigurator.init();
		
		assertTrue(rule1c.hasCaptured());
		assertTrue(rule2c.hasCaptured());
		assertTrue(rule3c.hasCaptured());
		
		Rule rule1 = rule1c.getValue();
		Rule rule2 = rule2c.getValue();
		Rule rule3 = rule3c.getValue();
		
		assertEquals("testRule_1",rule1.getName());
		assertEquals("testRule_2",rule2.getName());
		assertEquals("testRule_3",rule3.getName());
		
		assertNotNull(rule1.getActionToExecute());
		assertNotNull(rule2.getActionToExecute());
		assertNotNull(rule3.getActionToExecute());
		
		assertNotNull(rule1.getActionToExecute().getParameterValues());
		assertNotNull(rule2.getActionToExecute().getParameterValues());
		assertNotNull(rule3.getActionToExecute().getParameterValues());
		
		EasyMock.verify(mockCoreManagement);
	}

	@Test
	public void testConfigureXStream() {
		XStream xstream = new XStream(new DomDriver());
		FileConfigurator.configureXStream(xstream);

		// sample rule 1
		RuleSet rs = new RuleSet();
		rs.addRule(generateSampleRule(1));
		rs.addRule(generateSampleRule(2));
		rs.addRule(generateSampleRule(3));

		System.out.println(xstream.toXML(rs));
	}

	private Rule generateSampleRule(int i) {
		Rule rule = new Rule("testRule_" + i);
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
