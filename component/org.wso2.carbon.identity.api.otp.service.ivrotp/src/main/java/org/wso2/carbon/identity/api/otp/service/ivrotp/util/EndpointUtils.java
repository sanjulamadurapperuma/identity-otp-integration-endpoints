package org.wso2.carbon.identity.api.otp.service.ivrotp.util;

import org.apache.commons.logging.Log;
import org.apache.log4j.MDC;
import org.wso2.carbon.context.PrivilegedCarbonContext;
import org.wso2.carbon.identity.api.otp.service.ivrotp.dto.Error;
import org.wso2.carbon.identity.api.otp.service.ivrotp.exception.BadRequestException;
import org.wso2.carbon.identity.api.otp.service.ivrotp.exception.ConflictRequestException;
import org.wso2.carbon.identity.api.otp.service.ivrotp.exception.ForbiddenException;
import org.wso2.carbon.identity.api.otp.service.ivrotp.exception.InternalServerErrorException;
import org.wso2.carbon.identity.api.otp.service.ivrotp.exception.NotFoundException;
import org.wso2.carbon.identity.ivrotp.common.IVRCommonConstants;
import org.wso2.carbon.identity.ivrotp.common.IVROTPService;
import org.wso2.carbon.identity.ivrotp.common.exception.IVROTPClientException;
import org.wso2.carbon.identity.ivrotp.common.exception.IVROTPException;

import java.util.UUID;

import javax.ws.rs.core.Response;

import static org.wso2.carbon.identity.api.otp.service.ivrotp.constant.IVRConstants.CORRELATION_ID_MDC;

/**
 * This class provides util functions for IVR OTP REST APIs.
 */
public class EndpointUtils {

    public static IVROTPService getIVROTPService() {

        return (IVROTPService) PrivilegedCarbonContext.getThreadLocalCarbonContext().
                getOSGiService(IVROTPService.class, null);
    }

    private static void logDebug(Log log, Throwable throwable) {

        if (log.isDebugEnabled()) {
            log.debug(Response.Status.BAD_REQUEST, throwable);
        }
    }

    private static void logError(Log log, Throwable throwable) {

        log.error(throwable.getMessage(), throwable);
    }

    public static String getCorrelation() {

        String ref;
        if (isCorrelationIDPresent()) {
            ref = MDC.get(CORRELATION_ID_MDC).toString();
        } else {
            ref = UUID.randomUUID().toString();
        }
        return ref;
    }

    public static boolean isCorrelationIDPresent() {

        return MDC.get(CORRELATION_ID_MDC) != null;
    }

    private static Error getError(String message, String description, String code) {

        Error errorDTO = new Error();
        errorDTO.setCode(code);
        errorDTO.setMessage(message);
        errorDTO.setDescription(description);
        errorDTO.setTraceId(getCorrelation());
        return errorDTO;
    }

    public static Response handleBadRequestResponse(IVROTPClientException e, Log log) {

        if (isNotFoundError(e)) {
            throw buildNotFoundRequestException(e.getDescription(), e.getMessage(), e.getErrorCode(), log, e);
        }

        if (isConflictError(e)) {
            throw buildConflictRequestException(e.getDescription(), e.getMessage(), e.getErrorCode(), log, e);
        }

        if (isForbiddenError(e)) {
            throw buildForbiddenException(e.getDescription(), e.getMessage(), e.getErrorCode(), log, e);
        }
        throw buildBadRequestException(e.getDescription(), e.getMessage(), e.getErrorCode(), log, e);
    }

    public static Response handleServerErrorResponse(IVROTPException e, Log log) {

        throw buildInternalServerErrorException(e.getErrorCode(), log, e);
    }

    public static Response handleUnexpectedServerError(Throwable e, Log log) {

        throw buildInternalServerErrorException(IVRCommonConstants.ErrorMessage.SERVER_UNEXPECTED_ERROR.getCode(),
                log, e);
    }

    private static boolean isNotFoundError(IVROTPClientException e) {

        return IVRCommonConstants.isNotFoundError(e.getErrorCode());
    }

    private static boolean isConflictError(IVROTPClientException e) {

        return IVRCommonConstants.isConflictError(e.getErrorCode());
    }

    private static boolean isForbiddenError(IVROTPClientException e) {

        return IVRCommonConstants.isForbiddenError(e.getErrorCode());
    }

    public static NotFoundException buildNotFoundRequestException(String description, String message, String code,
                                                                  Log log, Throwable e) {

        Error errorDTO = getError(message, description, code);
        logDebug(log, e);
        return new NotFoundException(errorDTO);
    }

    public static ForbiddenException buildForbiddenException(String description, String message, String code, Log log,
                                                             Throwable e) {

        Error errorDTO = getError(message, description, code);
        logDebug(log, e);
        return new ForbiddenException(errorDTO);
    }

    public static BadRequestException buildBadRequestException(String description, String message, String code, Log log,
                                                               Throwable e) {

        Error errorDTO = getError(message, description, code);
        logDebug(log, e);
        return new BadRequestException(errorDTO);
    }

    public static InternalServerErrorException buildInternalServerErrorException(String code, Log log, Throwable e) {

        Error errorDTO = getError(Response.Status.INTERNAL_SERVER_ERROR.toString(),
                Response.Status.INTERNAL_SERVER_ERROR.toString(), code);
        logError(log, e);
        return new InternalServerErrorException(errorDTO);
    }

    public static ConflictRequestException buildConflictRequestException(String description, String message,
                                                                         String code, Log log, Throwable e) {

        Error errorDTO = getError(message, description, code);
        logDebug(log, e);
        return new ConflictRequestException(errorDTO);
    }
}
