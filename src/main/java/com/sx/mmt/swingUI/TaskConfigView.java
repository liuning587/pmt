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

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.protocol.Head;
import com.sx.mmt.internal.task.TaskXmlResolver;
import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class TaskConfigView extends JPanel{

	private static final long serialVersionUID = 1L;
	private JPanel leftContainer;
	private JPanel rightContainer;
	private JSeparator separator;
	
	private JTextField taskNameField;
	private JList<String> taskList;
	private JButton loadButton;
	private JButton saveButton;
	private JButton deleteButton;
	
	private JComboBox<String> protocolArea;
	private JComboBox<String> protocolTypeComboBox;
	private JList<String> commandList;
	private JList<String> commandOrderList;
	private JButton addButton;
	private JButton upButton;
	private JButton downButton;
	private JButton deleteCommandButton;
	private DefaultListModel<String> taskListModel;
	private DefaultListModel<String> commandListModel;
	private DefaultListModel <String> commandOrderListModel;
	
	public TaskConfigView(){
		ini();
		setAttribute();
		setListener();
		loadValue();
	}
	

	public void ini(){
		separator=new JSeparator();
		taskNameField=new JTextField();
		taskListModel=new DefaultListModel<String>();
		taskList=new JList<String>(taskListModel);
		loadButton=new JButton("载入");
		saveButton=new JButton("保存");
		deleteButton=new JButton("删除");
		leftContainer=new JPanel();
		rightContainer=new JPanel();
		protocolArea=new JComboBox<String>(new String[]{
				ViewConstants.GuoBiao,
				ViewConstants.GuangXi,
				ViewConstants.ShangXi,
				ViewConstants.JiLing
		});
		protocolTypeComboBox=new JComboBox<String>(new String[]{Head.PROTOCOL_GB13,Head.PROTOCOL_GB09,Head.PROTOCOL_GB05});
		commandListModel=new DefaultListModel<String>();
		commandList=new JList<String>(commandListModel);
		commandOrderListModel=new DefaultListModel<String>();
		commandOrderList=new JList<String>(commandOrderListModel);
		addButton=new JButton("添加");
		upButton=new JButton("上移");
		downButton=new JButton("下移");
		deleteCommandButton=new JButton("删除");	
	}
	
	public void setAttribute(){
		leftContainer.setMaximumSize(new Dimension(800, 600));
		leftContainer.setPreferredSize(new Dimension(300, 500));
		GridBagLayout leftContainerlayout=new GridBagLayout();
		leftContainerlayout.columnWidths=new int[]{20,80,150,20};
		leftContainerlayout.rowHeights=new int[]{38,38,38,38,38,38,38,38,38,38};
		leftContainer.setLayout(leftContainerlayout);
		JScrollPane taskListScrollPane=new JScrollPane();
		taskListScrollPane.setPreferredSize(new Dimension(150, 180));
		taskListScrollPane.setViewportView(taskList);
		
		leftContainer.add(new JLabel("任务名称"),getGBC(1,1,1,1));		
		leftContainer.add(taskNameField,        getGBC(2,1,1,1));		
		leftContainer.add(loadButton,           getGBC(1,2,1,1));		
		leftContainer.add(saveButton,           getGBC(1,4,1,1));	
		leftContainer.add(deleteButton,         getGBC(1,6,1,1));	
		leftContainer.add(taskListScrollPane,   getGBC(2,2,1,5));
		
		for(java.awt.Component component:leftContainer.getComponents()){
			component.setFont(ViewConstants.TextFont);
		}
		taskList.setFont(ViewConstants.TextFont);
		
		GridBagLayout thisLayout=new GridBagLayout();
		thisLayout.columnWidths=new int[]{300,40,390};
		thisLayout.rowHeights=new int[]{600};
		this.setLayout(thisLayout);
		
		this.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.add(leftContainer,getGBC(0,0,1,1));
				
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBackground(Color.black);
		separator.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.add(separator,
				new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST
						,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		rightContainer.setPreferredSize(new Dimension(390, 500));
		GridBagLayout rightContainerlayout=new GridBagLayout();
		rightContainerlayout.columnWidths=new int[]{160,50,150,2};
		rightContainerlayout.rowHeights=new int[]{38,38,70,70,70,70,70};
		rightContainer.setLayout(rightContainerlayout);
		JScrollPane commandListScrollPane=new JScrollPane();
		commandListScrollPane.setPreferredSize(new Dimension(160, 270));
		commandListScrollPane.setViewportView(commandList);
		JScrollPane commandOrderListScrollPane=new JScrollPane();
		commandOrderListScrollPane.setPreferredSize(new Dimension(140, 270));
		commandOrderListScrollPane.setViewportView(commandOrderList);
		rightContainer.add(new JLabel("区域协议"),  getGBC(0,0,1,1));
		rightContainer.add(protocolArea,           getGBC(1,0,2,1));
		rightContainer.add(new JLabel("规约类型"),  getGBC(0,1,1,1));
		rightContainer.add(protocolTypeComboBox,  getGBC(1,1,2,1));
		
		rightContainer.add(new JLabel("命令列表"), getGBC(0,2,1,1));
		rightContainer.add(new JLabel("任务命令执行顺序"), getGBC(2,2,1,1));
		rightContainer.add(addButton,           getGBC(1,3,1,1));
		rightContainer.add(upButton,            getGBC(1,4,1,1));
		rightContainer.add(downButton,          getGBC(1,5,1,1));
		rightContainer.add(deleteCommandButton,  getGBC(1,6,1,1));
		rightContainer.add(commandListScrollPane,getGBC(0,3,1,5));
		rightContainer.add(commandOrderListScrollPane,getGBC(2,3,1,5));
		this.add(rightContainer,getGBC(2,0,1,1));
		
		for(java.awt.Component component:rightContainer.getComponents()){
			component.setFont(ViewConstants.TextFont);
		}
		commandList.setFont(ViewConstants.TextFont);
		commandOrderList.setFont(ViewConstants.TextFont);
		
		taskList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		commandList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		commandOrderList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	private GridBagConstraints getGBC(int x,int y,int xn,int yn){
		Insets insets=new Insets(2,2,2,2);
		return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
	}
	
	public void setListener() {
		final TaskConfigController taskConfigController=
				(TaskConfigController)SpringBeanUtil.getBean("taskConfigController");
		taskConfigController.setTaskConfigView(this);
		
		loadButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskConfigController.loadHandler(e);
			}
        });
		
		saveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskConfigController.saveHandler(e);
			}
        });
		
		deleteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskConfigController.deleteHandler(e);
			}
        });
		
		addButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskConfigController.addHandler(e);
			}
        });
		upButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskConfigController.upHandler(e);
			}
        });
		downButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskConfigController.downHandler(e);
			}
        });
		deleteCommandButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskConfigController.deleteCommandHandler(e);
			}
        });
	
		taskList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					taskConfigController.loadHandler(e);
				}
			}
		});
		
		commandList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					taskConfigController.addHandler(e);
				}
			}
		});
		
	}

	
	public void loadValue(){
		TaskXmlResolver taskXmlResolver=
				(com.sx.mmt.internal.task.TaskXmlResolver) SpringBeanUtil.getBean("taskXmlResolver");
		for(String name:taskXmlResolver.getTasks().keySet()){
			taskListModel.addElement(name);
		}
		refreshValue();
	}
	
	public void refreshValue(){
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		commandListModel.clear();
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.isUse()){
				commandListModel.addElement(command.getName());
			}
		}
	}

	public JTextField getTaskNameField() {
		return taskNameField;
	}


	public void setTaskNameField(JTextField taskNameField) {
		this.taskNameField = taskNameField;
	}


	public JList<String> getTaskList() {
		return taskList;
	}


	public void setTaskList(JList<String> taskList) {
		this.taskList = taskList;
	}


	public JButton getLoadButton() {
		return loadButton;
	}


	public void setLoadButton(JButton loadButton) {
		this.loadButton = loadButton;
	}


	public JButton getSaveButton() {
		return saveButton;
	}


	public void setSaveButton(JButton saveButton) {
		this.saveButton = saveButton;
	}


	public JButton getDeleteButton() {
		return deleteButton;
	}


	public void setDeleteButton(JButton deleteButton) {
		this.deleteButton = deleteButton;
	}


	public JComboBox<String> getProtocolTypeComboBox() {
		return protocolTypeComboBox;
	}


	public void setProtocolTypeComboBox(JComboBox<String> protocolTypeComboBox) {
		this.protocolTypeComboBox = protocolTypeComboBox;
	}


	public JList<String> getCommandList() {
		return commandList;
	}


	public void setCommandList(JList<String> commandList) {
		this.commandList = commandList;
	}


	public JList<String> getCommandOrderList() {
		return commandOrderList;
	}


	public void setCommandOrderList(JList<String> commandOrderList) {
		this.commandOrderList = commandOrderList;
	}


	public JButton getAddButton() {
		return addButton;
	}


	public void setAddButton(JButton addButton) {
		this.addButton = addButton;
	}


	public JButton getUpButton() {
		return upButton;
	}


	public void setUpButton(JButton upButton) {
		this.upButton = upButton;
	}


	public JButton getDownButton() {
		return downButton;
	}


	public void setDownButton(JButton downButton) {
		this.downButton = downButton;
	}


	public JButton getDeleteCommandButton() {
		return deleteCommandButton;
	}


	public void setDeleteCommandButton(JButton deleteCommandButton) {
		this.deleteCommandButton = deleteCommandButton;
	}

	public DefaultListModel<String> getTaskListModel() {
		return taskListModel;
	}


	public void setTaskListModel(DefaultListModel<String> taskListModel) {
		this.taskListModel = taskListModel;
	}


	public DefaultListModel<String> getCommandListModel() {
		return commandListModel;
	}


	public void setCommandListModel(DefaultListModel<String> commandListModel) {
		this.commandListModel = commandListModel;
	}


	public DefaultListModel<String> getCommandOrderListModel() {
		return commandOrderListModel;
	}


	public void setCommandOrderListModel(
			DefaultListModel<String> commandOrderListModel) {
		this.commandOrderListModel = commandOrderListModel;
	}


	public JComboBox<String> getProtocolArea() {
		return protocolArea;
	}


	public void setProtocolArea(JComboBox<String> protocolArea) {
		this.protocolArea = protocolArea;
	}
	
}
