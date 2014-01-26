package pl.edu.agh.samm.testapp;

import org.eclipse.jetty.server.Server;

/**
 * User: koperek
 * Date: 26.01.14
 * Time: 13:56
 */
public class Starter {
    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);

        server.start();
        server.join();
    }
}
