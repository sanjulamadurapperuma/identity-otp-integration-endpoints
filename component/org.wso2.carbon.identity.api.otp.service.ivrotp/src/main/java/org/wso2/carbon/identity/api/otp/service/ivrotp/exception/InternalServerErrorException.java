package org.wso2.carbon.identity.api.otp.service.ivrotp.exception;

import org.apache.http.HttpHeaders;
import org.wso2.carbon.identity.api.otp.service.ivrotp.dto.Error;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * This class handles the Internal Server Error Exception.
 */
public class InternalServerErrorException extends WebApplicationException {

    public InternalServerErrorException(Error error) {

        super(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON).build());
    }
}
