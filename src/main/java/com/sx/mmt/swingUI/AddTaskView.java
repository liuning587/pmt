package com.sx.mmt.swingUI;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.sx.mmt.constants.AppParameterDao;
import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.task.TaskXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;
import com.sx.mmt.swingUI.AddTaskController.Address;
public class AddTaskView extends JDialog{

	private static final long serialVersionUID = 1L;
	private JTextField districtField;
	private JTextField addressStart;
	private JTextField addressEnd;
	private JComboBox<String> addressFormat;
	private JButton addButton;
	private JButton importButton;
	private JButton configButton;
	private JButton confirmButton;
	private JButton cancelButton;
	private JComboBox<String> taskType;
	private JList<String> terminalList;
	private MyListModel terminalListModel;
	private JPopupMenu removeMenu;
	private JMenuItem removeItem;
	private static AddressImportConfigView addressImportConfigView;
	
	public AddTaskView(Frame owner){
		super(owner,"新建任务",true);
		ini();
		setAttribute();
		loadValue();
		setListener();
		
	}
	
	public void ini(){
		districtField=new JTextField();
		addressStart=new JTextField();
		addressEnd=new JTextField();
		addressFormat=new JComboBox<String>(new String[]{ViewConstants.Decimal,ViewConstants.Hexadecimal});
		addButton=new JButton("添加");
		importButton=new JButton("从文件导入");
		configButton=new JButton("导入配置");
		confirmButton=new JButton("确定");
		cancelButton=new JButton("取消");
		taskType=new JComboBox<String>();
		terminalListModel=new MyListModel();
		terminalList=new JList<String>(terminalListModel);
		removeMenu=new JPopupMenu();
		removeItem=new JMenuItem("移除终端");
		removeItem.setFont(ViewConstants.TextFont);
		removeMenu.add(removeItem);
	}
	
	public void setAttribute(){
		this.setMinimumSize(new Dimension(600, 500));
		this.setTitle("添加新任务");
		Container contentPane = getContentPane();
		GridBagLayout layout=new GridBagLayout();
		layout.columnWidths=new int[]{66,66,132,66,198,66};
		layout.rowHeights=new int[]{38,38,38,38,38,38,38,38,38,38};
		this.setLayout(layout);
		
		JScrollPane terminalListScrollPane=new JScrollPane();
		terminalListScrollPane.setPreferredSize(new Dimension(198, 180));
		terminalListScrollPane.setViewportView(terminalList);
		
		Insets insets=new Insets(2,2,2,2);
		contentPane.add(new JLabel("行政区划码"),getGBC(1,1,1,1));
		contentPane.add(districtField,getGBC(2,1,1,1));
		contentPane.add(new JLabel("起始地址"),getGBC(1,2,1,1));
		contentPane.add(new JLabel("结束地址"),getGBC(1,3,1,1));
		contentPane.add(new JLabel("地址格式"),getGBC(1,4,1,1));
		contentPane.add(addressStart,getGBC(2,2,1,1));
		contentPane.add(addressEnd,getGBC(2,3,1,1));
		contentPane.add(addressFormat,getGBC(2,4,1,1));
		contentPane.add(addButton,getGBC(2,5,1,1));
		contentPane.add(configButton,getGBC(1,6,1,1));
		contentPane.add(importButton,getGBC(2,6,1,1));
		contentPane.add(new JLabel("选择任务类型"),getGBC(2,8,2,1));
		contentPane.add(taskType,getGBC(3,8,2,1));
		contentPane.add(new JLabel("终端列表"),getGBC(4,1,1,1));
		contentPane.add(terminalListScrollPane,
				new GridBagConstraints(4,1,1,7,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0, 0, 0, 0),0,0));
		JPanel buttonPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		buttonPanel.add(confirmButton);
		buttonPanel.add(cancelButton);
		contentPane.add(buttonPanel,
				new GridBagConstraints(4,10,1,1,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0));
		
		for(java.awt.Component component:contentPane.getComponents()){
			component.setFont(ViewConstants.TextFont);
		}
		this.pack();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(getOwner());
		
	}
	
	private GridBagConstraints getGBC(int x,int y,int xn,int yn){
		Insets insets=new Insets(2,2,2,2);
		return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
	}
	
	
	public void setListener(){
		final AddTaskView me=this;
		final AddTaskController addTaskController=
				(AddTaskController) SpringBeanUtil.getBean("addTaskController");
		addTaskController.setAddTaskView(this);
		
		addButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addTaskController.addTaskHandler(e);
			}
        });
		
		configButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(addressImportConfigView==null){
					addressImportConfigView=new AddressImportConfigView(me);
				}
				addressImportConfigView.setVisible(true);
				addressImportConfigView.loadValue();
			}
        });
		
		importButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addTaskController.importTaskHandler(e);
			}
        });
		
		confirmButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addTaskController.confirmHandler(e);
			}
        });
		
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addTaskController.cancelHandler(e);
			}
        });
		
		terminalList.addMouseListener(new MouseAdapter() {		
			@Override
			public void mouseClicked(MouseEvent e) {
				addTaskController.popupMenu(e);
			}
		});
		
		removeItem.addMouseListener(new MouseAdapter() {		
			@Override
			public void mousePressed(MouseEvent e) {
				addTaskController.removeTerminal(e);
			}
		});
		
		addressFormat.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					AppParameterDao.update(ConfigConstants.ViewAddressFormat,
							String.valueOf(addressFormat.getSelectedIndex()));
				}
			}
		});
		
		taskType.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					AppParameterDao.update(ConfigConstants.ViewTaskType, 
							taskType.getSelectedItem().toString());
				}
			}
		});
		
	}
	
	public void loadValue(){
		TaskXmlResolver taskXmlResolver=
				(com.sx.mmt.internal.task.TaskXmlResolver) SpringBeanUtil.getBean("taskXmlResolver");
		for(String name:taskXmlResolver.getTasks().keySet()){
			taskType.addItem(name);
		}
		String tasktypeselect=AppParameterDao.getValue(ConfigConstants.ViewTaskType);
		taskType.setSelectedItem(tasktypeselect);
		addressFormat.setSelectedIndex(
				AppParameterDao.getIntValue(ConfigConstants.ViewAddressFormat));
	}

	

	public JTextField getDistrictField() {
		return districtField;
	}

	public void setDistrictField(JTextField districtField) {
		this.districtField = districtField;
	}

	public JTextField getAddressStart() {
		return addressStart;
	}

	public void setAddressStart(JTextField addressStart) {
		this.addressStart = addressStart;
	}

	public JTextField getAddressEnd() {
		return addressEnd;
	}

	public void setAddressEnd(JTextField addressEnd) {
		this.addressEnd = addressEnd;
	}

	public JComboBox<String> getAddressFormat() {
		return addressFormat;
	}

	public void setAddressFormat(JComboBox<String> addressFormat) {
		this.addressFormat = addressFormat;
	}

	public JComboBox<String> getTaskType() {
		return taskType;
	}

	public void setTaskType(JComboBox<String> taskType) {
		this.taskType = taskType;
	}

	public JList<String> getTerminalList() {
		return terminalList;
	}

	public void setTerminalList(JList<String> terminalList) {
		this.terminalList = terminalList;
	}

	

	public MyListModel getTerminalListModel() {
		return terminalListModel;
	}

	public void setTerminalListModel(MyListModel terminalListModel) {
		this.terminalListModel = terminalListModel;
	}

	public JPopupMenu getRemoveMenu() {
		return removeMenu;
	}

	public void setRemoveMenu(JPopupMenu removeMenu) {
		this.removeMenu = removeMenu;
	}

	public JMenuItem getRemoveItem() {
		return removeItem;
	}

	public void setRemoveItem(JMenuItem removeItem) {
		this.removeItem = removeItem;
	}
	
	public JButton getConfigButton() {
		return configButton;
	}

	public void setConfigButton(JButton configButton) {
		this.configButton = configButton;
	}


	@SuppressWarnings("rawtypes")
	public class MyListModel extends AbstractListModel{
		private static final long serialVersionUID = 1L;
		private Map<Long,Address> data=new ConcurrentHashMap<Long, Address>();
		private Vector<Address> vdata=new Vector<Address>();
		
		public void add(List<Address> adds){
			for(Address a:adds){
				data.put(a.getCode(), a);
			}
			for(Address a:adds){
				vdata.add(data.get(a.getCode()));
			}
		}
		public void remove(List<Address> adds){
			for(Address a:adds){
				data.remove(a.getCode());
			}
			vdata.removeAll(adds);
		}
		public Map<Long,Address> getData(){
			return data;
		}
		@Override
		public int getSize() {
			return vdata.size();
		}

		@Override
		public Address getElementAt(int index) {
			return vdata.get(index);
		}
		
		public void clear(){
			data.clear();
			vdata.clear();
		}
		
	}
	
	
}
