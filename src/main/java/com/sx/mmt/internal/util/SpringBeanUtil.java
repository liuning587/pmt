package com.sx.mmt.internal.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@SuppressWarnings("static-access")
public class SpringBeanUtil implements ApplicationContextAware{
	public static final String KEY="propertyConfigurer";
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		SpringBeanUtil.applicationContext=applicationContext;
		
	}
	
	public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
	
	
	public static Object getBean(String beanName) throws BeansException {
		return applicationContext.getBean(beanName);
	}
	

	
	
	
}
