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
public class PortShiftCommandController {
	private PortShiftCommandView portShiftCommandView;

	public PortShiftCommandView getPortShiftCommandView() {
		return portShiftCommandView;
	}

	public void setPortShiftCommandView(PortShiftCommandView portShiftCommandView) {
		this.portShiftCommandView = portShiftCommandView;
	}
	
	public void loadHandler(EventObject e){
		String commandName=
				portShiftCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			for(CommandConfig command:commandXmlResolver.getCommands().values()){
				if(command.getClazz().equals(CommandFactory.PortShiftCommand)){
					if(command.getName().equals(commandName)){
						portShiftCommandView.getCommandSaveView().getActivateCheckBox().setSelected(command.isUse());
						portShiftCommandView.getCommandSaveView().getCommandNameField().setText(command.getName());
						portShiftCommandView.getMainIPField().setText(command.getAttr().get(ConfigConstants.MainIP));
						portShiftCommandView.getMainPortField().setText(command.getAttr().get(ConfigConstants.MainPort));
						portShiftCommandView.getSubIPField().setText(command.getAttr().get(ConfigConstants.SubIP));
						portShiftCommandView.getSubPortField().setText(command.getAttr().get(ConfigConstants.SubPort));
						portShiftCommandView.getAPNField().setText(command.getAttr().get(ConfigConstants.APN));
						portShiftCommandView.getIsSetMainBySub()
								.setSelected(Boolean.parseBoolean(command.getAttr().get(ConfigConstants.IsSetMainBySub)));
						portShiftCommandView.getIsNotChangeAPN()
								.setSelected(Boolean.parseBoolean(command.getAttr().get(ConfigConstants.IsNotChangeAPN)));
					}
				}
			}
		}
	}
	
	public void saveHandler(ActionEvent e){
		String commandName=portShiftCommandView.getCommandSaveView().getCommandNameField().getText();
		if(StringUtils.isBlank(commandName)){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请输入命令名");
			return;
		}
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		boolean isModify=false;
		boolean isDuplicate=false;
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.PortShiftCommand)){
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
			config.setClazz(CommandFactory.PortShiftCommand);
			config.setUse(portShiftCommandView.getCommandSaveView().getActivateCheckBox().isSelected());
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.MainIP, portShiftCommandView.getMainIPField().getText());
			attr.put(ConfigConstants.MainPort,portShiftCommandView.getMainPortField().getText());
			attr.put(ConfigConstants.SubIP, portShiftCommandView.getSubIPField().getText());
			attr.put(ConfigConstants.SubPort, portShiftCommandView.getSubPortField().getText());
			attr.put(ConfigConstants.APN, portShiftCommandView.getAPNField().getText());
			attr.put(ConfigConstants.IsSetMainBySub, String.valueOf(portShiftCommandView.getIsSetMainBySub().isSelected()));
			attr.put(ConfigConstants.IsNotChangeAPN, String.valueOf(portShiftCommandView.getIsNotChangeAPN().isSelected()));
			config.setAttr(attr);
			
			if(isModify){
				commandXmlResolver.modify(config);
				JOptionPane.showMessageDialog(portShiftCommandView, "修改成功");
				
			}else{
				commandXmlResolver.add(config);
				portShiftCommandView.getCommandSaveView()
					.getCommandListModel().addElement(commandName);
				JOptionPane.showMessageDialog(portShiftCommandView, "保存成功");
			}
		}
	}
	
	public void deleteHandler(ActionEvent e){
		String commandName=
				portShiftCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			commandXmlResolver.delete(new CommandConfig(commandName, CommandFactory.PortShiftCommand, true));
			portShiftCommandView.getCommandSaveView().getActivateCheckBox().setSelected(false);
			portShiftCommandView.getCommandSaveView().getCommandNameField().setText("");
			portShiftCommandView.getMainIPField().setText("");
			portShiftCommandView.getMainPortField().setText("");
			portShiftCommandView.getSubIPField().setText("");
			portShiftCommandView.getSubPortField().setText("");
			portShiftCommandView.getAPNField().setText("");
			portShiftCommandView.getIsSetMainBySub().setSelected(false);
			portShiftCommandView.getIsNotChangeAPN().setSelected(true);
			portShiftCommandView.getCommandSaveView().getCommandListModel().removeElement(commandName);
		}
	}
	
}
