package com.sx.mmt.swingUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBox;
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

public class PortShiftCommandView extends JPanel{

	private static final long serialVersionUID = 1L;
	private final Font defaultFont=new Font("宋体",Font.PLAIN,14);
	private CommandSaveView commandSaveView;
	private JSeparator separator;
	private JPanel container;
	private JTextField mainIPField;
	private JTextField mainPortField;
	private JTextField subIPField;
	private JTextField subPortField;
	private JTextField APNField;
	private JCheckBox IsSetMainBySub;
	private JCheckBox isNotChangeAPN;
	
	
	public PortShiftCommandView(){
		ini();
		setAttribute();
		setListener();
		loadValue();
	}
	
	public void ini(){
		commandSaveView=new CommandSaveView();
		separator=new JSeparator();
		container=new JPanel();
		mainIPField=new JTextField();
		mainPortField=new JTextField();
		subIPField=new JTextField();
		subPortField=new JTextField();
		APNField=new JTextField();
		IsSetMainBySub=new JCheckBox("用备IP设置主IP");
		isNotChangeAPN=new JCheckBox("不要更改APN");
	}
	
	public void setAttribute(){
		GridBagLayout thisLayout=new GridBagLayout();
		thisLayout.columnWidths=new int[]{350,40,340};
		thisLayout.rowHeights=new int[]{600};
		this.setLayout(thisLayout);
		
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.add(commandSaveView,getGBC(0,0,1,1));
				
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBackground(Color.black);
		separator.setBorder(new EmptyBorder(10, 10, 10, 10));
		this.add(separator,
				new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.BOTH,new Insets(2,2,2,2),0,0));
		container.setPreferredSize(new Dimension(360, 500));
		GridBagLayout layout=new GridBagLayout();
		layout.columnWidths=new int[]{100,140,60,20};
		layout.rowHeights=new int[]{38,38,38,38,38,38,38,38,38,38};
		container.setLayout(layout);
		
		container.add(new JLabel("主IP地址"),  getGBC(0,1,1,1));	
		container.add(new JLabel("主端口"),    getGBC(0,2,1,1));	
		container.add(new JLabel("备用IP地址"),getGBC(0,3,1,1));	
		container.add(new JLabel("备用端口"),  getGBC(0,4,1,1));		
		container.add(new JLabel("APN"),      getGBC(0,5,1,1));		
		container.add(mainIPField,  getGBC(1,1,1,1));	
		container.add(mainPortField,getGBC(1,2,1,1));	
		container.add(subIPField,   getGBC(1,3,1,1));	
		container.add(subPortField, getGBC(1,4,1,1));		
		container.add(APNField,     getGBC(1,5,1,1));	
		container.add(IsSetMainBySub,getGBC(1,6,1,1));
		container.add(isNotChangeAPN,getGBC(1,7,1,1));
		
		this.add(container,getGBC(2,0,1,1));
		
		for(java.awt.Component component:container.getComponents()){
			component.setFont(defaultFont);
		}
		
		isNotChangeAPN.setSelected(true);
		APNField.setEditable(false);
	}
	
	private GridBagConstraints getGBC(int x,int y,int xn,int yn){
		Insets insets=new Insets(2,2,2,2);
		return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
	}
	
	public void setListener(){
		final PortShiftCommandController portShiftCommandController=
				(PortShiftCommandController)SpringBeanUtil.getBean("portShiftCommandController");
		portShiftCommandController.setPortShiftCommandView(this);
		
		commandSaveView.getLoadButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				portShiftCommandController.loadHandler(e);
			}
        });
		
		commandSaveView.getSaveButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				portShiftCommandController.saveHandler(e);
			}
        });
		
		commandSaveView.getDeleteButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				portShiftCommandController.deleteHandler(e);
			}
        });
		
		isNotChangeAPN.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					APNField.setEditable(false);
				}else{
					APNField.setEditable(true);
				}
				
			}
		});
		
		IsSetMainBySub.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					mainIPField.setEditable(false);
					mainPortField.setEditable(false);
					subIPField.setEditable(false);
					subPortField.setEditable(false);
				}else{
					mainIPField.setEditable(true);
					mainPortField.setEditable(true);
					subIPField.setEditable(true);
					subPortField.setEditable(true);
				}
				
			}
		});
		
		commandSaveView.getCommandList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					portShiftCommandController.loadHandler(e);
				}
			}
		});
	}
	
	public void loadValue(){
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.PortShiftCommand)){
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

	public JTextField getMainIPField() {
		return mainIPField;
	}

	public void setMainIPField(JTextField mainIPField) {
		this.mainIPField = mainIPField;
	}

	public JTextField getMainPortField() {
		return mainPortField;
	}

	public void setMainPortField(JTextField mainPortField) {
		this.mainPortField = mainPortField;
	}

	public JTextField getSubIPField() {
		return subIPField;
	}

	public void setSubIPField(JTextField subIPField) {
		this.subIPField = subIPField;
	}

	public JTextField getSubPortField() {
		return subPortField;
	}

	public void setSubPortField(JTextField subPortField) {
		this.subPortField = subPortField;
	}

	public JTextField getAPNField() {
		return APNField;
	}

	public void setAPNField(JTextField aPNField) {
		APNField = aPNField;
	}

	public JCheckBox getIsSetMainBySub() {
		return IsSetMainBySub;
	}

	public void setIsSetMainBySub(JCheckBox isMainSubSame) {
		this.IsSetMainBySub = isMainSubSame;
	}

	public JCheckBox getIsNotChangeAPN() {
		return isNotChangeAPN;
	}

	public void setIsNotChangeAPN(JCheckBox isNotChangeAPN) {
		this.isNotChangeAPN = isNotChangeAPN;
	}
		
	
	
		
}
