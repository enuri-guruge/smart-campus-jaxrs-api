package org.westminster;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import java.io.IOException;
import java.net.URI;

public class Main {
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static void main(String[] args) throws IOException {
        // Updated this line to scan your resources package specifically
        final ResourceConfig rc = new ResourceConfig()
                .packages("org.westminster.resources");

        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        System.out.println("🚀 API started!");
        System.out.println("Test Discovery: " + BASE_URI + "api/v1");
        System.out.println("Test Rooms:     " + BASE_URI + "api/v1/rooms");
        System.out.println("Press Enter to stop...");

        System.in.read();
        server.shutdownNow();
    }
}