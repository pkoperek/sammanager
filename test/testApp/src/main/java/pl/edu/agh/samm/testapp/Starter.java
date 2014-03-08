package pl.edu.agh.samm.testapp;

import com.vaadin.server.VaadinServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import pl.edu.agh.samm.testapp.core.WorkloadGeneratorFacade;

/**
 * User: koperek
 * Date: 26.01.14
 * Time: 13:56
 */
public class Starter {

    public static final String HTTP_PORT_PROPERTY = "pl.edu.agh.samm.testapp.httpport";
    public static final String DEFAULT_HTTP_PORT = "8080";

    public static void main(String[] args) throws Exception {
        initSystemVariables();

        WorkloadGeneratorFacade.getInstance(); // init workload generator

        startWebServer(getPort());
    }

    private static int getPort() {
        try {
            return Integer.parseInt(System.getProperty(HTTP_PORT_PROPERTY, DEFAULT_HTTP_PORT));
        } catch (NumberFormatException e) {
            System.out.println("Couldn't read the pl.edu.agh.samm.testapp.httpport parameter! Using default http port: " + DEFAULT_HTTP_PORT);
        }

        return Integer.parseInt(DEFAULT_HTTP_PORT);
    }

    private static void initSystemVariables() {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    private static void startWebServer(int port) throws Exception {
        Server server = new Server(port);

        ServletHolder servletHolder = new ServletHolder();
        servletHolder.setName("vaadinServlet");
        servletHolder.setClassName(VaadinServlet.class.getName());
        servletHolder.setInitParameter("UI", SAMMTestApplication.class.getCanonicalName());

        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.addServlet(servletHolder, "/*");

        server.setHandler(servletHandler);

        server.start();
        server.join();
    }
}
