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

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class CustomMessageCommandView extends JPanel{
	private static final long serialVersionUID = 1L;
	private CommandSaveView commandSaveView;
	private JSeparator separator;
	private JPanel container;
	private JTextField AFNField;
	private JTextField FnField;
	private JCheckBox isUsePw;
	private JTextArea dataBodyArea;
	
	public CustomMessageCommandView(){
		ini();
		setAttribute();
		setListener();
		loadValue();
	}
	
	public void ini(){
		commandSaveView=new CommandSaveView();
		separator=new JSeparator();
		container=new JPanel();
		AFNField=new JTextField();
		AFNField.setToolTipText("十六进制");
		FnField=new JTextField();
		FnField.setToolTipText("十进制");
		isUsePw=new JCheckBox("是否有密码Pw");
		dataBodyArea=new JTextArea();
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

		JScrollPane updateInfoScrollPane=new JScrollPane();
		updateInfoScrollPane.setBorder(new EmptyBorder(2, 2,2, 2));
		updateInfoScrollPane.setPreferredSize(new Dimension(240,150));
		updateInfoScrollPane.setViewportView(dataBodyArea);
		dataBodyArea.setFont(ViewConstants.TextFont);
		dataBodyArea.setLineWrap(true);
		dataBodyArea.setWrapStyleWord(true); 
		
		container.add(new JLabel("AFN"),  getGBC(0,1,1,1));
		container.add(AFNField,          getGBC(1,1,1,1));
		container.add(new JLabel("FN"),       getGBC(0,2,1,1));
		container.add(FnField,            getGBC(1,2,1,1));
		container.add(isUsePw,           getGBC(1,3,1,1));
		container.add(new JLabel("数据体（适用于请求一类数据）"),  getGBC(0,4,2,1));
		container.add(updateInfoScrollPane,  getGBC(0,5,2,5));
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
		final CustomMessageCommandController customMessageCommandController=
				(CustomMessageCommandController)SpringBeanUtil.getBean("customMessageCommandController");
		customMessageCommandController.setCustomMessageCommandView(this);
		
		commandSaveView.getLoadButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				customMessageCommandController.loadHandler(e);
			}
        });
		
		commandSaveView.getSaveButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				customMessageCommandController.saveHandler(e);
			}
        });
		
		commandSaveView.getDeleteButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				customMessageCommandController.deleteHandler(e);
			}
        });
		
		commandSaveView.getCommandList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					customMessageCommandController.loadHandler(e);
				}
			}
		});
	}
	
	public void loadValue(){
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.CustomMessageCommand)){
				commandSaveView.getCommandListModel().addElement(command.getName());
			}
		}
	}

	public JTextField getAFNField() {
		return AFNField;
	}

	public void setAFNField(JTextField aFNField) {
		AFNField = aFNField;
	}

	public JTextField getFnField() {
		return FnField;
	}

	public void setFnField(JTextField fnField) {
		FnField = fnField;
	}

	public JCheckBox getIsUsePw() {
		return isUsePw;
	}

	public void setIsUsePw(JCheckBox isUsePw) {
		this.isUsePw = isUsePw;
	}

	public JTextArea getDataBodyArea() {
		return dataBodyArea;
	}

	public void setDataBodyArea(JTextArea dataBodyArea) {
		this.dataBodyArea = dataBodyArea;
	}

	public CommandSaveView getCommandSaveView() {
		return commandSaveView;
	}

	public void setCommandSaveView(CommandSaveView commandSaveView) {
		this.commandSaveView = commandSaveView;
	}
	
	
}
