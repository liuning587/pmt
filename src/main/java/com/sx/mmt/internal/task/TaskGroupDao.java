package com.sx.mmt.internal.task;

import static com.sx.mmt.internal.util.JDBCHelp.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;


@Component

public class TaskGroupDao {

	
	public List<TaskGroup> groups;
	
	@PostConstruct
	public void load(){
		if(groups==null){
			try{
				groups=new Vector<TaskGroup>();
				Connection c=getConnection();
				String sql="select taskTag,name,parentTag from TaskGroup order by taskTag";
				PreparedStatement ps=c.prepareStatement(sql);
				ResultSet rs=ps.executeQuery();
				while(rs.next()){
					TaskGroup taskGroup=new TaskGroup();
					taskGroup.setName(rs.getString("name"));
					taskGroup.setTaskTag(rs.getString("taskTag"));
					taskGroup.setParentTag(rs.getString("parentTag"));
					groups.add(taskGroup);
				}
				ps.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
	}
	public TaskGroup get(String tag){
		for(TaskGroup taskGroup:groups){
			if(taskGroup.getTaskTag().equals(tag)){
				return taskGroup;
			}
		}
		return null;
	}
	
	public List<TaskGroup> getChildren(String tag){
		List<TaskGroup> taskgroups=new Vector<TaskGroup>();
		for(TaskGroup taskGroup:groups){
			if(tag.equals(taskGroup.getParentTag())){
				taskgroups.add(taskGroup);
			}
		}
		return taskgroups;
	}
	
	public boolean isLeaf(String tag){
		for(TaskGroup taskGroup:groups){
			if(tag.equals(taskGroup.getParentTag())){
				return false;
			}
		}
		return true;
	}
	
	public void save(TaskGroup newGroup){
		try{
			Connection c=getConnection();
			String sql="insert into TaskGroup (taskTag,name,parentTag) values(?,?,?)";
			PreparedStatement ps=c.prepareStatement(sql);
			ps.setString(1, newGroup.getTaskTag());
			ps.setString(2, newGroup.getName());
			ps.setString(3, newGroup.getParentTag());
			ps.execute();
			ps.close();
			List<TaskGroup> removeList=new ArrayList<TaskGroup>();
			for(TaskGroup tg:groups){
				if(tg.getTaskTag().startsWith(newGroup.getTaskTag())){
					removeList.add(tg);
				}
			}
			for(TaskGroup tg:removeList){
				groups.remove(tg);
			}
			groups.add(newGroup);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void update(TaskGroup newGroup){
		try{
			Connection c=getConnection();
			String sql="update TaskGroup set name=?,parentTag=? where taskTag=?";
			PreparedStatement ps=c.prepareStatement(sql);
			ps.setString(1, newGroup.getName());
			ps.setString(2, newGroup.getParentTag());
			ps.setString(3, newGroup.getTaskTag());
			ps.execute();
			ps.close();
			List<TaskGroup> removeList=new ArrayList<TaskGroup>();
			for(TaskGroup tg:groups){
				if(tg.getTaskTag().startsWith(newGroup.getTaskTag())){
					removeList.add(tg);
				}
			}
			for(TaskGroup tg:removeList){
				groups.remove(tg);
			}
			groups.add(newGroup);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void delete(TaskGroup group){
		try{
			Connection c=getConnection();
			String sql="delete from TaskGroup where taskTag like ?";
			PreparedStatement ps=c.prepareStatement(sql);
			ps.setString(1, group.getTaskTag()+"%");
			ps.execute();
			ps.close();
			List<TaskGroup> removeList=new ArrayList<TaskGroup>();
			for(TaskGroup tg:groups){
				if(tg.getTaskTag().startsWith(group.getTaskTag())){
					removeList.add(tg);
				}
			}
			for(TaskGroup tg:removeList){
				groups.remove(tg);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
