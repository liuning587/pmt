package com.sx.mmt.swingUI;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sx.mmt.internal.task.TaskConfig;
import com.sx.mmt.internal.task.TaskXmlResolver;
import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;


@Component
public class TaskConfigController {
	private TaskConfigView taskConfigView;

	public TaskConfigView getTaskConfigView() {
		return taskConfigView;
	}

	public void setTaskConfigView(TaskConfigView taskConfigView) {
		this.taskConfigView = taskConfigView;
	}
	
	public void loadHandler(EventObject e){
		String name=taskConfigView.getTaskList().getSelectedValue();
		taskConfigView.getTaskNameField().setText(name);
		if(name==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个任务");
		}else{
			TaskXmlResolver taskXmlResolver=
					(com.sx.mmt.internal.task.TaskXmlResolver) SpringBeanUtil.getBean("taskXmlResolver");
			taskConfigView.getCommandOrderListModel().clear();
			for(String command:taskXmlResolver.getTasks().get(name).getCommands()){
				taskConfigView.getCommandOrderListModel().addElement(command);
			}
			taskConfigView.getProtocolArea().setSelectedItem(taskXmlResolver
					.getTasks().get(name).getProtocolArea());
			taskConfigView.getProtocolTypeComboBox().setSelectedItem(taskXmlResolver
					.getTasks().get(name).getProtocolType());
		}
	}
	
	public void saveHandler(ActionEvent e){
		String name=taskConfigView.getTaskNameField().getText();
		if(StringUtils.isBlank(name)){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请输入任务名");
			return;
		}
		
		TaskXmlResolver taskXmlResolver=
				(com.sx.mmt.internal.task.TaskXmlResolver) SpringBeanUtil.getBean("taskXmlResolver");
		boolean isModify=false;
		for(String s:taskXmlResolver.getTasks().keySet()){
			if(s.equals(name)){
				isModify=true;
			}
		}
		List<String> commandOrder=new ArrayList<String>();
		for(int i=0;i<taskConfigView.getCommandOrderListModel().getSize();i++){
			commandOrder.add(taskConfigView.getCommandOrderListModel().getElementAt(i));
		}
		TaskConfig tc=new TaskConfig();
		tc.setName(name);
		tc.setProtocolArea(taskConfigView.getProtocolArea().getSelectedItem().toString());
		tc.setProtocolType(taskConfigView.getProtocolTypeComboBox().getSelectedItem().toString());
		tc.setCommands(commandOrder);
		if(isModify){
			taskXmlResolver.modify(name, tc);
			JOptionPane.showMessageDialog(taskConfigView, "修改成功");
		}else{
			taskXmlResolver.add(name, tc);
			taskConfigView.getTaskListModel().addElement(name);
			TaskManagerController taskManagerController=(TaskManagerController) 
					SpringBeanUtil.getBean("taskManagerController");
			taskManagerController.getTaskManagerBodyView().loadMenuChangeTaskMenu();
			JOptionPane.showMessageDialog(taskConfigView, "保存成功");
		}
		
	}
	
	public void deleteHandler(ActionEvent e){
		String name=taskConfigView.getTaskList().getSelectedValue();
		if(name==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个任务");
		}else{
			TaskXmlResolver taskXmlResolver=
					(com.sx.mmt.internal.task.TaskXmlResolver) SpringBeanUtil.getBean("taskXmlResolver");
			taskXmlResolver.delete(name);
			taskConfigView.getTaskNameField().setText("");
			taskConfigView.getTaskListModel().removeElement(name);
			taskConfigView.getCommandOrderListModel().clear();
			TaskManagerController taskManagerController=(TaskManagerController) 
					SpringBeanUtil.getBean("taskManagerController");
			taskManagerController.getTaskManagerBodyView().loadMenuChangeTaskMenu();
		}
	}
	
	public void addHandler(EventObject e){
		String selectCommand=taskConfigView.getCommandList().getSelectedValue();
		if(selectCommand==null){
			return;
		}
		taskConfigView.getCommandOrderListModel().addElement(selectCommand);
	}
	
	public void upHandler(ActionEvent e){
		Integer i=taskConfigView.getCommandOrderList().getSelectedIndex();
		if(i==null){
			return;
		}
		if(i-1<0){
			return;
		}else{
			String up=taskConfigView.getCommandOrderListModel().getElementAt(i-1);
			taskConfigView.getCommandOrderListModel().remove(i-1);
			taskConfigView.getCommandOrderListModel().add(i, up);
		}
	}
	
	public void downHandler(ActionEvent e){
		Integer i=taskConfigView.getCommandOrderList().getSelectedIndex();
		if(i==null){
			return;
		}
		if(i+1>taskConfigView.getCommandOrderListModel().getSize()-1){
			return;
		}else{
			String down=taskConfigView.getCommandOrderListModel().getElementAt(i+1);
			taskConfigView.getCommandOrderListModel().remove(i+1);
			taskConfigView.getCommandOrderListModel().add(i, down);
		}
	}
	
	public void deleteCommandHandler(ActionEvent e){
		String selectCommand=taskConfigView.getCommandOrderList().getSelectedValue();
		if(selectCommand==null){
			return;
		}
		taskConfigView.getCommandOrderListModel().removeElement(selectCommand);
	}
	

	
}
