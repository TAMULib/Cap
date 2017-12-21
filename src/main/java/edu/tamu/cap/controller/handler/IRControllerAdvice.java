package edu.tamu.cap.controller.handler;

import static edu.tamu.weaver.response.ApiStatus.ERROR;

import org.fcrepo.client.FcrepoOperationFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.cap.exceptions.IRVerificationException;
import edu.tamu.weaver.response.ApiResponse;

@RestController
@ControllerAdvice
public class IRControllerAdvice {

	@ResponseStatus(value=HttpStatus.OK)
	@ExceptionHandler(FcrepoOperationFailedException.class)
	public ApiResponse handleFcrepoOperationFailedException(FcrepoOperationFailedException e) {
		return new ApiResponse(ERROR, e.getMessage());
	}
	
	@ResponseStatus(value=HttpStatus.OK)
	@ExceptionHandler(IRVerificationException.class)
	public ApiResponse handleIRVerificationException(IRVerificationException e) {
		return new ApiResponse(ERROR, e.getMessage());
	}
	
}
