package com.sx.mmt.swingUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class FileUpdateCommandView extends JPanel{

	private static final long serialVersionUID = 1L;
	private CommandSaveView commandSaveView;
	private JSeparator separator;
	private JPanel container;
	private JTextField filePathField;
	private JButton fileChooseButton;
	private JComboBox<String> fileTypeComboBox;
	private JTextField fileVersion;
	private JComboBox<Integer> segmentLengthComboBox;
	private JComboBox<Integer> rebootDelayTimeComboBox;
	private JCheckBox isCompressed;
	private JCheckBox isReboot;
	
	public FileUpdateCommandView(){
		ini();
		setAttribute();
		setListener();
		loadValue();
	}
	
	public void ini(){
		commandSaveView=new CommandSaveView();
		separator=new JSeparator();
		container=new JPanel();
		filePathField=new JTextField();
		fileVersion=new JTextField();
		fileChooseButton=new JButton("选择");
		fileTypeComboBox=new JComboBox<String>(new String[]{
				"0-负控终端主文件",
				"1-配变终端主文件",
				"2-内核文件",
				"3-负控终端参数文件",
				"4-配变终端参数文件",
				"5-字库文件asc8",
				"6-字库文件asc12",
				"7-字库文件asc16",
				"8-字库文件hzk12",
				"9-字库文件hzk16"});
		segmentLengthComboBox=new JComboBox<Integer>(new Integer[]{512,768,896,1024,2048});
		rebootDelayTimeComboBox=new JComboBox<Integer>(new Integer[]{5,10,20,30,60,120,240});
		isCompressed=new JCheckBox("启用压缩");
		isReboot=new JCheckBox("升级完成后重启");
	}
	
	public void setAttribute(){
		Insets insets=new Insets(2,2,2,2);
		GridBagLayout thisLayout=new GridBagLayout();
		thisLayout.columnWidths=new int[]{350,40,340};
		thisLayout.rowHeights=new int[]{600};
		this.setLayout(thisLayout);
		
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.add(commandSaveView,
				new GridBagConstraints(0,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0));
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBackground(Color.black);
		separator.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.add(separator,
				new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.BOTH,insets,0,0));
		container.setPreferredSize(new Dimension(360, 500));
		GridBagLayout layout=new GridBagLayout();
		layout.columnWidths=new int[]{100,140,60,20};
		layout.rowHeights=new int[]{38,38,38,38,38,38,38,38,38,38};
		container.setLayout(layout);
		filePathField.setEditable(false);
		container.add(new JLabel("文件路径"),  getGBC(0,1,1,1));
		container.add(filePathField,          getGBC(1,1,1,1));
		container.add(fileChooseButton,       getGBC(2,1,1,1));
		container.add(new JLabel("文件类型"),  getGBC(0,2,1,1));
		container.add(fileTypeComboBox,       getGBC(1,2,1,1));
		container.add(new JLabel("文件版本"),  getGBC(0,3,1,1));
		container.add(fileVersion,            getGBC(1,3,1,1));
		container.add(new JLabel("分包长度"),  getGBC(0,4,1,1));
		container.add(segmentLengthComboBox,  getGBC(1,4,1,1));
		container.add(new JLabel("重启延迟时间(秒)"),getGBC(0,5,1,1));
		container.add(rebootDelayTimeComboBox,getGBC(1,5,1,1));
		container.add(isCompressed,           getGBC(1,6,1,1));
		container.add(isReboot,               getGBC(1,7,1,1));
		this.add(container,                   getGBC(2,0,1,1));
		for(java.awt.Component component:container.getComponents()){
			component.setFont(ViewConstants.TextFont);
		}
		
	}
	
	private GridBagConstraints getGBC(int x,int y,int xn,int yn){
		Insets insets=new Insets(2,2,2,2);
		return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
	}
	
	public void setListener(){
		final FileUpdateCommandController fileUpdateCommandController=
				(FileUpdateCommandController) SpringBeanUtil.getBean("fileUpdateCommandController");
		fileUpdateCommandController.setFileUpdateCommandView(this);
		
		commandSaveView.getLoadButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fileUpdateCommandController.loadHandler(e);
			}
        });
		
		commandSaveView.getSaveButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fileUpdateCommandController.saveHandler(e);
				
			}
        });
		
		commandSaveView.getDeleteButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fileUpdateCommandController.deleteHandler(e);
				
			}
        });
		
		fileChooseButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fileUpdateCommandController.fileChooseHandler(e);
			}
        });
		
		commandSaveView.getCommandList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					fileUpdateCommandController.loadHandler(e);
				}
			}
		});
	}
	
	public void loadValue(){
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.UpdateFileCommand)){
				commandSaveView.getCommandListModel().addElement(command.getName());
			}
		}
	}

	public CommandSaveView getCommandSaveView() {
		return commandSaveView;
	}

	public void setCommandSaveView(CommandSaveView commandSaveView) {
		this.commandSaveView = commandSaveView;
	}

	public JTextField getFilePathField() {
		return filePathField;
	}

	public void setFilePathField(JTextField filePathField) {
		this.filePathField = filePathField;
	}

	public JButton getFileChooseButton() {
		return fileChooseButton;
	}

	public void setFileChooseButton(JButton fileChooseButton) {
		this.fileChooseButton = fileChooseButton;
	}

	public JComboBox<String> getFileTypeComboBox() {
		return fileTypeComboBox;
	}

	public void setFileTypeComboBox(JComboBox<String> fileTypeComboBox) {
		this.fileTypeComboBox = fileTypeComboBox;
	}

	public JComboBox<Integer> getSegmentLengthComboBox() {
		return segmentLengthComboBox;
	}

	public void setSegmentLengthComboBox(JComboBox<Integer> segmentLengthComboBox) {
		this.segmentLengthComboBox = segmentLengthComboBox;
	}

	public JComboBox<Integer> getRebootDelayTimeComboBox() {
		return rebootDelayTimeComboBox;
	}

	public void setRebootDelayTimeComboBox(
			JComboBox<Integer> rebootDelayTimeComboBox) {
		this.rebootDelayTimeComboBox = rebootDelayTimeComboBox;
	}

	public JCheckBox getIsCompressed() {
		return isCompressed;
	}

	public void setIsCompressed(JCheckBox isCompressed) {
		this.isCompressed = isCompressed;
	}

	public JCheckBox getIsReboot() {
		return isReboot;
	}

	public void setIsReboot(JCheckBox isReboot) {
		this.isReboot = isReboot;
	}

	public JTextField getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(JTextField fileVersion) {
		this.fileVersion = fileVersion;
	}
	
	
	
}
