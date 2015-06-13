package com.sx.mmt.internal.task;

import static com.sx.mmt.internal.util.JDBCHelp.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.util.ErrorTool;

@Component
public class TaskImplDao {
	
	@Autowired
	private TaskManager taskManager;
	
	private ExecutorService executorPool;
	public TaskImplDao(){
		executorPool=Executors.newSingleThreadExecutor();
	}
	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}
	
	public void execute(Runnable runnable){
		executorPool.execute(runnable);
	}

	public long getTreeTaskCount(String groupTag,String filter){
		try{
			Connection c=getConnection();
			String sql="select count(id) from Task where taskGroupTag like ?";
			sql+=" and (terminalAddress like ? or taskName=?"
					+ " or taskStatus=? or actionNow like ? or terminalReturn like ? or additionalParam1 like ?) ";
			PreparedStatement ps=c.prepareStatement(sql);
			int index=1;
			ps.setString(index++, groupTag+"%");
			ps.setString(index++, "%"+filter.toUpperCase()+"%");
			ps.setString(index++, filter);
			ps.setString(index++, filter);
			ps.setString(index++, "%"+filter+"%");
			ps.setString(index++, "%"+filter+"%");
			ps.setString(index++, "%"+filter+"%");
			ResultSet rs=ps.executeQuery();
			long page=0;
			while(rs.next()){
				page=rs.getLong(1);
			}
			ps.close();
			rs.close();
			return page;
		}catch(SQLException e){
			e.printStackTrace();
		}
		return 0L;
	}
	
	public Map getFootCount(String groupTag){
		Map result=Maps.newHashMap();
		try{
			Connection c=getConnection();
			String sql="select taskStatus,count(taskStatus) as num from Task where taskGroupTag like ? group by taskStatus";
			PreparedStatement ps=c.prepareStatement(sql);
			ps.setString(1, groupTag+"%");
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				result.put(rs.getString(1), rs.getInt(2));
			}
			rs.close();
			ps.close();
		}catch(Exception e){
			ErrorTool.getErrorInfoFromException(e);
		}
		return result;
	}
	
	public void add(List<TaskImpl> tasks){
		try{
			Connection c=getConnection();
			String sql="insert into Task (id,terminalAddress,taskName,taskStatus,"
					+ "createTime,actionNow,terminalReturn,taskGroupTag,nextActionTime,"
					+ "counter,pfc,currentStepIndex,stepStatus,packetIndex,retryCount,priority,"
					+ "additionalParam1,additionalParam2,additionalParam3) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps=c.prepareStatement(sql);
			int cache=0;
			int index=0;
			for(TaskImpl task:tasks){
				index=1;
				cache++;
				ps.setString(index++, task.getId());
				ps.setString(index++, task.getTerminalAddress());
				ps.setString(index++, task.getTaskName());
				ps.setString(index++, task.getTaskStatus());
				ps.setTimestamp(index++, new java.sql.Timestamp(task.getCreateTime().getTime()));
				ps.setString(index++, task.getActionNow());
				ps.setString(index++, task.getTerminalReturn());
				ps.setString(index++, task.getTaskGroupTag());
				ps.setLong(index++, task.getNextActionTime());
				ps.setLong(index++, task.getCounter());
				ps.setInt(index++, task.getPfc());
				ps.setInt(index++, task.getCurrentStepIndex());
				ps.setString(index++, task.getStepStatus());
				ps.setInt(index++, task.getPacketIndex());
				ps.setInt(index++, task.getRetryCount());
				ps.setInt(index++, task.getPriority());
				ps.setString(index++, task.getAdditionalParam1());
				ps.setString(index++, task.getAdditionalParam2());
				ps.setString(index++, task.getAdditionalParam3());
				ps.addBatch();
				if(cache>50){
					ps.executeBatch();
					cache=0;
				}
			}
			ps.executeBatch();
			ps.close();
		}catch(SQLException e){
			ErrorTool.getErrorInfoFromException(e);
		}
	}	
	
	public List<TaskImpl> getTaskList(String groupTag,int page,int limit,String column,String direction,String filter){
		try{
			Connection c=getConnection();
			String sql="select id,terminalAddress,taskName,taskStatus,createTime,finishTime,actionNow"
					+ ",terminalReturn,taskGroupTag,nextActionTime,counter,pfc,currentStepIndex,"
					+ "stepStatus,packetIndex,retryCount,priority,additionalParam1,additionalParam2,additionalParam3 "
					+ "from Task where taskGroupTag like ? and (terminalAddress like ? or taskName=?"
					+ " or taskStatus=? or actionNow like ? or terminalReturn like ? or additionalParam1 like ?) ";
			if(!StringUtils.isBlank(column) && !StringUtils.isBlank(direction)){
				sql+="order by "+column+" "+direction+" ";
			}
			sql+="offset ? rows fetch next ? rows only";
			PreparedStatement ps=c.prepareStatement(sql);
			int index=1;
			ps.setString(index++, groupTag+"%");
			ps.setString(index++, "%"+filter.toUpperCase()+"%");
			ps.setString(index++, filter);
			ps.setString(index++, filter);
			ps.setString(index++, "%"+filter+"%");
			ps.setString(index++, "%"+filter+"%");
			ps.setString(index++, "%"+filter+"%");
			ps.setInt(index++, (page-1)*limit);
			ps.setInt(index++, limit);
			ResultSet rs=ps.executeQuery();
			List<TaskImpl> tasks=new ArrayList<TaskImpl>();
			while(rs.next()){
				TaskImpl task=new TaskImpl();
				task.setId(rs.getString("id"));
				task.setTerminalAddress(rs.getString("terminalAddress"));
				task.setTaskName(rs.getString("taskName"));
				task.setTaskStatus(rs.getString("taskStatus"));
				task.setCreateTime(new Date(rs.getTimestamp("createTime").getTime()));
				if(rs.getTimestamp("finishTime")!=null){
					task.setFinishTime(new Date(rs.getTimestamp("finishTime").getTime()));
				}
				task.setActionNow(rs.getString("actionNow"));
				task.setTerminalReturn(rs.getString("terminalReturn"));
				task.setTaskGroupTag(rs.getString("taskGroupTag"));
				task.setNextActionTime(rs.getLong("nextActionTime"));
				task.setCounter(rs.getInt("counter"));
				task.setPfc(rs.getInt("pfc"));
				task.setCurrentStepIndex(rs.getInt("currentStepIndex"));
				task.setStepStatus(rs.getString("stepStatus"));
				task.setPacketIndex(rs.getInt("packetIndex"));
				task.setRetryCount(rs.getInt("retryCount"));
				task.setPriority(rs.getInt("priority"));
				task.setAdditionalParam1(rs.getString("additionalParam1"));
				task.setAdditionalParam2(rs.getString("additionalParam2"));
				task.setAdditionalParam3(rs.getString("additionalParam3"));
				tasks.add(task);
			}
			ps.close();
			rs.close();
			return tasks;
		}catch(SQLException e){
			ErrorTool.getErrorInfoFromException(e);
		}
		return new ArrayList<TaskImpl>();
	}
	
	
	public TaskImpl getTaskImpl(String id){
		try{
			Connection c=getConnection();
			String sql="select id,terminalAddress,taskName,taskStatus,createTime,finishTime,actionNow"
					+ ",terminalReturn,taskGroupTag,nextActionTime,counter,pfc,currentStepIndex,"
					+ "stepStatus,packetIndex,retryCount,priority,additionalParam1,additionalParam2,additionalParam3 "
					+ "from Task where id=?";
			PreparedStatement ps=c.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs=ps.executeQuery();
			while(rs.next()){
				TaskImpl task=new TaskImpl();
				task.setId(rs.getString("id"));
				task.setTerminalAddress(rs.getString("terminalAddress"));
				task.setTaskName(rs.getString("taskName"));
				task.setTaskStatus(rs.getString("taskStatus"));
				task.setCreateTime(new Date(rs.getTimestamp("createTime").getTime()));
				if(rs.getTimestamp("finishTime")!=null){
					task.setFinishTime(new Date(rs.getTimestamp("finishTime").getTime()));
				}
				task.setActionNow(rs.getString("actionNow"));
				task.setTerminalReturn(rs.getString("terminalReturn"));
				task.setTaskGroupTag(rs.getString("taskGroupTag"));
				task.setNextActionTime(rs.getLong("nextActionTime"));
				task.setCounter(rs.getInt("counter"));
				task.setPfc(rs.getInt("pfc"));
				task.setCurrentStepIndex(rs.getInt("currentStepIndex"));
				task.setStepStatus(rs.getString("stepStatus"));
				task.setPacketIndex(rs.getInt("packetIndex"));
				task.setRetryCount(rs.getInt("retryCount"));
				task.setPriority(rs.getInt("priority"));
				task.setAdditionalParam1(rs.getString("additionalParam1"));
				task.setAdditionalParam2(rs.getString("additionalParam2"));
				task.setAdditionalParam3(rs.getString("additionalParam3"));
				return task;
			}
			ps.close();
			rs.close();
		}catch(SQLException e){
			ErrorTool.getErrorInfoFromException(e);
		}
		return null;
	}
	
	public void changeTaskStatus(String[] fromStatus,String toStatus,String id){
		try{
			Connection c=getConnection();
			String sql="update Task set taskStatus=? where id=? and taskStatus in (";
			StringBuilder sb=new StringBuilder(sql);
			for(String s:fromStatus){
				sb.append("'").append(s).append("'").append(",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append(")");
			PreparedStatement ps=c.prepareStatement(sb.toString());
			ps.setString(1, toStatus);
			ps.setString(2, id);
			ps.executeUpdate();
			ps.close();
		}catch(SQLException e){
			ErrorTool.getErrorInfoFromException(e);
		}
		
		

	}
	
	public void changeAllTaskStatus(String[] fromStatus,String toStatus,String groupTag){
		try{
			Connection c=getConnection();
			String sql="update Task set taskStatus=? where taskGroupTag like ? and taskStatus in (";
			StringBuilder sb=new StringBuilder(sql);
			for(String s:fromStatus){
				sb.append("'").append(s).append("'").append(",");
			}
			sb.deleteCharAt(sb.lastIndexOf(","));
			sb.append(")");
			PreparedStatement ps=c.prepareStatement(sb.toString());
			ps.setString(1, toStatus);
			ps.setString(2, groupTag+"%");
			ps.executeUpdate();
			ps.close();
		}catch(SQLException e){
			ErrorTool.getErrorInfoFromException(e);
		}
	}
	
	
	public void deleteAllTask(String groupTag){
		try{
			Connection c=getConnection();
			String sql="delete from Task where taskGroupTag like ?";
			PreparedStatement ps=c.prepareStatement(sql);
			ps.setString(1, groupTag+"%");
			ps.executeUpdate();
			ps.close();
		}catch(SQLException e){
			ErrorTool.getErrorInfoFromException(e);
		}
		
	}
	
	public void deleteTask(String id){
		try{
			Connection c=getConnection();
			String sql="delete from Task where id=?";
			PreparedStatement ps=c.prepareStatement(sql);
			ps.setString(1, id);
			ps.executeUpdate();
			ps.close();
		}catch(SQLException e){
			ErrorTool.getErrorInfoFromException(e);
		}
		
	}
	
	public void update(List<TaskImpl> tasks){
		try{
			Connection c=getConnection();
			String sql="update Task set terminalAddress=?,taskName=?,taskStatus=?,createTime=?,finishTime=?,actionNow=?, terminalReturn=?,"
					+ "taskGroupTag=?,nextActionTime=?, counter=?,pfc=?,currentStepIndex=?,stepStatus=?,"
					+ "packetIndex=?,retryCount=?,priority=?,additionalParam1=?,additionalParam2=?,additionalParam3=? "
					+ "where id=?";
			PreparedStatement ps=c.prepareStatement(sql);
			int cache=0;
			int index=0;
			for(TaskImpl task:tasks){
				index=1;
				cache++;
				ps.setString(index++, task.getTerminalAddress());
				ps.setString(index++, task.getTaskName());
				ps.setString(index++, task.getTaskStatus());
				if(task.getCreateTime()!=null){
					ps.setTimestamp(index++, new java.sql.Timestamp(task.getCreateTime().getTime()));
				}else{
					ps.setTimestamp(index++,null);
				}
				if(task.getFinishTime()!=null){
					ps.setTimestamp(index++, new java.sql.Timestamp(task.getFinishTime().getTime()));
				}else{
					ps.setTimestamp(index++,null);
				}
				ps.setString(index++, task.getActionNow());
				ps.setString(index++, task.getTerminalReturn());
				ps.setString(index++, task.getTaskGroupTag());
				ps.setLong(index++, task.getNextActionTime());
				ps.setLong(index++, task.getCounter());
				ps.setInt(index++, task.getPfc());
				ps.setInt(index++, task.getCurrentStepIndex());
				ps.setString(index++, task.getStepStatus());
				ps.setInt(index++, task.getPacketIndex());
				ps.setInt(index++, task.getRetryCount());
				ps.setInt(index++, task.getPriority());
				ps.setString(index++, task.getAdditionalParam1());
				ps.setString(index++, task.getAdditionalParam2());
				ps.setString(index++, task.getAdditionalParam3());
				ps.setString(index++, task.getId());
				
				ps.addBatch();
				if(cache>50){
					ps.executeBatch();
					cache=0;
				}
			}
			ps.executeBatch();
			ps.close();
		}catch(SQLException e){
			ErrorTool.getErrorInfoFromException(e);
		}
	}
	

	
	public boolean isExist(String id){
		try{
			Connection c=getConnection();
			String sql="select count(id) from Task where id=?";
			PreparedStatement ps=c.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs=ps.executeQuery();
			int num=0;
			while(rs.next()){
				num=rs.getInt(1);
			}
			ps.close();
			rs.close();
			if(num>0){
				return true;
			}
		}catch(SQLException e){
			ErrorTool.getErrorInfoFromException(e);
		}
		return false;
	}
}