package com.sb.template.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sb.template.interceptor.RememberMeInterceptor;


@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private RememberMeInterceptor rememberMeInterceptor;


	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(rememberMeInterceptor)
		    .order(1)
		    .addPathPatterns("/board/**")
		    .excludePathPatterns("/auth/logout", "/auth/join", "/common/**")
		    ;
	}

}
