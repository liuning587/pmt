package com.sx.mmt.swingUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

public class DelayCommandView extends JPanel{

	private static final long serialVersionUID = 1L;
	private final Font defaultFont=new Font("宋体",Font.PLAIN,14);
	private CommandSaveView commandSaveView;
	private JSeparator separator;
	private JPanel container;
	private JTextField delayTimeField;
	
	public DelayCommandView(){
		ini();
		setAttribute();
		setListener();
		loadValue();
	}
	
	public void ini(){
		commandSaveView=new CommandSaveView();
		separator=new JSeparator();
		container=new JPanel();
		delayTimeField=new JTextField();
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
		
		container.add(new JLabel("延迟时间(秒)"),  getGBC(0,1,1,1));	
		container.add(delayTimeField,  getGBC(1,1,1,1));	
		
		this.add(container,getGBC(2,0,1,1));
		for(java.awt.Component component:container.getComponents()){
			component.setFont(defaultFont);
		}
	}
	
	private GridBagConstraints getGBC(int x,int y,int xn,int yn){
		Insets insets=new Insets(2,2,2,2);
		return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
	}
	
	
	public void setListener(){
		final DelayCommandController delayCommandController=
				(DelayCommandController)SpringBeanUtil.getBean("delayCommandController");
		delayCommandController.setDelayCommandView(this);
		
		commandSaveView.getLoadButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				delayCommandController.loadHandler(e);
			}
        });
		
		commandSaveView.getSaveButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				delayCommandController.saveHandler(e);
			}
        });
		
		commandSaveView.getDeleteButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				delayCommandController.deleteHandler(e);
			}
        });
		
		commandSaveView.getCommandList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					delayCommandController.loadHandler(e);
				}
			}
		});
	}
	
	public void loadValue(){
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.DelayCommand)){
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

	public JTextField getDelayTimeField() {
		return delayTimeField;
	}

	public void setDelayTimeField(JTextField delayTimeField) {
		this.delayTimeField = delayTimeField;
	}

	
	
	
}
