package com.sx.mmt.swingUI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

public class CommandSaveView extends JPanel{

	private static final long serialVersionUID = 1L;
	private final Font defaultFont=new Font("宋体",Font.PLAIN,14);
	private JCheckBox activateCheckBox;
	private JTextField commandNameField;
	private JList<String> commandList;
	private JButton loadButton;
	private JButton saveButton;
	private JButton deleteButton;
	private DefaultListModel<String> commandListModel;
	
	public CommandSaveView(){
		ini();
		setAttribute();
	}
	
	public void ini(){
		activateCheckBox=new JCheckBox("启用");
		commandNameField=new JTextField();
		commandListModel=new DefaultListModel<String>();
		commandList=new JList<String>(commandListModel);
		loadButton=new JButton("载入");
		saveButton=new JButton("保存");
		deleteButton=new JButton("删除");
		
	}
	
	public void setAttribute(){
		this.setMaximumSize(new Dimension(800, 600));
		this.setPreferredSize(new Dimension(360, 500));
		GridBagLayout layout=new GridBagLayout();
		layout.columnWidths=new int[]{20,100,200,20};
		layout.rowHeights=new int[]{38,38,38,38,38,38,38,38,38,38};
		this.setLayout(layout);
		JScrollPane commandListScrollPane=new JScrollPane();
		commandListScrollPane.setPreferredSize(new Dimension(198, 180));
		commandListScrollPane.setViewportView(commandList);

		this.add(activateCheckBox,     getGBC(1,1,1,1));				
		this.add(new JLabel("命令名称"),getGBC(1,2,1,1));		
		this.add(commandNameField,     getGBC(2,2,1,1));		
		this.add(loadButton,           getGBC(1,3,1,1));		
		this.add(saveButton,           getGBC(1,5,1,1));	
		this.add(deleteButton,         getGBC(1,7,1,1));	
		this.add(commandListScrollPane,getGBC(2,3,1,5));
				
		for(java.awt.Component component:this.getComponents()){
			component.setFont(defaultFont);
		}
		commandList.setFont(defaultFont);
		
		commandList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	private GridBagConstraints getGBC(int x,int y,int xn,int yn){
		Insets insets=new Insets(2,2,2,2);
		return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
	}
	

	public JCheckBox getActivateCheckBox() {
		return activateCheckBox;
	}

	public void setActivateCheckBox(JCheckBox activateCheckBox) {
		this.activateCheckBox = activateCheckBox;
	}


	public JTextField getCommandNameField() {
		return commandNameField;
	}

	public void setCommandNameField(JTextField commandNameField) {
		this.commandNameField = commandNameField;
	}

	public JList<String> getCommandList() {
		return commandList;
	}

	public void setCommandList(JList<String> commandList) {
		this.commandList = commandList;
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

	public DefaultListModel<String> getCommandListModel() {
		return commandListModel;
	}

	public void setCommandListModel(DefaultListModel<String> commandListModel) {
		this.commandListModel = commandListModel;
	}
	
	
	
	
}
