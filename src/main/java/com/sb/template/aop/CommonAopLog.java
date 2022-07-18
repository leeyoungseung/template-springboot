package com.sb.template.aop;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.util.GenericSignature.ClassSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@Aspect
public class CommonAopLog {

	@Pointcut("execution(* com.sb.template.controller.*.*(..))")
	public void pointCut() {}

	@Pointcut("@annotation(com.sb.template.annotation.Timer)")
	public void timerPointCut() {}

	@Before("pointCut()")
	public void beforeProcess(JoinPoint joinPoint) {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		Object[] objs = joinPoint.getArgs();
		log.info("Start-{} ", methodSignature.getDeclaringTypeName() + "." + method.getName());
		log.info("Request Param : {} ", objs);
	}

	@AfterReturning(value = "pointCut()", returning = "returnValue")
	public void afterReturning(JoinPoint joinPoint, Object returnValue) {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		Method method = methodSignature.getMethod();
		log.info("End-{} ", methodSignature.getDeclaringTypeName() + "." + method.getName());
		log.info("Response Param : {} ", returnValue);
	}

	@Around("timerPointCut()")
	public Object timerProcess(ProceedingJoinPoint proceedingJoinPoint) {
		StopWatch stopWatch = new StopWatch();
		Object res = new Object();

		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		Method method = methodSignature.getMethod();

		Object[] objs = proceedingJoinPoint.getArgs();
		log.info("Start-{} ", methodSignature.getDeclaringTypeName() + "." + method.getName());
		log.info("Input : {} ", objs);

		try {
			stopWatch.start();
			res = proceedingJoinPoint.proceed();
			log.info("End-{} ",  methodSignature.getDeclaringTypeName() + "." + method.getName());
			log.info("Output : {} ", res);
			stopWatch.stop();
			log.info("Process execution time  : {} ", stopWatch.getTotalTimeSeconds());
			return res;
		} catch(Throwable throwable) {
			log.error("Process-Error-{} , Message : {} ",
					method.getName(), throwable.getMessage());
			return res;
		}
	}

}
