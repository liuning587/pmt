package com.sx.mmt.constants;

import static com.sx.mmt.internal.util.JDBCHelp.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class AppParameterDao {
	public static Map<String,String> params;
	@PostConstruct
	public void load(){
		if(params==null){
			try{
				params=new ConcurrentHashMap<String, String>();
				Connection c=getConnection();
				String sql="select mykey,value from AppParameter";
				PreparedStatement ps=c.prepareStatement(sql);
				ResultSet rs=ps.executeQuery();
				while(rs.next()){
					params.put(rs.getString("mykey"), rs.getString("value"));
				}
				ps.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	
	public static void update(String key,String value){
		params.put(key, value);
		try{
			Connection c=getConnection();
			String sql="update AppParameter set value=? where mykey=?";
			PreparedStatement ps=c.prepareStatement(sql);
			ps.setString(1, value);
			ps.setString(2, key);
			ps.execute();
			ps.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public static String getValue(String key){
		String value=params.get(key);
		if(value==null){
			params.put(key, "");
			try{
				Connection c=getConnection();
				String sql="INSERT INTO AppParameter (mykey,value) VALUES (?,?)";
				PreparedStatement ps=c.prepareStatement(sql);
				ps.setString(1, key);
				ps.setString(2, "");
				ps.execute();
				ps.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		
		return value;
	}
	
	public static int getIntValue(String key){
		String value=(getValue(key));
		if(!StringUtils.isBlank(value)){
			return Integer.parseInt(value);
		}else{
			return 0;
		}
	}
	
	
}
