package com.sx.mmt.swingUI;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.task.TaskXmlResolver;
import com.sx.mmt.internal.task.command.CommandConfig;
import com.sx.mmt.internal.task.command.CommandFactory;
import com.sx.mmt.internal.task.command.CommandXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class QueryVersionCommandView extends JPanel{

	private static final long serialVersionUID = 1L;
	private CommandSaveView commandSaveView;
	private JSeparator separator;
	private JPanel container;
	private JComboBox<String> versionTypeComboBox;
	private JTextField versionListField;
	private JTextField dateFromField;
	private JTextField dateToField;
	private JComboBox<String> inExecuteComboBox;
	private JComboBox<String> outExcuteComboBox;
	private JCheckBox useListCheckBox;
	private JCheckBox useDateCheckBox;
	
	
	public QueryVersionCommandView(){
		ini();
		setAttribute();
		setListener();
		loadValue();
	}
	
	public void ini(){
		commandSaveView=new CommandSaveView();
		separator=new JSeparator();
		container=new JPanel();
		versionTypeComboBox=new JComboBox<String>(new String[]{ViewConstants.AppVersion
				,ViewConstants.CoreVersion});
		versionListField=new JTextField();
		versionListField.setToolTipText("多个版本用英文分号隔开");
		dateFromField=new JTextField();
		dateToField=new JTextField();
		String tooltip="日期格式2014-10-01";
		dateFromField.setToolTipText(tooltip);
		dateToField.setToolTipText(tooltip);
		inExecuteComboBox=new JComboBox<String>();
		inExecuteComboBox.setSelectedItem(0);
		outExcuteComboBox=new JComboBox<String>();
		outExcuteComboBox.setSelectedItem(0);
		useListCheckBox=new JCheckBox("使用版本列表过滤");
		useListCheckBox.setSelected(true);
		useDateCheckBox=new JCheckBox("使用日期范围过滤");
		useDateCheckBox.setSelected(true);
	}
	
	public void setAttribute(){
		Insets insets=new Insets(2,2,2,2);
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
				new GridBagConstraints(1,0,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.BOTH,insets,0,0));
		container.setPreferredSize(new Dimension(360, 500));
		GridBagLayout layout=new GridBagLayout();
		layout.columnWidths=new int[]{100,140,60,20};
		layout.rowHeights=new int[]{38,38,38,38,38,38,38,38,38,38};
		container.setLayout(layout);
		
		container.add(new JLabel("版本类型"),       getGBC(0,1,1,1));
		container.add(versionTypeComboBox,         getGBC(1,1,1,1));
		container.add(new JLabel("版本列表"),       getGBC(0,2,1,1));
		container.add(versionListField,           getGBC(1,2,1,1));		
		container.add(new JLabel("版本日期范围从"),    getGBC(0,3,1,1));
		container.add(dateFromField,                  getGBC(1,3,1,1));
		container.add(new JLabel("版本日期范围到"),  getGBC(0,4,1,1));
		container.add(dateToField,                 getGBC(1,4,1,1));
		container.add(new JLabel("在列表或日期范围内执行任务"),    getGBC(0,5,2,1));
		container.add(inExecuteComboBox,           getGBC(1,6,1,1));
		container.add(new JLabel("否则执行任务"),    getGBC(0,7,1,1));
		container.add(outExcuteComboBox,           getGBC(1,7,1,1));
		container.add(useListCheckBox,             getGBC(1,8,1,1));
		container.add(useDateCheckBox,            getGBC(1,9,1,1));
		
		this.add(container,getGBC(2,0,1,1));
		for(java.awt.Component component:container.getComponents()){
			component.setFont(ViewConstants.TextFont);
		}
	}
	
	private GridBagConstraints getGBC(int x,int y,int xn,int yn){
		Insets insets=new Insets(2,2,2,2);
		return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
	}
	
	public void setListener(){
		final QueryVersionCommandController queryVersionCommandController=
				(QueryVersionCommandController) SpringBeanUtil.getBean("queryVersionCommandController");
		queryVersionCommandController.setQueryVersionCommandView(this);
		
		commandSaveView.getLoadButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				queryVersionCommandController.loadHandler(e);
			}
        });
		
		commandSaveView.getSaveButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				queryVersionCommandController.saveHandler(e);
			}
        });
		
		commandSaveView.getDeleteButton().addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				queryVersionCommandController.deleteHandler(e);
			}
        });
		
		useListCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					versionListField.setEditable(true);
				}else{
					versionListField.setEditable(false);
					versionListField.setText("");
				}
			}
		});
		
		useDateCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					dateFromField.setEditable(true);
					dateToField.setEditable(true);
				}else{
					dateFromField.setEditable(false);
					dateToField.setEditable(false);
					dateFromField.setText("");
					dateToField.setText("");
				}
			}
		});
		
		commandSaveView.getCommandList().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount()==2){
					queryVersionCommandController.loadHandler(e);
				}
			}
		});
		
	}
	
	public void loadValue(){
		CommandXmlResolver commandXmlResolver=
				(CommandXmlResolver)SpringBeanUtil.getBean("commandXmlResolver");
		for(CommandConfig command:commandXmlResolver.getCommands().values()){
			if(command.getClazz().equals(CommandFactory.QueryVersionCommand)){
				commandSaveView.getCommandListModel().addElement(command.getName());
			}
		}
		refreshValue();
	}
	
	public void refreshValue(){
		Object s1=inExecuteComboBox.getSelectedItem();
		Object s2=outExcuteComboBox.getSelectedItem();
		inExecuteComboBox.removeAllItems();
		outExcuteComboBox.removeAllItems();
		inExecuteComboBox.addItem("");
		outExcuteComboBox.addItem("");
		TaskXmlResolver taskXmlResolver=
				(com.sx.mmt.internal.task.TaskXmlResolver) SpringBeanUtil.getBean("taskXmlResolver");
		for(String name:taskXmlResolver.getTasks().keySet()){
			inExecuteComboBox.addItem(name);
			outExcuteComboBox.addItem(name);
		}
		if(s1==null){
			inExecuteComboBox.setSelectedIndex(0);
		}else{
			inExecuteComboBox.setSelectedItem(s1);
		}
		if(s2==null){
			outExcuteComboBox.setSelectedIndex(0);
		}else{
			outExcuteComboBox.setSelectedItem(s2);
		}
		
	}

	public CommandSaveView getCommandSaveView() {
		return commandSaveView;
	}

	public void setCommandSaveView(CommandSaveView commandSaveView) {
		this.commandSaveView = commandSaveView;
	}

	public JComboBox<String> getVersionTypeComboBox() {
		return versionTypeComboBox;
	}

	public void setVersionTypeComboBox(JComboBox<String> versionTypeComboBox) {
		this.versionTypeComboBox = versionTypeComboBox;
	}

	

	

	public JTextField getVersionListField() {
		return versionListField;
	}

	public void setVersionListField(JTextField versionListField) {
		this.versionListField = versionListField;
	}

	public JTextField getDateFromField() {
		return dateFromField;
	}

	public void setDateFromField(JTextField dateFromField) {
		this.dateFromField = dateFromField;
	}

	public JTextField getDateToField() {
		return dateToField;
	}

	public void setDateToField(JTextField dateToField) {
		this.dateToField = dateToField;
	}

	public JComboBox<String> getInExecuteComboBox() {
		return inExecuteComboBox;
	}

	public void setInExecuteComboBox(JComboBox<String> inExecuteComboBox) {
		this.inExecuteComboBox = inExecuteComboBox;
	}

	public JComboBox<String> getOutExcuteComboBox() {
		return outExcuteComboBox;
	}

	public void setOutExcuteComboBox(JComboBox<String> outExcuteComboBox) {
		this.outExcuteComboBox = outExcuteComboBox;
	}

	public JCheckBox getUseListCheckBox() {
		return useListCheckBox;
	}

	public void setUseListCheckBox(JCheckBox useListCheckBox) {
		this.useListCheckBox = useListCheckBox;
	}

	public JCheckBox getUseDateCheckBox() {
		return useDateCheckBox;
	}

	public void setUseDateCheckBox(JCheckBox useDateCheckBox) {
		this.useDateCheckBox = useDateCheckBox;
	}

	
	
	
	
	
}