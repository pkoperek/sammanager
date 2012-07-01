package pl.edu.agh.samm.tadapter.jmx;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * Created by IntelliJ IDEA.
 * User: koperek
 * Date: 01.07.12
 * Time: 14:17
 */

public class JMXAdapterConfiguratorTest {
    public static final String VALUE = "val";
    public static final String PROPERTY = "prop";
    private JMXAdapterConfigurator jmxAdapterConfigurator;

    @Before
    public void setUp() throws Exception {
        jmxAdapterConfigurator = new JMXAdapterConfigurator();
    }

    @Test
    public void testInit() throws Exception {
        // Given
        File tempFile = File.createTempFile("pfix", "sfix");
        tempFile.deleteOnExit();
        storeProperties(tempFile);

        System.setProperty(JMXAdapterConfigurator.ENV_PROPERTIES_FILE, tempFile.getAbsolutePath());

        // When
        jmxAdapterConfigurator.init();

        // Then
        assertEquals(VALUE, jmxAdapterConfigurator.getProperty(PROPERTY));
    }

    private void storeProperties(File tempFile) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
        storeProperties(fileOutputStream);
        fileOutputStream.close();
    }

    private void storeProperties(FileOutputStream fileOutputStream) throws IOException {
        Properties properties = new Properties();
        properties.setProperty(PROPERTY, VALUE);
        properties.store(fileOutputStream, "comment");
    }

    @Test
    public void testContext() throws Exception {
        // Given
        ClassPathXmlApplicationContext xmlApplicationContext = new ClassPathXmlApplicationContext("test-context.xml");

        // When
        JMXAdapterConfigurator configurator = (JMXAdapterConfigurator)xmlApplicationContext.getBean("jmxAdapterConfigurator");

        // Then
        assertNotNull(configurator);
        assertEquals("testval",configurator.getProperty("testprop"));
    }
}
