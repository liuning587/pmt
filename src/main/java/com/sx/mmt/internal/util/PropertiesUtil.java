package com.sx.mmt.internal.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public final class PropertiesUtil {
	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class); 
	public static final String path=System.getProperty("user.dir")+"/config/application.properties";
	private static Properties prop;
	@PostConstruct
	public void load(){
		prop = new Properties();
		FileInputStream fis=null;
		try {
			fis = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		}
		try {
			prop.load(fis);
		} catch (IOException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		}
	}
	
	public void modify(String key,String value){
		prop.setProperty(key, value);
	}
	
	public String show(){
		List<String> keylist=new ArrayList<String>();
		for(Object s:prop.keySet()){
			keylist.add((String)s);
		}
		Collections.sort(keylist);
		StringBuilder sb=new StringBuilder();
		for(String s:keylist){
			sb.append(s).append("=").append(prop.get(s)).append("\n");
		}
		return sb.toString();
	}
	
	public void save(String s){
		String[] entry=s.split("\n");
		prop.clear();
		for(String str:entry){
			str=str.trim();
			prop.put(str.substring(0,str.indexOf("=")),str.substring(str.indexOf("=")+1,str.length()));
		}
		save();
	}
	
	public void save(){
		FileOutputStream fos=null;
		try{
			fos=new FileOutputStream(path);
		}catch (FileNotFoundException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
			e.printStackTrace();
		}
		try {
			prop.store(fos, "my");
		} catch (IOException e) {
			logger.error(ErrorTool.getErrorInfoFromException(e));
		}
	}
	
	public static String parseToStr(String keyName) {
		if(prop!=null){
			return String.valueOf(prop.getProperty(keyName)).trim();
		}else{
			return "";
		}
    }
	
	public static int parseToInt(String keyName) {
		if(prop!=null){
			return Integer.parseInt(String.valueOf(prop.getProperty(keyName)).trim());
		}else{
			return 0;
		}
    }
	
	public static double parseToDouble(String keyName) {
		if(prop!=null){
			return Double.parseDouble(String.valueOf(prop.getProperty(keyName)).trim());
		}else{
			return 0.0;
		}
    }
	
	public static boolean parseToBoolean(String keyName) {
		if(prop!=null){
			return Boolean.parseBoolean(String.valueOf(prop.getProperty(keyName)).trim());
		}else{
			return false;
		}
    }
}
