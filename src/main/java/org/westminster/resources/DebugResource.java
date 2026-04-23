package org.westminster.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Resource for testing error handling and observability.
 * This endpoint is used to verify that the GlobalExceptionMapper
 * correctly catches unhandled exceptions and hides stack traces.
 */
@Path("/crash")
public class DebugResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response triggerError() {
        // Intentionally triggering a RuntimeException: Division by zero.
        // The GlobalExceptionMapper is expected to intercept this
        // and return a sanitized 500 Internal Server Error JSON response.
        int crash = 10 / 0;

        // This line will never be reached due to the exception above.
        return Response.ok("Success").build();
    }
}