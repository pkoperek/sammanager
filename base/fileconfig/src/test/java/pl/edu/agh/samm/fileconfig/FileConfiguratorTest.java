package pl.edu.agh.samm.fileconfig;

import com.thoughtworks.xstream.XStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.edu.agh.samm.api.action.Action;
import pl.edu.agh.samm.api.core.ICoreManagement;
import pl.edu.agh.samm.api.core.Resource;
import pl.edu.agh.samm.api.core.ResourceAlreadyRegisteredException;
import pl.edu.agh.samm.api.core.Rule;
import pl.edu.agh.samm.api.metrics.IMetric;
import pl.edu.agh.samm.api.metrics.Metric;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FileConfiguratorTest {

    @Mock
    private ICoreManagement coreManagement;

    private XStreamFactory xStreamFactory = new XStreamFactory();

    @Test
    public void testMetricDeserializationDefaultPollTimeVal() throws Exception {

        // Given
        File configurationFile = createTempFileWithContent("<configuration>  <metricSet>    <metric resourceURI=\"sampleResourceUri1\" metricURI=\"sampleMetricUri1\"/>  </metricSet></configuration>");

        System.setProperty(FileConfigurator.PROPERTIES_FILENAME_KEY, configurationFile.getAbsolutePath());

        // When
        FileConfigurator fileConfigurator = new FileConfigurator();
        fileConfigurator.setCoreManagement(coreManagement);
        fileConfigurator.setxStreamFactory(xStreamFactory);
        fileConfigurator.init();

        // Then
        ArgumentCaptor<IMetric> metric = ArgumentCaptor.forClass(IMetric.class);
        verify(coreManagement).startMetric(metric.capture());

        IMetric values = metric.getValue();
        assertNotNull(values);
        assertEquals(20000, values.getMetricPollTimeInterval());
    }

    @Test
    public void testEmptyConfiguration() throws Exception {

        // Given
        File configurationFile = createTempFileWithContent(generateEmptyConfigurationXML());

        System.setProperty(FileConfigurator.PROPERTIES_FILENAME_KEY, configurationFile.getAbsolutePath());

        // When
        FileConfigurator fileConfigurator = new FileConfigurator();
        fileConfigurator.setCoreManagement(coreManagement);
        fileConfigurator.setxStreamFactory(xStreamFactory);
        fileConfigurator.init();

        // Then
        verifyNoMoreInteractions(coreManagement);
    }

    @Test
    public void testSampleDataSetInit() throws IOException, ResourceAlreadyRegisteredException {
        // Given
        File configurationFile = createTempFileWithContent(generateSampleContent());

        // set property
        System.setProperty(FileConfigurator.PROPERTIES_FILENAME_KEY, configurationFile.getAbsolutePath());

        // When
        FileConfigurator fileConfigurator = new FileConfigurator();
        fileConfigurator.setCoreManagement(coreManagement);
        fileConfigurator.setxStreamFactory(xStreamFactory);
        fileConfigurator.init();

        // Then

        // captures...
        ArgumentCaptor<Rule> rules = ArgumentCaptor.forClass(Rule.class);
        ArgumentCaptor<Resource> resources = ArgumentCaptor.forClass(Resource.class);

        verify(coreManagement, times(3)).registerResource(resources.capture());
        verify(coreManagement, times(3)).addRule(rules.capture());

        // metrics stuff
        verify(coreManagement, times(3)).startMetric(any(IMetric.class));

        assertEquals("testRule_1", rules.getAllValues().get(0).getName());
        assertEquals("testRule_2", rules.getAllValues().get(1).getName());
        assertEquals("testRule_3", rules.getAllValues().get(2).getName());

        assertEquals("sampleUri1", resources.getAllValues().get(0).getUri());
        assertEquals("sampleUri2", resources.getAllValues().get(1).getUri());
        assertEquals("sampleUri3", resources.getAllValues().get(2).getUri());

        assertNotNull(rules.getAllValues().get(0).getActionToExecute());
        assertNotNull(rules.getAllValues().get(0).getActionToExecute());
        assertNotNull(rules.getAllValues().get(2).getActionToExecute());

        assertNotNull(resources.getAllValues().get(0).getProperties());
        assertEquals(1, resources.getAllValues().get(0).getProperties().size());
        assertNotNull(resources.getAllValues().get(1).getProperties());
        assertEquals(1, resources.getAllValues().get(1).getProperties().size());
        assertNotNull(resources.getAllValues().get(2).getProperties());
        assertEquals(1, resources.getAllValues().get(2).getProperties().size());

        assertNotNull(rules.getAllValues().get(0).getActionToExecute().getParameterValues());
        assertNotNull(rules.getAllValues().get(1).getActionToExecute().getParameterValues());
        assertNotNull(rules.getAllValues().get(2).getActionToExecute().getParameterValues());
    }

    @Test
    public void testConfigureXStreamGrace() {
        String contents = generateConfigurationXMLGrace();
        System.out.println(contents);
        assertTrue(contents.contains("gracePeriod"));
    }

    private String generateConfigurationXMLGrace() {
        XStream xstream = xStreamFactory.createXStream();

        Configuration configuration = new Configuration();
        configuration.setGracePeriod(1);
        return xstream.toXML(configuration);
    }

    @Test
    public void testConfigureXStream() {
        System.out.println(generateSampleContent());
    }

    private String generateEmptyConfigurationXML() {
        XStream xstream = xStreamFactory.createXStream();

        Configuration configuration = new Configuration();
        return xstream.toXML(configuration);
    }

    private String generateSampleContent() {
        XStream xstream = xStreamFactory.createXStream();

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

    private File createTempFileWithContent(String content) throws IOException {
        File tempFile = File.createTempFile("111", "222");

        Writer writer = new FileWriter(tempFile);
        writer.append(content);
        writer.close();
        return tempFile;
    }
}
