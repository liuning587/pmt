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
public class FileUpdateCommandController {
	private FileUpdateCommandView fileUpdateCommandView;

	public FileUpdateCommandView getFileUpdateCommandView() {
		return fileUpdateCommandView;
	}

	public void setFileUpdateCommandView(FileUpdateCommandView fileUpdateCommandView) {
		this.fileUpdateCommandView = fileUpdateCommandView;
	}
	
	public void loadHandler(EventObject e){
		String commandName=
				fileUpdateCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			for(CommandConfig command:commandXmlResolver.getCommands().values()){
				if(command.getClazz().equals(CommandFactory.UpdateFileCommand)){
					if(command.getName().equals(commandName)){
						fileUpdateCommandView.getCommandSaveView().getActivateCheckBox().setSelected(command.isUse());
						fileUpdateCommandView.getCommandSaveView().getCommandNameField().setText(command.getName());
						fileUpdateCommandView.getFilePathField().setText(command.getAttr().get(ConfigConstants.FilePath));
						fileUpdateCommandView.getFileTypeComboBox()
										.setSelectedIndex(Integer.parseInt(command.getAttr().get(ConfigConstants.FileType)));
						fileUpdateCommandView.getFileVersion().setText(command.getAttr().get(ConfigConstants.Version));
						fileUpdateCommandView.getSegmentLengthComboBox()
										.setSelectedItem(Integer.parseInt(command.getAttr().get(ConfigConstants.SegmentLength))+24);
						fileUpdateCommandView.getRebootDelayTimeComboBox()
										.setSelectedItem(Integer.parseInt(command.getAttr().get(ConfigConstants.RebootWaitTime)));
						fileUpdateCommandView.getIsCompressed()
										.setSelected(Boolean.parseBoolean((command.getAttr().get(ConfigConstants.IsCompressed))));
						fileUpdateCommandView.getIsReboot()
										.setSelected(Boolean.parseBoolean((command.getAttr().get(ConfigConstants.IsReboot))));
					}
				}
			}
		}
		
	}
	
	public void saveHandler(ActionEvent e){
		String commandName=fileUpdateCommandView.getCommandSaveView().getCommandNameField().getText();
		String filePath=fileUpdateCommandView.getFilePathField().getText();
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
			if(command.getClazz().equals(CommandFactory.UpdateFileCommand)){
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
			config.setClazz(CommandFactory.UpdateFileCommand);
			config.setUse(fileUpdateCommandView.getCommandSaveView().getActivateCheckBox().isSelected());
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.FilePath, filePath);
			attr.put(ConfigConstants.Version, fileUpdateCommandView.getFileVersion().getText());
			attr.put(ConfigConstants.FileType,String.valueOf(fileUpdateCommandView.getFileTypeComboBox().getSelectedIndex()));
			attr.put(ConfigConstants.SegmentLength, String.valueOf((int)fileUpdateCommandView.getSegmentLengthComboBox().getSelectedItem()-24));
			attr.put(ConfigConstants.RebootWaitTime, String.valueOf(fileUpdateCommandView.getRebootDelayTimeComboBox().getSelectedItem()));
			attr.put(ConfigConstants.IsCompressed, String.valueOf(fileUpdateCommandView.getIsCompressed().isSelected()));
			attr.put(ConfigConstants.IsReboot, String.valueOf(fileUpdateCommandView.getIsReboot().isSelected()));
			config.setAttr(attr);
			
			if(isModify){
				commandXmlResolver.modify(config);
				JOptionPane.showMessageDialog(fileUpdateCommandView, "修改成功");
				
			}else{
				commandXmlResolver.add(config);
				fileUpdateCommandView.getCommandSaveView()
					.getCommandListModel().addElement(commandName);
				JOptionPane.showMessageDialog(fileUpdateCommandView, "保存成功");
			}
		}
	}
	
	public void deleteHandler(ActionEvent e){
		String commandName=
				fileUpdateCommandView.getCommandSaveView().getCommandList().getSelectedValue();
		if(commandName==null){
			JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "请先选中一个命令");
		}else{
			CommandXmlResolver commandXmlResolver=
					(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
			commandXmlResolver.delete(new CommandConfig(commandName, CommandFactory.UpdateFileCommand, true));
			fileUpdateCommandView.getCommandSaveView().getActivateCheckBox().setSelected(false);
			fileUpdateCommandView.getCommandSaveView().getCommandNameField().setText("");
			fileUpdateCommandView.getFilePathField().setText("");
			fileUpdateCommandView.getFileVersion().setText("");
			fileUpdateCommandView.getFileTypeComboBox().setSelectedIndex(0);
			fileUpdateCommandView.getSegmentLengthComboBox().setSelectedIndex(0);
			fileUpdateCommandView.getRebootDelayTimeComboBox().setSelectedIndex(0);
			fileUpdateCommandView.getIsCompressed().setSelected(false);
			fileUpdateCommandView.getIsReboot().setSelected(false);
			fileUpdateCommandView.getCommandSaveView().getCommandListModel().removeElement(commandName);
		}
	}
	
	public void fileChooseHandler(ActionEvent e){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("选择升级文件");
		File defaultPath=new File(
				fileUpdateCommandView.getFilePathField().getText());
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
            fileUpdateCommandView.getFilePathField().setText(selectedFile.getAbsolutePath());
        }
	}
	

	
}
