package com.sx.mmt.internal.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTool {
	public static SimpleDateFormat sdf1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat sdf2=new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat sdf3=new SimpleDateFormat("HH:mm:ss");
	public static String getDateString(Date date){
		if(date==null){
			return "";
		}else{
			String now=sdf1.format(date);
			return now;
		}
	}
	
	public static Date getDateFromString(String s){
		Date date=null;
		try {
			date=sdf2.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	public static String getDateStringNoTime(Date date){
		if(date==null){
			return "";
		}else{
			String now=sdf2.format(date);
			return now;
		}
	}
	
	public static String getTimeString(Date date){
		if(date==null){
			return "";
		}else{
			String now=sdf3.format(date);
			return now;
		}
	}
}
