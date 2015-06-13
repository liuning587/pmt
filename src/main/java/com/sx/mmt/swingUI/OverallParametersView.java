package com.sx.mmt.swingUI;

import static com.sx.mmt.internal.util.PropertiesUtil.parseToInt;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class OverallParametersView extends JPanel{
	private static final long serialVersionUID = 1L;
	
	//工具条按钮
	private JToolBar topToolBar;
	private JButton saveButton;
	
	private JFormattedTextField responseTimeoutField;
	private JFormattedTextField packetSendIntervalField;
	private JFormattedTextField taskMaxNumberField;
	private JComboBox<Integer> protocolTerminalAddressLengthField;
	private JFormattedTextField serverMsaField;
	private JFormattedTextField maxRetry;
	private JCheckBox isLog;
	
	public OverallParametersView(){
		ini();
		loadValue();
		setListener();
	}
	public void ini(){
		topToolBar=new JToolBar();
		saveButton=new JButton("保存");
		isLog=new JCheckBox("是否记录日志");
		topToolBar.add(saveButton);
		this.setLayout(new BorderLayout(5, 5));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		topToolBar.setFloatable(false);
		this.add(topToolBar,BorderLayout.NORTH);
		
		responseTimeoutField=new JFormattedTextField(NumberFormat.getIntegerInstance(Locale.CHINA));
		packetSendIntervalField=new JFormattedTextField(NumberFormat.getIntegerInstance(Locale.CHINA));
		taskMaxNumberField=new JFormattedTextField(NumberFormat.getIntegerInstance(Locale.CHINA));
		protocolTerminalAddressLengthField=new JComboBox<Integer>(new Integer[]{5,7});
		serverMsaField=new JFormattedTextField(NumberFormat.getIntegerInstance(Locale.CHINA));
		maxRetry=new JFormattedTextField(NumberFormat.getIntegerInstance(Locale.CHINA));
		GridBagLayout thisLayout=new GridBagLayout();
		thisLayout.columnWidths=new int[]{20,100,150,20};
		thisLayout.rowHeights=new int[]{50,50,50,50,50,50,50,50};
		JPanel configPanel=new JPanel();
		
		configPanel.setBorder((BevelBorder)BorderFactory.createRaisedBevelBorder());
		configPanel.setLayout(thisLayout);
		this.add(configPanel,BorderLayout.CENTER);
		
		int index=1;
		configPanel.add(new JLabel("回复超时时间（秒）"),getGBC(1,index,1,1));
		configPanel.add(responseTimeoutField,getGBC(2,index,1,1));
		index++;
		configPanel.add(new JLabel("报文发送间隔（秒）"),   getGBC(1,index,1,1));
		configPanel.add(packetSendIntervalField,   getGBC(2,index,1,1));
		index++;
		configPanel.add(new JLabel("最多同时任务数"),   getGBC(1,index,1,1));
		configPanel.add(taskMaxNumberField,   getGBC(2,index,1,1));
		index++;
		configPanel.add(new JLabel("最大重试次数"),    getGBC(1,index,1,1));
		configPanel.add(maxRetry,    getGBC(2,index,1,1));
		configPanel.add(new JLabel("0表示无限制"),    getGBC(3,index,1,1));
		index++;
		configPanel.add(new JLabel("终端地址长度"),    getGBC(1,index,1,1));
		configPanel.add(protocolTerminalAddressLengthField,    getGBC(2,index,1,1));
		index++;
		configPanel.add(new JLabel("主站编号"),    getGBC(1,index,1,1));
		configPanel.add(serverMsaField,    getGBC(2,index,1,1));
		index++;
		configPanel.add(isLog,    getGBC(2,index,1,1));
	}
	
	private GridBagConstraints getGBC(int x,int y,int xn,int yn){
		Insets insets=new Insets(2,2,2,2);
		return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
	}
	
	public void loadValue(){
		responseTimeoutField.setValue(parseToInt(ConfigConstants.ResponseTimeout));
		packetSendIntervalField.setValue(parseToInt(ConfigConstants.PacketSendInterval));
		taskMaxNumberField.setValue(parseToInt(ConfigConstants.TaskMaxNumber));
		protocolTerminalAddressLengthField.setSelectedItem(
				parseToInt(ConfigConstants.ProtocolTerminalAddressLength));
		serverMsaField.setValue(parseToInt(ConfigConstants.ConnectionServerMsa));
		maxRetry.setValue(parseToInt(ConfigConstants.MaxRetryCount));
		isLog.setSelected(PropertiesUtil.parseToBoolean(ConfigConstants.IsLog));
	}
	
	public void saveValue(){
		PropertiesUtil propertiesUtil=
				(PropertiesUtil)SpringBeanUtil.getBean("propertiesUtil");
		propertiesUtil.modify(ConfigConstants.ResponseTimeout, 
				responseTimeoutField.getValue().toString());
		propertiesUtil.modify(ConfigConstants.PacketSendInterval, 
				packetSendIntervalField.getValue().toString());
		propertiesUtil.modify(ConfigConstants.TaskMaxNumber, 
				taskMaxNumberField.getValue().toString());
		propertiesUtil.modify(ConfigConstants.ProtocolTerminalAddressLength, 
				protocolTerminalAddressLengthField.getSelectedItem().toString());
		propertiesUtil.modify(ConfigConstants.ConnectionServerMsa, 
				serverMsaField.getValue().toString());
		propertiesUtil.modify(ConfigConstants.MaxRetryCount, 
				maxRetry.getValue().toString());
		propertiesUtil.modify(ConfigConstants.IsLog, 
				String.valueOf(isLog.isSelected()));
		propertiesUtil.save();
	}
	
	public void setListener(){
		final OverallParametersView me=this;
		saveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				saveValue();
				JOptionPane.showMessageDialog(me, "保存成功");
			}
        });
	}
}
