package com.sx.mmt.swingUI;

import java.awt.event.ActionEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;


@Component
public class CustomMessageCommandController {
	private CustomMessageCommandView customMessageCommandView;

	public void setCustomMessageCommandView(
			CustomMessageCommandView customMessageCommandView) {
		this.customMessageCommandView = customMessageCommandView;
	}

	public void loadHandler(EventObject e){
		String commandName=
				customMessageCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			for(CommandConfig command:commandXmlResolver.getCommands().values()){
				if(command.getClazz().equals(CommandFactory.CustomMessageCommand)){
					if(command.getName().equals(commandName)){
						customMessageCommandView.getCommandSaveView().getActivateCheckBox().setSelected(command.isUse());
						customMessageCommandView.getCommandSaveView().getCommandNameField().setText(command.getName());
						customMessageCommandView.getAFNField().setText(command.getAttr().get(ConfigConstants.AFN));
						customMessageCommandView.getFnField().setText(command.getAttr().get(ConfigConstants.FN));
						customMessageCommandView.getIsUsePw()
								.setSelected(Boolean.parseBoolean(command.getAttr().get(ConfigConstants.IsUsePw)));
						customMessageCommandView.getDataBodyArea()
								.setText(command.getAttr().get(ConfigConstants.DataBody));
					}
				}
			}
		}
	}
	
	public void saveHandler(ActionEvent e){
		String commandName=customMessageCommandView.getCommandSaveView().getCommandNameField().getText();
		if(StringUtils.isBlank(commandName)){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请输入命令名");
			return;
		}
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		boolean isModify=false;
		boolean isDuplicate=false;
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.CustomMessageCommand)){
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
			config.setClazz(CommandFactory.CustomMessageCommand);
			config.setUse(customMessageCommandView.getCommandSaveView().getActivateCheckBox().isSelected());
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.AFN,customMessageCommandView.getAFNField().getText());
			attr.put(ConfigConstants.FN,customMessageCommandView.getFnField().getText());
			attr.put(ConfigConstants.IsUsePw,String.valueOf(customMessageCommandView.getIsUsePw().isSelected()));
			attr.put(ConfigConstants.DataBody,customMessageCommandView.getDataBodyArea().getText());
			config.setAttr(attr);
			if(isModify){
				commandXmlResolver.modify(config);
				JOptionPane.showMessageDialog(customMessageCommandView, "修改成功");
				
			}else{
				commandXmlResolver.add(config);
				customMessageCommandView.getCommandSaveView()
					.getCommandListModel().addElement(commandName);
				JOptionPane.showMessageDialog(customMessageCommandView, "保存成功");
			}
		}
	}
	
	public void deleteHandler(ActionEvent e){
		String commandName=
				customMessageCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			commandXmlResolver.delete(new CommandConfig(commandName, CommandFactory.CustomMessageCommand, true));
			
			customMessageCommandView.getCommandSaveView().getActivateCheckBox().setSelected(false);
			customMessageCommandView.getCommandSaveView().getCommandNameField().setText("");
			customMessageCommandView.getAFNField().setText("");
			customMessageCommandView.getFnField().setText("");
			customMessageCommandView.getIsUsePw().setSelected(false);
			customMessageCommandView.getDataBodyArea().setText("");
			customMessageCommandView.getCommandSaveView().getCommandListModel().removeElement(commandName);
		}
	}
	
	
}
