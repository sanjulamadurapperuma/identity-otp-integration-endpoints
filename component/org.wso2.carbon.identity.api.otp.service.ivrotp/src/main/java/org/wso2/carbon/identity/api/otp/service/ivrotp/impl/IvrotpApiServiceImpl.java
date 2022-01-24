/*
 * Copyright (c) 2022, WSO2 Inc. (http://www.wso2.com).
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.api.otp.service.ivrotp.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.identity.api.otp.service.ivrotp.IvrotpApiService;
import org.wso2.carbon.identity.api.otp.service.ivrotp.dto.OTPGenerateResponse;
import org.wso2.carbon.identity.api.otp.service.ivrotp.dto.OTPGenerationRequest;
import org.wso2.carbon.identity.api.otp.service.ivrotp.dto.OTPValidationFailureReason;
import org.wso2.carbon.identity.api.otp.service.ivrotp.dto.OTPValidationRequest;
import org.wso2.carbon.identity.api.otp.service.ivrotp.dto.OTPValidationResponse;
import org.wso2.carbon.identity.api.otp.service.ivrotp.util.EndpointUtils;
import org.wso2.carbon.identity.ivrotp.common.dto.FailureReasonDTO;
import org.wso2.carbon.identity.ivrotp.common.dto.GenerationResponseDTO;
import org.wso2.carbon.identity.ivrotp.common.dto.ValidationResponseDTO;
import org.wso2.carbon.identity.ivrotp.common.exception.IVROTPClientException;
import org.wso2.carbon.identity.ivrotp.common.exception.IVROTPException;

import javax.ws.rs.core.Response;

/**
 * This class implements the service layer for org.wso2.carbon.identity.api.otp.service.ivrotp.IvrotpApi.
 */
public class IvrotpApiServiceImpl implements IvrotpApiService {

    private static final Log log = LogFactory.getLog(IvrotpApiServiceImpl.class);

    @Override
    public Response ivrotpGeneratePost(OTPGenerationRequest otpGenerationRequest) {

        String userId = StringUtils.trim(otpGenerationRequest.getUserId());
        try {
            GenerationResponseDTO responseDTO = EndpointUtils.getIVROTPService().generateIVROTP(userId);
            OTPGenerateResponse response = new OTPGenerateResponse()
                    .transactionId(responseDTO.getTransactionId())
                    .ivrOTP(responseDTO.getIvrOTP());
            return Response.ok(response).build();
        } catch (IVROTPClientException e) {
            return EndpointUtils.handleBadRequestResponse(e, log);
        } catch (IVROTPException e) {
            return EndpointUtils.handleServerErrorResponse(e, log);
        } catch (Throwable e) {
            return EndpointUtils.handleUnexpectedServerError(e, log);
        }
    }

    @Override
    public Response ivrotpValidatePost(OTPValidationRequest otpValidationRequest) {

        String transactionId = StringUtils.trim(otpValidationRequest.getTransactionId());
        String userId = StringUtils.trim(otpValidationRequest.getUserId());
        String ivrOtp = StringUtils.trim(otpValidationRequest.getIvrOTP());
        try {
            ValidationResponseDTO responseDTO = EndpointUtils.getIVROTPService().validateIVROTP(
                    transactionId, userId, ivrOtp);
            FailureReasonDTO failureReasonDTO = responseDTO.getFailureReason();
            OTPValidationFailureReason failureReason = null;
            if (failureReasonDTO != null) {
                failureReason = new OTPValidationFailureReason()
                        .code(failureReasonDTO.getCode())
                        .message(failureReasonDTO.getMessage())
                        .description(failureReasonDTO.getDescription());
            }
            OTPValidationResponse response = new OTPValidationResponse()
                    .isValid(responseDTO.isValid())
                    .userId(responseDTO.getUserId())
                    .failureReason(failureReason);
            return Response.ok(response).build();
        } catch (IVROTPClientException e) {
            return EndpointUtils.handleBadRequestResponse(e, log);
        } catch (IVROTPException e) {
            return EndpointUtils.handleServerErrorResponse(e, log);
        } catch (Throwable e) {
            return EndpointUtils.handleUnexpectedServerError(e, log);
        }
    }
}
