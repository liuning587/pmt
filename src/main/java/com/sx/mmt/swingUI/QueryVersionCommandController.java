package com.sx.mmt.swingUI;

import java.awt.event.ActionEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;


@Component
public class QueryVersionCommandController {
	
	private QueryVersionCommandView queryVersionCommandView;
	
	public QueryVersionCommandView getQueryVersionCommandView() {
		return queryVersionCommandView;
	}

	public void setQueryVersionCommandView(
			QueryVersionCommandView queryVersionCommandView) {
		this.queryVersionCommandView = queryVersionCommandView;
	}

	public void loadHandler(EventObject e){
		String commandName=
				queryVersionCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			for(CommandConfig command:commandXmlResolver.getCommands().values()){
				if(command.getClazz().equals(CommandFactory.QueryVersionCommand)){
					if(command.getName().equals(commandName)){
						queryVersionCommandView.getCommandSaveView().getActivateCheckBox().setSelected(command.isUse());
						queryVersionCommandView.getCommandSaveView().getCommandNameField().setText(command.getName());
						queryVersionCommandView.getVersionTypeComboBox()
								.setSelectedItem(command.getAttr().get(ConfigConstants.VersionType));
						queryVersionCommandView.getVersionListField()
								.setText(command.getAttr().get(ConfigConstants.VersionList));
						queryVersionCommandView.getDateFromField()
								.setText(command.getAttr().get(ConfigConstants.DateFrom));
						queryVersionCommandView.getDateToField()
								.setText(command.getAttr().get(ConfigConstants.DateTo));
						queryVersionCommandView.getInExecuteComboBox()
								.setSelectedItem(String.valueOf(command.getAttr().get(ConfigConstants.InExecute)));
						queryVersionCommandView.getOutExcuteComboBox()
								.setSelectedItem(String.valueOf(command.getAttr().get(ConfigConstants.OutExecute)));
						queryVersionCommandView.getUseListCheckBox()
								.setSelected(Boolean.parseBoolean(command.getAttr().get(ConfigConstants.UseList)));
						queryVersionCommandView.getUseDateCheckBox()
								.setSelected(Boolean.parseBoolean(command.getAttr().get(ConfigConstants.UseDate)));
					}
				}
			}
		}
	}
	
	public void saveHandler(ActionEvent e){
		String commandName=queryVersionCommandView.getCommandSaveView().getCommandNameField().getText();
		if(StringUtils.isBlank(commandName)){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请输入命令名");
			return;
		}
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		boolean isModify=false;
		boolean isDuplicate=false;
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.QueryVersionCommand)){
				if(command.getName().equals(commandName)){
					isModify=true;
				}
			}else{
				if(command.getName().equals(commandName)){
					isDuplicate=true;
					JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "命令名重名，请重新输入");
					return;
				}
			}
		}
		
		if(!isDuplicate){
			CommandConfig config=new CommandConfig();
			config.setName(commandName);
			config.setClazz(CommandFactory.QueryVersionCommand);
			config.setUse(queryVersionCommandView.getCommandSaveView().getActivateCheckBox().isSelected());
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.VersionType,queryVersionCommandView.getVersionTypeComboBox().getSelectedItem().toString());
			attr.put(ConfigConstants.VersionList,queryVersionCommandView.getVersionListField().getText());
			attr.put(ConfigConstants.DateFrom,queryVersionCommandView.getDateFromField().getText());
			attr.put(ConfigConstants.DateTo,queryVersionCommandView.getDateToField().getText());
			attr.put(ConfigConstants.InExecute,String.valueOf(queryVersionCommandView.getInExecuteComboBox().getSelectedItem()));
			attr.put(ConfigConstants.OutExecute,String.valueOf(queryVersionCommandView.getOutExcuteComboBox().getSelectedItem()));
			attr.put(ConfigConstants.UseList,String.valueOf(queryVersionCommandView.getUseListCheckBox().isSelected()));
			attr.put(ConfigConstants.UseDate,String.valueOf(queryVersionCommandView.getUseDateCheckBox().isSelected()));
			config.setAttr(attr);
			if(isModify){
				commandXmlResolver.modify(config);
				JOptionPane.showMessageDialog(queryVersionCommandView, "修改成功");
				
			}else{
				commandXmlResolver.add(config);
				queryVersionCommandView.getCommandSaveView()
					.getCommandListModel().addElement(commandName);
				JOptionPane.showMessageDialog(queryVersionCommandView, "保存成功");
			}
		}
	}
	
	public void deleteHandler(ActionEvent e){
		String commandName=
				queryVersionCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			commandXmlResolver.delete(new CommandConfig(commandName, CommandFactory.QueryVersionCommand, true));
			
			queryVersionCommandView.getCommandSaveView().getActivateCheckBox().setSelected(false);
			queryVersionCommandView.getCommandSaveView().getCommandNameField().setText("");
			queryVersionCommandView.getVersionTypeComboBox().setSelectedIndex(0);
			queryVersionCommandView.getVersionListField().setText("");
			queryVersionCommandView.getVersionListField().setEditable(true);
			queryVersionCommandView.getDateFromField().setText("");
			queryVersionCommandView.getDateFromField().setEditable(true);
			queryVersionCommandView.getDateToField().setText("");
			queryVersionCommandView.getDateToField().setEditable(true);
			queryVersionCommandView.getInExecuteComboBox().setSelectedItem(0);
			queryVersionCommandView.getOutExcuteComboBox().setSelectedItem(0);
			queryVersionCommandView.getUseListCheckBox().setSelected(true);
			queryVersionCommandView.getUseDateCheckBox().setSelected(true);
			queryVersionCommandView.getCommandSaveView().getCommandListModel().removeElement(commandName);
			
		}
	}
}
