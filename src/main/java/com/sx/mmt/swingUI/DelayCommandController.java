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
public class DelayCommandController {
	private DelayCommandView delayCommandView;

	public DelayCommandView getDelayCommandView() {
		return delayCommandView;
	}

	public void setDelayCommandView(DelayCommandView delayCommandView) {
		this.delayCommandView = delayCommandView;
	}
	
	public void loadHandler(EventObject e){
		String commandName=
				delayCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			for(CommandConfig command:commandXmlResolver.getCommands().values()){
				if(command.getClazz().equals(CommandFactory.DelayCommand)){
					if(command.getName().equals(commandName)){
						delayCommandView.getCommandSaveView().getActivateCheckBox().setSelected(command.isUse());
						delayCommandView.getCommandSaveView().getCommandNameField().setText(command.getName());
						delayCommandView.getDelayTimeField().setText(command.getAttr().get(ConfigConstants.DelayTime));
					}
				}
			}
		}
	}
	
	public void saveHandler(ActionEvent e){
		String commandName=delayCommandView.getCommandSaveView().getCommandNameField().getText();
		if(StringUtils.isBlank(commandName)){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请输入命令名");
			return;
		}
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		boolean isModify=false;
		boolean isDuplicate=false;
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.DelayCommand)){
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
			config.setClazz(CommandFactory.DelayCommand);
			config.setUse(delayCommandView.getCommandSaveView().getActivateCheckBox().isSelected());
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.DelayTime, delayCommandView.getDelayTimeField().getText());
			config.setAttr(attr);
			
			if(isModify){
				commandXmlResolver.modify(config);
				JOptionPane.showMessageDialog(delayCommandView, "修改成功");
				
			}else{
				commandXmlResolver.add(config);
				delayCommandView.getCommandSaveView()
						.getCommandListModel().addElement(commandName);
				JOptionPane.showMessageDialog(delayCommandView, "保存成功");
			}
		}
	}
	
	public void deleteHandler(ActionEvent e){
		String commandName=
				delayCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			commandXmlResolver.delete(new CommandConfig(commandName, CommandFactory.DelayCommand, true));
			delayCommandView.getCommandSaveView().getActivateCheckBox().setSelected(false);
			delayCommandView.getCommandSaveView().getCommandNameField().setText("");
			delayCommandView.getDelayTimeField().setText("");
			delayCommandView.getCommandSaveView().getCommandListModel().removeElement(commandName);
		}
	}
}
