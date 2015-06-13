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
public class TerminalParameterReadCommandController {
	private TerminalParameterReadCommandView terminalParameterReadCommandView;

	public TerminalParameterReadCommandView getTerminalParameterReadCommandView() {
		return terminalParameterReadCommandView;
	}

	public void setTerminalParameterReadCommandView(
			TerminalParameterReadCommandView terminalParameterReadCommandView) {
		this.terminalParameterReadCommandView = terminalParameterReadCommandView;
	}
	
	public void loadHandler(EventObject e){
		String commandName=
				terminalParameterReadCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			for(CommandConfig command:commandXmlResolver.getCommands().values()){
				if(command.getClazz().equals(CommandFactory.TerminalParameterReadCommand)){
					if(command.getName().equals(commandName)){
						terminalParameterReadCommandView.getCommandSaveView().getActivateCheckBox().setSelected(command.isUse());
						terminalParameterReadCommandView.getCommandSaveView().getCommandNameField().setText(command.getName());
						terminalParameterReadCommandView.getReadNumberField()
							.setValue(Integer.parseInt(command.getAttr().get(ConfigConstants.QueryNumber)));
						terminalParameterReadCommandView.getPortShiftField()
							.setValue(Integer.parseInt(command.getAttr().get(ConfigConstants.PortShift)));
					}
				}
			}
		}
		
	}
	
	public void saveHandler(ActionEvent e){
		String commandName=terminalParameterReadCommandView.getCommandSaveView().getCommandNameField().getText();
		if(StringUtils.isBlank(commandName)){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请输入命令名");
			return;
		}
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		boolean isModify=false;
		boolean isDuplicate=false;
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.TerminalParameterReadCommand)){
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
			config.setClazz(CommandFactory.TerminalParameterReadCommand);
			config.setUse(terminalParameterReadCommandView.getCommandSaveView().getActivateCheckBox().isSelected());
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.QueryNumber, terminalParameterReadCommandView.getReadNumberField().getValue().toString());
			attr.put(ConfigConstants.PortShift, terminalParameterReadCommandView.getPortShiftField().getValue().toString());
			config.setAttr(attr);
			
			if(isModify){
				commandXmlResolver.modify(config);
				JOptionPane.showMessageDialog(terminalParameterReadCommandView, "修改成功");
				
			}else{
				commandXmlResolver.add(config);
				terminalParameterReadCommandView.getCommandSaveView()
					.getCommandListModel().addElement(commandName);
				JOptionPane.showMessageDialog(terminalParameterReadCommandView, "保存成功");
			}
		}
	}
	
	public void deleteHandler(ActionEvent e){
		String commandName=
				terminalParameterReadCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			commandXmlResolver.delete(new CommandConfig(commandName, CommandFactory.UpdateFileCommand, true));
			terminalParameterReadCommandView.getCommandSaveView().getActivateCheckBox().setSelected(false);
			terminalParameterReadCommandView.getCommandSaveView().getCommandNameField().setText("");
			terminalParameterReadCommandView.getReadNumberField().setText("");
			terminalParameterReadCommandView.getPortShiftField().setText("");
			terminalParameterReadCommandView.getCommandSaveView().getCommandListModel().removeElement(commandName);
		}
	}
	
	
	
}
