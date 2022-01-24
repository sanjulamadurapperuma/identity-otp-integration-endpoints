package org.wso2.carbon.identity.api.otp.service.ivrotp.exception;

import org.apache.http.HttpHeaders;
import org.wso2.carbon.identity.api.otp.service.ivrotp.dto.Error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class handles the Forbidden Exception.
 */
public class ForbiddenException extends WebApplicationException {

    private String message;

    public ForbiddenException(Error error) {

        super(Response.status(Response.Status.FORBIDDEN).entity(error)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build());
        message = error.getDescription();
    }

    public ForbiddenException() {

        super(Response.Status.FORBIDDEN);
    }

    @Override
    public String getMessage() {

        return message;
    }
}
