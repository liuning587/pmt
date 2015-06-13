package com.sx.mmt.swingUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.connection.ConnectXmlResolver;
import com.sx.mmt.internal.task.TaskXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class ConnectionConfigView extends JPanel{
	private static final long serialVersionUID = 1L;
	private ConnectXmlResolver ConnectXmlResolver;
	//工具条按钮
	private JToolBar topToolBar;
	private JButton saveButton;
	private JButton testButton;
	
	//连接方式列表
	
	private JList<ConnectConfig> connectList;
	private DefaultListModel<ConnectConfig> connectListModel;
	//不同连接方式的配置面板
	private Map<String,ConfigPanel> connectPanelList;
	
	
	
	public JList<ConnectConfig> getConnectList() {
		return connectList;
	}
	public DefaultListModel<ConnectConfig> getConnectListModel() {
		return connectListModel;
	}
	public Map<String, ConfigPanel> getConnectPanelList() {
		return connectPanelList;
	}

	public ConnectionConfigView(){
		ConnectXmlResolver=(ConnectXmlResolver) 
				SpringBeanUtil.getBean("connectXmlResolver");
		
		ini();
		setAttribute();
		setListener();
		loadValue();
	}
	
	public void ini(){
		topToolBar=new JToolBar();
		saveButton=new JButton("选择并保存");
		testButton=new JButton("测试");
		topToolBar.add(saveButton);
		topToolBar.add(testButton);
		JLabel warn=new JLabel("                              切换连接方式后请重启软件以使配置生效");
		warn.setForeground(Color.BLUE);
		topToolBar.add(warn);
		
		
		connectListModel=new DefaultListModel<ConnectConfig>();
		connectList=new JList<ConnectConfig>(connectListModel);
		
		connectPanelList=new HashMap<String,ConfigPanel>();
		
		
	}
	
	public void setAttribute(){
		this.setLayout(new BorderLayout(5, 5));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		topToolBar.setFloatable(false);
		this.add(topToolBar,BorderLayout.NORTH);
		//左侧
		JPanel connectListPanel=new JPanel();
		connectListPanel.setLayout(new BorderLayout(5, 5));
		connectListPanel.add(new JLabel("连接方式列表"),BorderLayout.NORTH);
		JScrollPane connectListScrollPane=new JScrollPane();
		connectListScrollPane.setViewportView(connectList);
		connectList.setFont(ViewConstants.TextFont);
		connectList.setCellRenderer(new DefaultListCellRenderer(){
			private static final long serialVersionUID = 1L;
			
			@Override
			public Component getListCellRendererComponent(JList<?> list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
					Component component=super.getListCellRendererComponent
							(list, value, index, isSelected, cellHasFocus);
				    ConnectConfig config=(ConnectConfig)value;
				    
					if(config.isUse()){
						setIcon(new ImageIcon(getClass().getResource("/img/finish.png")));
					}
					return component;
				}
		});
		connectListPanel.add(connectListScrollPane,BorderLayout.CENTER);
		connectListPanel.setBorder((BevelBorder)BorderFactory.createRaisedBevelBorder());
		connectListPanel.setPreferredSize(new Dimension(250, 500));
		this.add(connectListPanel,BorderLayout.WEST);
		
		FrontModelPanel frontModelPanel=new FrontModelPanel();
		connectPanelList.put(ViewConstants.FrontMode,frontModelPanel);
		
		StationTransparentPanel stationTransparentPanel=new StationTransparentPanel();
		connectPanelList.put(ViewConstants.StationTransparent, stationTransparentPanel);
		
		StationCQDareWaySocketPanel stationCQDareWaySocketPanel=
				new StationCQDareWaySocketPanel();
		connectPanelList.put(ViewConstants.StationCQDareWaySocket, stationCQDareWaySocketPanel);
		
		StationCQDareWayPanel stationCQDareWayPanel=new StationCQDareWayPanel();
		connectPanelList.put(ViewConstants.StationCQDareWay, stationCQDareWayPanel);
		
		StationSDDareWayPanel stationSDDareWayPanel=new StationSDDareWayPanel();
		connectPanelList.put(ViewConstants.StationSDDareWay, stationSDDareWayPanel);
		
		StationJSATCTPanel stationJSATCTPanel=new StationJSATCTPanel();
		connectPanelList.put(ViewConstants.StationJSATCT, stationJSATCTPanel);
		
		StationJLInterfacePanel stationJLInterfacePanel=new StationJLInterfacePanel();
		connectPanelList.put(ViewConstants.StationJLInterface, stationJLInterfacePanel);
		
	}
	
	public void setListener(){
		final ConnectionConfigController connectionConfigController=
				(ConnectionConfigController)SpringBeanUtil.getBean("connectionConfigController");
		connectionConfigController.setConnectionConfigView(this);
		final ConnectionConfigView me=this;
		saveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				connectionConfigController.saveHandler(e);
			}
        });
		testButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				connectionConfigController.testConnectHandler(e);
			}
        });
		connectList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()){
					ConnectConfig firstconfig=connectList.getSelectedValue();
					ConfigPanel firstpanel=connectPanelList.get(firstconfig.getName());
					firstpanel.loadValue(firstconfig.getAttr());
					try{
						me.remove(2);
					}catch(Exception e1){}
					me.add((Component)firstpanel,BorderLayout.CENTER);
					me.updateUI();
				}
			}
		});
		
	}
	
	public void loadValue(){
		Map<String,ConnectConfig> connects=ConnectXmlResolver.getconnects();
		int idx=0;
		int selectedIdx=0;
		connectListModel.clear();
		for(ConnectConfig config:connects.values()){
			connectListModel.addElement(config);
			ConfigPanel panel=connectPanelList.get(config.getName());
			
			if(config.isUse() && panel!=null){
				selectedIdx=idx;
				panel.loadValue(config.getAttr());
				this.add((Component) panel,BorderLayout.CENTER);
			}
			idx++;
		}
		connectList.setSelectedIndex(selectedIdx);
	}
	
	public class StationJLInterfacePanel extends JPanel implements ConfigPanel{
		private static final long serialVersionUID = 1L;
		private JLabel frontAddressLabel;
		private JLabel frontPortLabel;
		private JTextField frontAddressField;
		private JFormattedTextField frontPortField;
		private JLabel channelNoLabel;
		private JTextField channelNoField;
		private JLabel userNameLabel;
		private JTextField userNameField;
		private JLabel passwordLabel;
		private JTextField passwordField;
		
		public StationJLInterfacePanel(){
			frontAddressLabel=new JLabel("前置机IP地址");
			frontPortLabel=new JLabel("前置机端口");
			frontAddressField=new JTextField();
			frontPortField=new JFormattedTextField(NumberFormat.getIntegerInstance());
			channelNoLabel=new JLabel("升级通道地址");
			channelNoField=new JTextField();
			userNameLabel=new JLabel("用户名");
			userNameField=new JTextField();
			passwordLabel=new JLabel("密码");
			passwordField=new JTextField();
			ini();
			
		}
		
		public void ini(){
			GridBagLayout thisLayout=new GridBagLayout();
			thisLayout.columnWidths=new int[]{20,100,150,20};
			thisLayout.rowHeights=new int[]{50,50,50,50,50,50,50,50};
			this.setBorder((BevelBorder)BorderFactory.createRaisedBevelBorder());
			this.setLayout(thisLayout);
			this.add(frontAddressLabel,getGBC(1,1,1,1));
			this.add(frontAddressField,getGBC(2,1,1,1));
			this.add(frontPortLabel,   getGBC(1,2,1,1));
			this.add(frontPortField,   getGBC(2,2,1,1));
			this.add(channelNoLabel,   getGBC(1,3,1,1));
			this.add(channelNoField,   getGBC(2,3,1,1));
			this.add(userNameLabel,    getGBC(1,4,1,1));
			this.add(userNameField,    getGBC(2,4,1,1));
			this.add(passwordLabel,    getGBC(1,5,1,1));
			this.add(passwordField,    getGBC(2,5,1,1));
			
		}
		private GridBagConstraints getGBC(int x,int y,int xn,int yn){
			Insets insets=new Insets(2,2,2,2);
			return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
		}
		
		@Override
		public void loadValue(Map<String, String> attr) {
			if(attr==null) return;
			frontAddressField.setText(attr.get(ConfigConstants.FrontAddress));
			frontPortField.setValue(Integer.parseInt(attr.get(ConfigConstants.FrontPort)));
			channelNoField.setText(attr.get(ConfigConstants.ChannelNo));
			userNameField.setText(attr.get(ConfigConstants.UserName));
			passwordField.setText(attr.get(ConfigConstants.Password));
			
		}

		@Override
		public Map<String, String> getValue() {
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.FrontAddress, frontAddressField.getText());
			attr.put(ConfigConstants.FrontPort, frontPortField.getValue().toString());
			attr.put(ConfigConstants.ChannelNo, channelNoField.getText());
			attr.put(ConfigConstants.UserName, userNameField.getText());
			attr.put(ConfigConstants.Password, passwordField.getText());
			return attr;
		}
		
	}
	
	public class StationJSATCTPanel extends JPanel implements ConfigPanel{
		private static final long serialVersionUID = 1L;
		private JLabel frontAddressLabel;
		private JLabel frontPortLabel;
		private JTextField frontAddressField;
		private JFormattedTextField frontPortField;
		
		public StationJSATCTPanel(){
			frontAddressLabel=new JLabel("前置机IP地址");
			frontPortLabel=new JLabel("前置机端口");
			frontAddressField=new JTextField();
			frontPortField=new JFormattedTextField(NumberFormat.getIntegerInstance());
			ini();
		}
		
		public void ini(){
			GridBagLayout thisLayout=new GridBagLayout();
			thisLayout.columnWidths=new int[]{20,100,150,20};
			thisLayout.rowHeights=new int[]{50,50,50,50,50,50,50,50};
			this.setBorder((BevelBorder)BorderFactory.createRaisedBevelBorder());
			this.setLayout(thisLayout);
			this.add(frontAddressLabel,getGBC(1,1,1,1));
			this.add(frontAddressField,getGBC(2,1,1,1));
			this.add(frontPortLabel,   getGBC(1,2,1,1));
			this.add(frontPortField,   getGBC(2,2,1,1));
			
		}
		private GridBagConstraints getGBC(int x,int y,int xn,int yn){
			Insets insets=new Insets(2,2,2,2);
			return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
		}
		
		@Override
		public void loadValue(Map<String, String> attr) {
			if(attr==null) return;
			frontAddressField.setText(attr.get(ConfigConstants.FrontAddress));
			frontPortField.setValue(Integer.parseInt(attr.get(ConfigConstants.FrontPort)));
			
		}

		@Override
		public Map<String, String> getValue() {
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.FrontAddress, frontAddressField.getText());
			attr.put(ConfigConstants.FrontPort, frontPortField.getValue().toString());
			return attr;
		}
	}
	
	public class StationSDDareWayPanel extends JPanel implements ConfigPanel{
		private static final long serialVersionUID = 1L;
		private JLabel wsdlLabel;
		private JTextField wsdlField;
		public StationSDDareWayPanel(){
			wsdlLabel=new JLabel("WSDL");
			wsdlField=new JTextField();
			ini();
		}
		
		public void ini(){
			GridBagLayout thisLayout=new GridBagLayout();
			thisLayout.columnWidths=new int[]{20,100,150,20};
			thisLayout.rowHeights=new int[]{50,50,50,50,50,50,50,50};
			this.setBorder((BevelBorder)BorderFactory.createRaisedBevelBorder());
			this.setLayout(thisLayout);
			this.add(wsdlLabel,        getGBC(1,1,1,1));
			this.add(wsdlField,        getGBC(2,1,1,1));

		}
		private GridBagConstraints getGBC(int x,int y,int xn,int yn){
			Insets insets=new Insets(2,2,2,2);
			return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
		}
		
		@Override
		public void loadValue(Map<String, String> attr) {
			if(attr==null) return;
			wsdlField.setText(attr.get(ConfigConstants.Wsdl));
			
		}

		@Override
		public Map<String, String> getValue() {
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.Wsdl, wsdlField.getText());
			return attr;
		}
		
	}
	
	
	public class StationCQDareWayPanel extends JPanel implements ConfigPanel{
		private static final long serialVersionUID = 1L;
		private JLabel wsdlLabel;
		private JTextField wsdlField;
		private JLabel channelNoLabel;
		private JTextField channelNoField;
		public StationCQDareWayPanel(){
			wsdlLabel=new JLabel("WSDL");
			wsdlField=new JTextField();
			channelNoLabel=new JLabel("升级审核编号");
			channelNoField=new JTextField();
			ini();
		}
		
		public void ini(){
			GridBagLayout thisLayout=new GridBagLayout();
			thisLayout.columnWidths=new int[]{20,100,150,20};
			thisLayout.rowHeights=new int[]{50,50,50,50,50,50,50,50};
			this.setBorder((BevelBorder)BorderFactory.createRaisedBevelBorder());
			this.setLayout(thisLayout);
			this.add(wsdlLabel,        getGBC(1,1,1,1));
			this.add(wsdlField,        getGBC(2,1,1,1));
			this.add(channelNoLabel,   getGBC(1,2,1,1));
			this.add(channelNoField,   getGBC(2,2,1,1));
		}
		private GridBagConstraints getGBC(int x,int y,int xn,int yn){
			Insets insets=new Insets(2,2,2,2);
			return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
		}
		
		@Override
		public void loadValue(Map<String, String> attr) {
			if(attr==null) return;
			wsdlField.setText(attr.get(ConfigConstants.Wsdl));
			channelNoField.setText(attr.get(ConfigConstants.ChannelNo));
			
		}

		@Override
		public Map<String, String> getValue() {
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.Wsdl, wsdlField.getText());
			attr.put(ConfigConstants.ChannelNo, channelNoField.getText());
			return attr;
		}
		
		
	}
	
	
	public class StationCQDareWaySocketPanel extends JPanel implements ConfigPanel{
		private static final long serialVersionUID = 1L;
		private JLabel frontAddressLabel;
		private JLabel frontPortLabel;
		private JTextField frontAddressField;
		private JFormattedTextField frontPortField;
		private JLabel channelNoLabel;
		private JTextField channelNoField;
		public StationCQDareWaySocketPanel(){
			frontAddressLabel=new JLabel("前置机IP地址");
			frontPortLabel=new JLabel("前置机端口");
			frontAddressField=new JTextField();
			frontPortField=new JFormattedTextField(NumberFormat.getIntegerInstance());
			channelNoLabel=new JLabel("升级审核编号");
			channelNoField=new JTextField();
			ini();
		}
		
		public void ini(){
			GridBagLayout thisLayout=new GridBagLayout();
			thisLayout.columnWidths=new int[]{20,100,150,20};
			thisLayout.rowHeights=new int[]{50,50,50,50,50,50,50,50};
			this.setBorder((BevelBorder)BorderFactory.createRaisedBevelBorder());
			this.setLayout(thisLayout);
			this.add(frontAddressLabel,getGBC(1,1,1,1));
			this.add(frontAddressField,getGBC(2,1,1,1));
			this.add(frontPortLabel,   getGBC(1,2,1,1));
			this.add(frontPortField,   getGBC(2,2,1,1));
			this.add(channelNoLabel,   getGBC(1,3,1,1));
			this.add(channelNoField,   getGBC(2,3,1,1));
			
		}
		private GridBagConstraints getGBC(int x,int y,int xn,int yn){
			Insets insets=new Insets(2,2,2,2);
			return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
		}
		
		@Override
		public void loadValue(Map<String, String> attr) {
			if(attr==null) return;
			frontAddressField.setText(attr.get(ConfigConstants.FrontAddress));
			frontPortField.setValue(Integer.parseInt(attr.get(ConfigConstants.FrontPort)));
			channelNoField.setText(attr.get(ConfigConstants.ChannelNo));
			
		}

		@Override
		public Map<String, String> getValue() {
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.FrontAddress, frontAddressField.getText());
			attr.put(ConfigConstants.FrontPort, frontPortField.getValue().toString());
			attr.put(ConfigConstants.ChannelNo, channelNoField.getText());
			return attr;
		}
	}
	
	
	public class StationTransparentPanel extends JPanel implements ConfigPanel{
		private static final long serialVersionUID = 1L;
		private JLabel frontAddressLabel;
		private JLabel frontPortLabel;
		private JTextField frontAddressField;
		private JFormattedTextField frontPortField;
		
		public StationTransparentPanel(){
			frontAddressLabel=new JLabel("前置机IP地址");
			frontPortLabel=new JLabel("前置机端口");
			frontAddressField=new JTextField();
			frontPortField=new JFormattedTextField(NumberFormat.getIntegerInstance());
			ini();
		}
		
		public void ini(){
			GridBagLayout thisLayout=new GridBagLayout();
			thisLayout.columnWidths=new int[]{20,100,150,20};
			thisLayout.rowHeights=new int[]{50,50,50,50,50,50,50,50};
			this.setBorder((BevelBorder)BorderFactory.createRaisedBevelBorder());
			this.setLayout(thisLayout);
			this.add(frontAddressLabel,getGBC(1,1,1,1));
			this.add(frontAddressField,getGBC(2,1,1,1));
			this.add(frontPortLabel,   getGBC(1,2,1,1));
			this.add(frontPortField,   getGBC(2,2,1,1));
			
		}
		private GridBagConstraints getGBC(int x,int y,int xn,int yn){
			Insets insets=new Insets(2,2,2,2);
			return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
		}
		
		@Override
		public void loadValue(Map<String, String> attr) {
			if(attr==null) return;
			frontAddressField.setText(attr.get(ConfigConstants.FrontAddress));
			frontPortField.setValue(Integer.parseInt(attr.get(ConfigConstants.FrontPort)));
			
		}

		@Override
		public Map<String, String> getValue() {
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.FrontAddress, frontAddressField.getText());
			attr.put(ConfigConstants.FrontPort, frontPortField.getValue().toString());
			return attr;
		}
	}
	
	
	public class FrontModelPanel extends JPanel implements ConfigPanel{
		private static final long serialVersionUID = 1L;
		private JCheckBox autoCreateTaskCheckBox;
		private JCheckBox autoDetermineProtocol;
		private JLabel createTaskTypeLabel;
		private JLabel listenerPortLabel;
		private JFormattedTextField listenerPort;
		private JComboBox<String> createTaskType;
		
		public FrontModelPanel(){
			autoCreateTaskCheckBox=new JCheckBox("允许终端一上线自动开始任务");
			autoDetermineProtocol=new JCheckBox("自动判断规约类型");
			createTaskTypeLabel=new JLabel("执行任务类型");
			listenerPortLabel=new JLabel("监听端口");
			listenerPort=new JFormattedTextField(NumberFormat.getIntegerInstance());
			createTaskType=new JComboBox<String>();
			ini();
		}
		
		public void ini(){
			GridBagLayout thisLayout=new GridBagLayout();
			thisLayout.columnWidths=new int[]{20,100,150,20};
			thisLayout.rowHeights=new int[]{50,50,50,50,50,50,50,50};
			this.setBorder((BevelBorder)BorderFactory.createRaisedBevelBorder());
			this.setLayout(thisLayout);
			this.add(autoCreateTaskCheckBox,getGBC(2,1,1,1));
			this.add(autoDetermineProtocol,getGBC(2,2,1,1));
			this.add(createTaskTypeLabel,getGBC(1,3,1,1));
			this.add(createTaskType,getGBC(2,3,1,1));
			this.add(listenerPortLabel,getGBC(1,4,1,1));
			this.add(listenerPort,getGBC(2,4,1,1));
			
		}
		private GridBagConstraints getGBC(int x,int y,int xn,int yn){
			Insets insets=new Insets(2,2,2,2);
			return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
		}
		@Override
		public void loadValue(Map<String,String> attr) {
			if(attr==null) return;
			if(Boolean.parseBoolean(attr.get(ConfigConstants.AutoCreateTask))){
				autoCreateTaskCheckBox.setSelected(true);
			}else{
				autoCreateTaskCheckBox.setSelected(false);
			}
			if(Boolean.parseBoolean(attr.get(ConfigConstants.AutoDetermineProtocol))){
				autoDetermineProtocol.setSelected(true);
			}else{
				autoDetermineProtocol.setSelected(false);
			}
			
			TaskXmlResolver taskXmlResolver=
					(com.sx.mmt.internal.task.TaskXmlResolver) SpringBeanUtil.getBean("taskXmlResolver");
			createTaskType.removeAllItems();
			for(String name:taskXmlResolver.getTasks().keySet()){
				createTaskType.addItem(name);
			}
			String tasktypeselect=attr.get(ConfigConstants.CreateTaskType);
			for(String name:taskXmlResolver.getTasks().keySet()){
				if(name.equals(tasktypeselect)){
					createTaskType.setSelectedItem(tasktypeselect);
				}
			}
			listenerPort.setValue(Integer.parseInt(
					attr.get(ConfigConstants.ListenerPort)));
			
		}

		@Override
		public Map<String,String> getValue() {
			Map<String,String> attr=new HashMap<String,String>();
			attr.put(ConfigConstants.AutoCreateTask, 
					String.valueOf(autoCreateTaskCheckBox.isSelected()));
			attr.put(ConfigConstants.AutoDetermineProtocol, 
					String.valueOf(autoDetermineProtocol.isSelected()));
			attr.put(ConfigConstants.CreateTaskType, 
					createTaskType.getSelectedItem().toString());
			attr.put(ConfigConstants.ListenerPort, listenerPort.getValue().toString());
			return attr;
		}	
	}
	
	public interface ConfigPanel{
		void loadValue(Map<String,String> attr);
		Map<String,String> getValue();
	}

}