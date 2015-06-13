package com.sx.mmt.internal.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

@SuppressWarnings({"rawtypes","unchecked"})
public class MyPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer{
	
	private static Map propertiesMap;

	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {
		super.processProperties(beanFactoryToProcess, props);
		propertiesMap=new HashMap();
		for(Object key:props.keySet()){
			String keystr=key.toString();
			String value=props.getProperty(keystr);
			propertiesMap.put(keystr, value);
		}
	}
	
	public static Object getProperty(String name){
		return propertiesMap.get(name);
	}

}
