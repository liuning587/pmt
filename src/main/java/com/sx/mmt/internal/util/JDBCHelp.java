package com.sx.mmt.internal.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

public class JDBCHelp {
	private static Connection con = null;
	private static Logger logger = LoggerFactory.getLogger(JDBCHelp.class);

	public static void createConnection() throws Exception { 
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");// 加载数据驱动  
              
            con = DriverManager.getConnection(  
                    "jdbc:derby:db;create=true", "sx", "sx");// 创建数据连接  
    }
	
	public static void checkTable(){
		ResultSet oResults = null;
        String[] types = {"TABLE"};
        List<String> tables=Lists.newArrayList("TASK","TASKGROUP","APPPARAMETER");
        try {
            oResults = con.getMetaData().getTables(null, null, null, types);
            while (oResults.next()) {   
                  String tt = oResults.getString("TABLE_NAME");
                  tables.remove(tt);
            }
            oResults.close();
            for(String s:tables){
            	if(s.equals("TASK")){
            		StringBuilder sb=new StringBuilder();
            		sb.append("CREATE TABLE TASK (")
            		.append("ID varchar(255) PRIMARY KEY NOT NULL,")
            		.append("TERMINALADDRESS varchar(255),")
            		.append("TASKNAME varchar(255),")
            		.append("TASKSTATUS varchar(255),")
            		.append("CREATETIME timestamp,")
            		.append("FINISHTIME timestamp,")
            		.append("ACTIONNOW varchar(255),")
            		.append("TERMINALRETURN varchar(255),")
            		.append("TASKGROUPTAG varchar(255),")
            		.append("NEXTACTIONTIME bigint,")
            		.append("COUNTER int,")
            		.append("PFC int,")
            		.append("CURRENTSTEPINDEX int,")
            		.append("STEPSTATUS varchar(255),")
            		.append("packetIndex bigint,")
            		.append("retryCount bigint,")
            		.append("priority int,")
            		.append("additionalParam1 varchar(2048),")
            		.append("additionalParam2 varchar(2048),")
            		.append("additionalParam3 varchar(2048))");
            		Statement stmt = con.createStatement();
            		stmt.execute(sb.toString());
            		stmt.close();
            		
            	}else if(s.equals("TASKGROUP")){
            		StringBuilder sb=new StringBuilder();
            		sb.append("CREATE TABLE TASKGROUP (")
            		.append("TASKTAG varchar(255) PRIMARY KEY NOT NULL,")
            		.append("NAME varchar(255),")
            		.append("PARENTTAG varchar(255))");
            		Statement stmt = con.createStatement();
            		stmt.execute(sb.toString());
            		sb.delete(0, sb.length());
            		sb.append("INSERT INTO TASKGROUP (TASKTAG,NAME,PARENTTAG) ")
            		.append("VALUES ('000','全部任务',null)");
            		stmt.execute(sb.toString());
            		sb.delete(0, sb.length());
            		sb.append("INSERT INTO TASKGROUP (TASKTAG,NAME,PARENTTAG) ")
            		.append("VALUES ('000000','默认分组','000')");
            		stmt.execute(sb.toString());
            		stmt.close();
            	}else if(s.equals("APPPARAMETER")){
            		StringBuilder sb=new StringBuilder();
            		sb.append("CREATE TABLE APPPARAMETER (")
            		.append("MYKEY varchar(255) PRIMARY KEY NOT NULL,")
            		.append("VALUE varchar(255))");
            		Statement stmt = con.createStatement();
            		stmt.execute(sb.toString());
            		stmt.close();
            	}
            }
        } catch (SQLException e) {
        	logger.error(ErrorTool.getErrorInfoFromException(e));
        }
	}
	
	public static Connection getConnection(){
		if(con==null){
			try {
				createConnection();
			} catch (Exception e) {
				logger.error(ErrorTool.getErrorInfoFromException(e));
			}
		}
		return con;
	}
	
	public static void dispose(){
		if(con!=null){
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}

