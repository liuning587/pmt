package com.sx.mmt.internal.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.DailyRollingFileAppender;

public class LogAppender extends DailyRollingFileAppender{
	private String datePattern = "yyyy-MM-dd";
    @Override
    public void setFile(String file)
    {
    	String path = System.getProperty("user.dir")+"/";
        String val = file.trim();
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        String dateName = sdf.format(new Date());
        String dirName = path+val;
        File fdir = new File(dirName);
        if (!fdir.exists()){
        	fdir.mkdirs();
        }
         
        StringBuffer buf = new StringBuffer();
        buf.append(dirName).append("/").append(dateName).append(".log");
         
        fileName = buf.toString();
    }
}
