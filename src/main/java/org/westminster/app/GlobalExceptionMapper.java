package org.westminster.app;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        // Log the error on the server console so YOU can see it
        System.err.println("🚨 Error caught by Mapper: " + exception.getMessage());

        // Create a sanitized JSON error message for the USER
        String errorMessage = "{\"error\": \"An internal server error occurred. Please contact the administrator.\"}";

        // Return a 500 Internal Server Error status
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(errorMessage)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
