package org.wso2.carbon.identity.api.otp.service.ivrotp.exception;

import org.apache.http.HttpHeaders;

import org.wso2.carbon.identity.api.otp.service.ivrotp.dto.Error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class handles the Conflict Request Exception.
 */
public class ConflictRequestException extends WebApplicationException {

    private String message;

    public ConflictRequestException(Error error) {

        super(Response.status(Response.Status.CONFLICT).entity(error)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build());
        message = error.getDescription();
    }

    public ConflictRequestException() {

        super(Response.Status.CONFLICT);
    }

    @Override
    public String getMessage() {

        return message;
    }
}
