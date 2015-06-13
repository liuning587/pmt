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
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class TerminalParameterReadCommandView extends JPanel {
	private static final long serialVersionUID = 1L;
	private CommandSaveView commandSaveView;
	private JSeparator separator;
	private JPanel container;
	private JFormattedTextField readNumberField;
	private JFormattedTextField portShiftField;
	
	public TerminalParameterReadCommandView(){
		ini();
		setAttribute();
		setListener();
		loadValue();
	}
	
	public void ini(){
		commandSaveView=new CommandSaveView();
		separator=new JSeparator();
		container=new JPanel();
		readNumberField=new JFormattedTextField(NumberFormat.getIntegerInstance());
		portShiftField=new JFormattedTextField(NumberFormat.getIntegerInstance());
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
		
		container.add(new JLabel("读取测量点个数"),  getGBC(0,1,1,1));
		container.add(readNumberField,          getGBC(1,1,1,1));
		container.add(new JLabel("下发时的通信端口偏移"),  getGBC(0,2,1,1));
		container.add(portShiftField,          getGBC(1,2,1,1));
		container.add(new JLabel("终端参数下发命令必须配合此命令先进行参数读取"),  getGBC(0,4,2,1));
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
		final TerminalParameterReadCommandController terminalParameterReadCommandController=
				(TerminalParameterReadCommandController) SpringBeanUtil.getBean("terminalParameterReadCommandController");
		terminalParameterReadCommandController.setTerminalParameterReadCommandView(this);
		
		commandSaveView.getLoadButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				terminalParameterReadCommandController.loadHandler(e);
			}
        });
		
		commandSaveView.getSaveButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				terminalParameterReadCommandController.saveHandler(e);
				
			}
        });
		
		commandSaveView.getDeleteButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				terminalParameterReadCommandController.deleteHandler(e);
				
			}
        });
		
		commandSaveView.getCommandList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					terminalParameterReadCommandController.loadHandler(e);
				}
			}
		});
	}
	
	public void loadValue(){
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.TerminalParameterReadCommand)){
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

	public JFormattedTextField getReadNumberField() {
		return readNumberField;
	}

	public void setReadNumberField(JFormattedTextField readNumberField) {
		this.readNumberField = readNumberField;
	}

	public JFormattedTextField getPortShiftField() {
		return portShiftField;
	}

	public void setPortShiftField(JFormattedTextField portShiftField) {
		this.portShiftField = portShiftField;
	}
	
	
}
