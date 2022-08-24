package com.sb.template.handler;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.sb.template.controller.ReplyController;
import com.sb.template.dto.ResponseDto;
import com.sb.template.enums.ResponseInfo;
import com.sb.template.exception.InvalidParamException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(assignableTypes = {ReplyController.class})
public class BaseApiExceptionHandler extends ResponseEntityExceptionHandler{


	public ResponseEntity<?> makeResponse(HttpStatus status, ResponseInfo info, Object data) {
		return ResponseEntity
				.status(status)
				.body(
						ResponseDto.builder()
						.resultCode(info.getResultCode())
						.message(info.getMessage())
						.data(data)
						.build()
					);
	}


	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public ResponseEntity<?> processExceptionHandler(Exception e) {

		log.error(e.getMessage());

		return makeResponse(
				HttpStatus.INTERNAL_SERVER_ERROR,
				ResponseInfo.SERVER_ERROR,
				e.getMessage()
				);
	}


	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ExceptionHandler
	public ResponseEntity<?> noSuchElementExceptionHandler(NoSuchElementException e) {

		log.error(e.getMessage());

		return makeResponse(
				HttpStatus.NO_CONTENT,
				ResponseInfo.NO_CONTENT,
				e.getMessage()
				);
	}


	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ResponseEntity<?> InvalidParamExceptionHandler(InvalidParamException e) {

		log.error(e.getMessage());

		return makeResponse(
				HttpStatus.BAD_REQUEST,
				ResponseInfo.PARAM_ERROR,
				e.getMessage()
				);
	}


}
