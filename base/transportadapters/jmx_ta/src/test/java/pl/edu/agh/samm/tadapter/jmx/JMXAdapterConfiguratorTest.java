package pl.edu.agh.samm.tadapter.jmx;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/test-context.xml"})
public class JMXAdapterConfiguratorTest {
    public static final String VALUE = "val";
    public static final String PROPERTY = "prop";
    private JMXAdapterConfigurator jmxAdapterConfigurator;

    @Autowired
    private ApplicationContext applicationContext;

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

        // When
        JMXAdapterConfigurator configurator = (JMXAdapterConfigurator)applicationContext.getBean("jmxAdapterConfigurator");

        // Then
        assertNotNull(configurator);
        assertEquals("testval",configurator.getProperty("testprop"));
    }
}
