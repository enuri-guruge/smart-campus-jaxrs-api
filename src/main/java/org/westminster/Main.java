package org.westminster;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.westminster.app.GlobalExceptionMapper;
import org.westminster.resources.DiscoveryResource;
import org.westminster.resources.RoomResource;
import org.westminster.resources.SensorResource;
import org.westminster.resources.ReadingResource;
import org.westminster.resources.DebugResource;

import java.io.IOException;
import java.net.URI;

/**
 * Main entry point for the Smart Campus API.
 * Student: Emith Jayasuriya
 * ID: W2082182
 */
public class Main {
    // Base URI for the Grizzly HTTP server
    public static final String BASE_URI = "http://localhost:8080/api/v1/";

    public static void main(String[] args) throws IOException {
        // ResourceConfig is used to configure the JAX-RS application
        final ResourceConfig rc = new ResourceConfig();

        // 1. Manually Register Resource (Controller) classes
        // This ensures the server finds your endpoints even if package scanning fails
        rc.register(DiscoveryResource.class);
        rc.register(RoomResource.class);
        rc.register(SensorResource.class);
        rc.register(ReadingResource.class);
        rc.register(DebugResource.class);

        // 2. Register the Global Exception Mapper
        // This catches all server crashes and returns sanitized JSON instead of stack traces
        rc.register(GlobalExceptionMapper.class);

        // 3. Initialize and start the Grizzly HTTP server
        final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);

        System.out.println("--------------------------------------------------");
        System.out.println("🚀 SMART CAMPUS API - RUNNING");
        System.out.println("--------------------------------------------------");
        System.out.println("Root/Discovery: " + BASE_URI);
        System.out.println("Rooms:          " + BASE_URI + "rooms");
        System.out.println("Sensors:        " + BASE_URI + "sensors");
        System.out.println("Crash Test:     " + BASE_URI + "crash");
        System.out.println("--------------------------------------------------");
        System.out.println("Status: Server is listening on port 8080...");
        System.out.println("Action: Press [ENTER] to stop the server.");
        System.out.println("--------------------------------------------------");

        // Keep the application running until Enter is pressed
        System.in.read();

        System.out.println("Stopping server...");
        server.shutdownNow();
        System.out.println("Server stopped. Goodbye!");
    }
}