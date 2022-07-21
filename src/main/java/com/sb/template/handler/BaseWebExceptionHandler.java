package com.sb.template.handler;

import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;

import com.sb.template.aop.CommonAopLog;
import com.sb.template.controller.BoardController;
import com.sb.template.exception.InvalidParamException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice(assignableTypes = {DispatcherServlet.class, BoardController.class, CommonAopLog.class})
public class BaseWebExceptionHandler {

	protected ModelAndView makeResult(String handlerType, String msg, String url, String view) {
		ModelAndView mav = new ModelAndView();

		String message = (msg == null || msg == "") ? "Server Error" : msg;
		String redirectUrl = (url == null || url == "") ? "http://localhost:8080/" : url;
		String viewPath = (view == null || view == "") ? "/common/error" : view;

		mav.addObject("message", message);
		mav.addObject("redirectUrl", redirectUrl);
		mav.setViewName(viewPath);

		log.error("handlerType : {}, Message : {}, RedirectUrl : {}, View : {}",handlerType, message, redirectUrl, viewPath);

		return mav;
	}


	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler
	public ModelAndView processErrorHandler(HttpServletRequest req, Exception e) {

		String handlerType = Thread.currentThread().getStackTrace()[1].getMethodName();
		String message = e.getStackTrace().toString();

		return makeResult(handlerType, message, null, null);
	}


	@ResponseStatus(value = HttpStatus.NOT_FOUND)
	@ExceptionHandler
	public ModelAndView notFoundErrorHandler(HttpServletRequest req, NoSuchElementException e) {

		req.setAttribute("requestDispatcherPath", "");
		String handlerType = Thread.currentThread().getStackTrace()[1].getMethodName();
		String message = e.getMessage();
		String redirectUrl = (req.getHeader("referer") == null ||req.getHeader("referer").isBlank()) ?  "http://localhost:8080/board/list" : req.getHeader("referer");
		String view = "/common/error";

		return makeResult(handlerType, message, redirectUrl, view);
	}


	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	@ExceptionHandler
	public ModelAndView badRequestErrorHandler(HttpServletRequest req, InvalidParamException e) {

		String handlerType = Thread.currentThread().getStackTrace()[1].getMethodName();
		String message = e.getMessage();
		String redirectUrl = (req.getHeader("referer") == null ||req.getHeader("referer").isBlank()) ?  "http://localhost:8080/board/list" : req.getHeader("referer");
		String view = "/common/error";

		return makeResult(handlerType, message, redirectUrl, view);
	}
}
