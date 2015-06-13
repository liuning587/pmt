package com.sx.mmt.swingUI;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;


@Component
public class FileTransferCommandController {	
	private FileTransferCommandView fileTransferCommandView;

	public FileTransferCommandView getFileTransferCommandView() {
		return fileTransferCommandView;
	}

	public void setFileTransferCommandView(
			FileTransferCommandView fileTransferCommandView) {
		this.fileTransferCommandView = fileTransferCommandView;
	}
	
	public void loadHandler(EventObject e){
		String commandName=
				fileTransferCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			for(CommandConfig command:commandXmlResolver.getCommands().values()){
				if(command.getClazz().equals(CommandFactory.GBFileTransferCommand)){
					if(command.getName().equals(commandName)){
						fileTransferCommandView.getCommandSaveView().getActivateCheckBox().setSelected(command.isUse());
						fileTransferCommandView.getCommandSaveView().getCommandNameField().setText(command.getName());
						fileTransferCommandView.getFilePathField().setText(command.getAttr().get(ConfigConstants.FilePath));
						fileTransferCommandView.getSegmentLengthComboBox()
										.setSelectedItem(Integer.parseInt(command.getAttr().get(ConfigConstants.SegmentLength))+53);
					}
				}
			}
		}
	}
	
	public void saveHandler(ActionEvent e){
		String commandName=fileTransferCommandView.getCommandSaveView().getCommandNameField().getText();
		String filePath=fileTransferCommandView.getFilePathField().getText();
		if(StringUtils.isBlank(commandName)){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请输入命令名");
			return;
		}
		if(StringUtils.isBlank(filePath)){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请选择一个文件");
			return;
		}
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		boolean isModify=false;
		boolean isDuplicate=false;
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.GBFileTransferCommand)){
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
			config.setClazz(CommandFactory.GBFileTransferCommand);
			config.setUse(fileTransferCommandView.getCommandSaveView().getActivateCheckBox().isSelected());
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.FilePath, filePath);
			attr.put(ConfigConstants.Version, "00");
			attr.put(ConfigConstants.FileType,"00");
			attr.put(ConfigConstants.SegmentLength, String.valueOf((int)fileTransferCommandView.getSegmentLengthComboBox().getSelectedItem()-53));
			attr.put(ConfigConstants.RebootWaitTime, "0");
			attr.put(ConfigConstants.IsCompressed, "false");
			attr.put(ConfigConstants.IsReboot, "false");
			config.setAttr(attr);
			
			if(isModify){
				commandXmlResolver.modify(config);
				JOptionPane.showMessageDialog(fileTransferCommandView, "修改成功");
				
			}else{
				commandXmlResolver.add(config);
				fileTransferCommandView.getCommandSaveView()
					.getCommandListModel().addElement(commandName);
				JOptionPane.showMessageDialog(fileTransferCommandView, "保存成功");
			}
		}
	}
	
	public void deleteHandler(ActionEvent e){
		String commandName=
				fileTransferCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			commandXmlResolver.delete(new CommandConfig(commandName, CommandFactory.GBFileTransferCommand, true));
			fileTransferCommandView.getCommandSaveView().getActivateCheckBox().setSelected(false);
			fileTransferCommandView.getCommandSaveView().getCommandNameField().setText("");
			fileTransferCommandView.getFilePathField().setText("");
			fileTransferCommandView.getSegmentLengthComboBox().setSelectedIndex(0);
			fileTransferCommandView.getCommandSaveView().getCommandListModel().removeElement(commandName);
		}
	}
	
	public void fileChooseHandler(ActionEvent e){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("选择升级文件");
		File defaultPath=new File(
				fileTransferCommandView.getFilePathField().getText());
		if(defaultPath.isFile()){
			fileChooser.setCurrentDirectory(defaultPath);
		}
		fileChooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file) {
				if(file.isDirectory() 
						|| file.getName().toLowerCase().lastIndexOf(".bin")>0
						|| file.getName().toLowerCase().lastIndexOf(".out")>0){
					return true;
				}else{
					return false;
				}
			}
			@Override
			public String getDescription() {
				return ".bin;.out";
			}	
		});
		int i = fileChooser.showOpenDialog((java.awt.Component) e.getSource());  
        if(i==JFileChooser.APPROVE_OPTION)  
        {
            File selectedFile = fileChooser.getSelectedFile();  
            fileTransferCommandView.getFilePathField().setText(selectedFile.getAbsolutePath());
        }
	}
	
	
}
