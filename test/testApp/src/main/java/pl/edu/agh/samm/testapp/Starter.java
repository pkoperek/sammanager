package pl.edu.agh.samm.testapp;

import com.vaadin.server.VaadinServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * User: koperek
 * Date: 26.01.14
 * Time: 13:56
 */
public class Starter {

    public static final String VAADIN_SERVLET = "vaadinServlet";

    public static void main(String[] args) throws Exception {
        int port = 8080;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                System.out.println("Using value: " + port + " as server port");
            } catch (NumberFormatException nfe) {
                System.err.println("Passed string is not a number! (" + args[0] + ")");
            }
        }

        startWebServer(port);
    }

    private static void startWebServer(int port) throws Exception {
        Server server = new Server(port);

        ServletHolder servletHolder = new ServletHolder();
        servletHolder.setName(VAADIN_SERVLET);
        servletHolder.setClassName(VaadinServlet.class.getName());
        servletHolder.setInitParameter("UI", SAMMTestApplication.class.getCanonicalName());

        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.addServlet(servletHolder, "/*");

        server.setHandler(servletHandler);

        server.start();
        server.join();
    }
}
