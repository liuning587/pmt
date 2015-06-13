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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class FileTransferCommandView extends JPanel{
	private static final long serialVersionUID = 1L;
	private CommandSaveView commandSaveView;
	private JSeparator separator;
	private JPanel container;
	private JTextField filePathField;
	private JButton fileChooseButton;
	private JComboBox<Integer> segmentLengthComboBox;
	
	public FileTransferCommandView(){
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
		fileChooseButton=new JButton("选择");
		segmentLengthComboBox=new JComboBox<Integer>(new Integer[]{512,768,896,1024,2048});
		
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
		container.add(new JLabel("分包长度"),  getGBC(0,2,1,1));
		container.add(segmentLengthComboBox,  getGBC(1,2,1,1));
		this.add(container,                   getGBC(2,0,1,1));
	}
	
	private GridBagConstraints getGBC(int x,int y,int xn,int yn){
		Insets insets=new Insets(2,2,2,2);
		return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
	}
	
	public void setListener(){
		final FileTransferCommandController fileTransferCommandController=
				(FileTransferCommandController) SpringBeanUtil.getBean("fileTransferCommandController");
		fileTransferCommandController.setFileTransferCommandView(this);
		
		commandSaveView.getLoadButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fileTransferCommandController.loadHandler(e);
			}
        });
		
		commandSaveView.getSaveButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fileTransferCommandController.saveHandler(e);
				
			}
        });
		
		commandSaveView.getDeleteButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fileTransferCommandController.deleteHandler(e);
				
			}
        });
		
		fileChooseButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				fileTransferCommandController.fileChooseHandler(e);
			}
        });
		
		commandSaveView.getCommandList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					fileTransferCommandController.loadHandler(e);
				}
			}
		});
	}
	
	public void loadValue(){
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.GBFileTransferCommand)){
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

	public JComboBox<Integer> getSegmentLengthComboBox() {
		return segmentLengthComboBox;
	}

	public void setSegmentLengthComboBox(JComboBox<Integer> segmentLengthComboBox) {
		this.segmentLengthComboBox = segmentLengthComboBox;
	}
	
	
}
