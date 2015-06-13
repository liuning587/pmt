package com.sx.mmt.swingUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.apache.commons.lang3.StringUtils;

import com.sx.mmt.constants.AppParameterDao;
import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectXmlResolver;
import com.sx.mmt.internal.task.TaskGroup;
import com.sx.mmt.internal.task.TaskGroupDao;
import com.sx.mmt.internal.task.TaskImpl;
import com.sx.mmt.internal.task.TaskXmlResolver;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class TaskManagerBodyView extends JScrollPane{

	private static final long serialVersionUID = 1L;
	private JTable taskListTable;
	private AbstractTableModel tableModel;
	private List<MySortHead> heads;

	private JPopupMenu tablePopupMenu;

	private JMenuItem startItem;
	private JMenuItem restartItem;
	private JMenuItem stopItem;
	private JMenuItem deleteItem;
	private JMenu changeTaskMenu;
	private JMenu moveToMenu;
	private JMenu addressShow;
	
	public TaskManagerBodyView(){
		
		ini();
		setAttribute();
		setListener();
		loadMenuChangeTaskMenu();
		loadMoveToMenu();
		loadAddressShowMenu();
	}
	
	public void reloadTableHead(){
		tableModel=new MyDefaultTableModel();
		taskListTable.setModel(tableModel);
		setColumnWidth();
		TaskManagerController taskManagerController=(TaskManagerController) 
				SpringBeanUtil.getBean("taskManagerController");
		taskManagerController.loadTaskTable();
	}
	
	private void setColumnWidth(){
		taskListTable.getColumn(ViewConstants.TaskId).setMinWidth(110);
		taskListTable.getColumn(ViewConstants.IsOnline).setMinWidth(60);
		taskListTable.getColumn(ViewConstants.TerminalAddress).setMinWidth(110);
		taskListTable.getColumn(ViewConstants.TaskStatus).setMinWidth(60);
		taskListTable.getColumn(ViewConstants.TaskType).setMinWidth(100);
		taskListTable.getColumn(ViewConstants.CreateTime).setMinWidth(150);
		taskListTable.getColumn(ViewConstants.FinishTime).setMinWidth(150);
		taskListTable.getColumn(ViewConstants.ActionNow).setMinWidth(150);
		taskListTable.getColumn(ViewConstants.TerminalReturn).setMinWidth(150);
		taskListTable.getColumn(ViewConstants.TaskStep).setMinWidth(120);
	}
	public void ini(){
		
		heads=new ArrayList<MySortHead>();
		tableModel=new MyDefaultTableModel();
		
		taskListTable = new JTable(tableModel){
			private static final long serialVersionUID = 1L;
			@Override
			public TableCellRenderer getCellRenderer(int row, int column){
				return new RoutineColor();
			}
			@Override
			public String getToolTipText(MouseEvent e) {
	            String tip = null;
	            int column = columnAtPoint(e.getPoint());
	            int row=rowAtPoint(e.getPoint());
	            if(column==7){
	            	String tmp=(String) this.getValueAt(row, column);
	            	if(!StringUtils.isBlank(tmp)){
	            		String[] list=tmp.split(";");
	            		StringBuilder sb=new StringBuilder();
	            		sb.append("<html>");
	            		for(String s:list){
	            			sb.append("<p style='font-size:12px;'>").append(s).append("</p>");
	            		}
	            		sb.append("</html>");
	            		tip=sb.toString();
	            	}
	            }
	            return tip;
	        }
		};

		tablePopupMenu=new JPopupMenu();
		startItem=new JMenuItem("开始");
		stopItem=new JMenuItem("停止");
		restartItem=new JMenuItem("重新开始");
		deleteItem=new JMenuItem("删除");
		changeTaskMenu=new JMenu("更改任务类型");
		moveToMenu=new JMenu("移动任务到");
		addressShow=new JMenu("查看");
	}
	
	public void setAttribute(){
		taskListTable.setFont(ViewConstants.TextFont);
		taskListTable.getTableHeader().setFont(ViewConstants.TitleFont);
		taskListTable.setRowHeight(20);
		taskListTable.setRowMargin(2);
		taskListTable.getTableHeader().setReorderingAllowed(false);
		taskListTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
		taskListTable.setFocusable(false);
		taskListTable.setDoubleBuffered(true);
		heads.add(new MySortHead(0,"id",0));
		heads.add(new MySortHead(1,"terminalAddress",0));
		heads.add(new MySortHead(2,"terminalAddress",0));
		heads.add(new MySortHead(3,"taskName",0));
		heads.add(new MySortHead(4,"taskStatus",0));
		heads.add(new MySortHead(5,"createTime",0));
		heads.add(new MySortHead(6,"finishTime",0));
		heads.add(new MySortHead(7,"actionNow",0));
		heads.add(new MySortHead(8,"terminalReturn",0));
		heads.add(new MySortHead(9,"currentStepIndex",0));
		tablePopupMenu.setPopupSize(160, 200);
		startItem.setIconTextGap(20);
		stopItem.setIconTextGap(20);
		restartItem.setIconTextGap(20);
		deleteItem.setIconTextGap(20);
		changeTaskMenu.setIconTextGap(20);
		moveToMenu.setIconTextGap(20);
		addressShow.setIconTextGap(20);
		tablePopupMenu.add(startItem);
		tablePopupMenu.add(stopItem);
		tablePopupMenu.add(restartItem);
		tablePopupMenu.add(deleteItem);
		tablePopupMenu.addSeparator();
		tablePopupMenu.add(changeTaskMenu);
		tablePopupMenu.addSeparator();
		tablePopupMenu.add(moveToMenu);
		tablePopupMenu.addSeparator();
		tablePopupMenu.add(addressShow);
		taskListTable.setComponentPopupMenu(tablePopupMenu);
		taskListTable.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setColumnWidth();
		this.setBorder(new EmptyBorder(5, 5, 5, 15));				
		this.setViewportView(taskListTable);
		
	}
	
	public void setListener(){
		final TaskManagerController taskManagerController=(TaskManagerController) 
				SpringBeanUtil.getBean("taskManagerController");
		taskListTable.getTableHeader().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getClickCount()==1){
					taskListTable.getTableHeader().removeMouseListener(this);
					taskManagerController.tableSort(e);
					taskListTable.getTableHeader().addMouseListener(this);
				}
			}
		});
		
		startItem.addMouseListener(new MouseAdapter() {		
			@Override
			public void mousePressed(MouseEvent e) {
				taskManagerController.startTaskHandler(e);
			}
		});
		
		stopItem.addMouseListener(new MouseAdapter() {		
			@Override
			public void mousePressed(MouseEvent e) {
				taskManagerController.stopTaskHandler(e);
			}
		});
		
		restartItem.addMouseListener(new MouseAdapter() {		
			@Override
			public void mousePressed(MouseEvent e) {
				taskManagerController.restartTaskHandler(e);
			}
		});
		
		deleteItem.addMouseListener(new MouseAdapter() {		
			@Override
			public void mousePressed(MouseEvent e) {
				taskManagerController.deleteTaskHandler(e);
			}
		});
	}
	
	public void loadAddressShowMenu(){
		JCheckBoxMenuItem decimalitem=new JCheckBoxMenuItem(ViewConstants.Decimal);
		JCheckBoxMenuItem addressSplititem=new JCheckBoxMenuItem(ViewConstants.AddressSplit);
		decimalitem.setIconTextGap(20);
		addressSplititem.setIconTextGap(20);
		decimalitem.setFont(ViewConstants.TextFont);
		addressSplititem.setFont(ViewConstants.TextFont);
		addressShow.add(decimalitem);
		addressShow.add(addressSplititem);
		//加载值
		String addressShowDecimal=AppParameterDao.getValue(ConfigConstants.ViewAddressTableDecimal);
		String addressShowsplit=AppParameterDao.getValue(ConfigConstants.ViewAddressTableAddressSplit);
		if(ViewConstants.Decimal.equals(addressShowDecimal)){
			decimalitem.setSelected(true);
		}
		if(ViewConstants.AddressSplit.equals(addressShowsplit)){
			addressSplititem.setSelected(true);
		}
		decimalitem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				int state = e.getStateChange();
            	TaskManagerController taskManagerController=
            			(TaskManagerController)SpringBeanUtil.getBean("taskManagerController");
				if(state==ItemEvent.SELECTED){
					AppParameterDao.update(
							ConfigConstants.ViewAddressTableDecimal, ViewConstants.Decimal);
				}else{
					AppParameterDao.update(
							ConfigConstants.ViewAddressTableDecimal, ViewConstants.Hexadecimal);
				}
				taskManagerController.loadTaskTable();
			}
		});
		addressSplititem.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				int state = e.getStateChange();
            	TaskManagerController taskManagerController=
            			(TaskManagerController)SpringBeanUtil.getBean("taskManagerController");
				if(state==ItemEvent.SELECTED){
					AppParameterDao.update(
							ConfigConstants.ViewAddressTableAddressSplit, ViewConstants.AddressSplit);

				}else{
					AppParameterDao.update(
							ConfigConstants.ViewAddressTableAddressSplit, "No_split");
				}
				taskManagerController.loadTaskTable();
			}
		});
		
		
	}
	
	public void loadMenuChangeTaskMenu(){
		changeTaskMenu.removeAll();
		final TaskManagerController taskManagerController=(TaskManagerController) 
				SpringBeanUtil.getBean("taskManagerController");
		TaskXmlResolver taskXmlResolver=
				(TaskXmlResolver)SpringBeanUtil.getBean("taskXmlResolver");
		for(final String s:taskXmlResolver.getTasks().keySet()){
			JMenuItem item=new JMenuItem(s);
			item.setIconTextGap(20);
			item.addMouseListener(new MouseAdapter() {		
				@Override
				public void mousePressed(MouseEvent e) {
					taskManagerController.changeTaskTypeHandler(s);
				}
			});
			changeTaskMenu.add(item);
		}
	}
	
	public void loadMoveToMenu(){
		moveToMenu.removeAll();
		TaskManagerController taskManagerController=(TaskManagerController) 
				SpringBeanUtil.getBean("taskManagerController");
		TaskGroupDao taskGroupDao=
				(TaskGroupDao)SpringBeanUtil.getBean("taskGroupDao");
		TaskGroup tgRoot=taskGroupDao.get("000");
		loadChild(tgRoot,moveToMenu,taskManagerController);
		
	}
	
	private void loadChild(TaskGroup node,JMenu menu,final TaskManagerController taskManagerController){
		for(final TaskGroup child:taskManagerController.getTaskGroupDao().getChildren(node.getTaskTag())){
			if(taskManagerController.getTaskGroupDao().isLeaf(child.getTaskTag())){
				JMenuItem menuItem=new JMenuItem(child.getName());
				menuItem.setIconTextGap(20);
				menuItem.addMouseListener(new MouseAdapter() {		
					@Override
					public void mousePressed(MouseEvent e) {
						taskManagerController.moveToMenuHandler(child);
					}
				});
				menu.add(menuItem);
			}else{
				JMenu nodeMenu=new JMenu(child.getName());
				nodeMenu.setIconTextGap(20);
				menu.add(nodeMenu);
				loadChild(child,nodeMenu,taskManagerController);
			}
		}
	}

	public JTable getTaskListTable() {
		return taskListTable;
	}

	public void setTaskListTable(JTable taskListTable) {
		this.taskListTable = taskListTable;
	}

	public AbstractTableModel getTableModel() {
		return tableModel;
	}

	public void setTableModelne(AbstractTableModel tableModel) {
		this.tableModel= tableModel;
	}

	public JPopupMenu getTablePopupMenu() {
		return tablePopupMenu;
	}

	public void setTablePopupMenu(JPopupMenu tablePopupMenu) {
		this.tablePopupMenu = tablePopupMenu;
	}
	
	
	public List<MySortHead> getHeads() {
		return heads;
	}

	public void setHeads(List<MySortHead> heads) {
		this.heads = heads;
	}

	

	public JMenu getChangeTaskMenu() {
		return changeTaskMenu;
	}

	public void setChangeTaskMenu(JMenu changeTaskMenu) {
		this.changeTaskMenu = changeTaskMenu;
	}

	public JMenu getMoveToMenu() {
		return moveToMenu;
	}

	public void setMoveToMenu(JMenu moveToMenu) {
		this.moveToMenu = moveToMenu;
	}



	public class MySortHead{
		private int columnIndex;
		private String sortName;
		private int sortStatu;
		
		public MySortHead(int columnIndex, String sortName, int sortStatu) {
			super();
			this.columnIndex = columnIndex;
			this.sortName = sortName;
			this.sortStatu = sortStatu;
		}
		public int getColumnIndex() {
			return columnIndex;
		}
		public void setColumnIndex(int columnIndex) {
			this.columnIndex = columnIndex;
		}
		public int getSortStatu() {
			return sortStatu;
		}
		public void setSortStatu(int sortStatu) {
			this.sortStatu = sortStatu;
		}
		public String getSortName() {
			return sortName;
		}
		public void setSortName(String sortName) {
			this.sortName = sortName;
		}
		
	}
	
	//放置表格颜色
	class RoutineColor extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value,
				boolean isSelected, boolean hasFocus, int row, int column) {
			
			Component reder=super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);
			if (row % 2 == 0) {
				// 设置偶数行的背景颜色
				setBackground(new Color(255,255,235));
			} else {
				// 基数行的背景颜色
				setBackground(new Color(255, 255, 255));
			}
			
			if(isSelected){
				setBackground(new Color(213, 235, 243));
			}
			
			if(column==0){
				String c3=(String) table.getValueAt(row, 4);
				if(ViewConstants.RUNNING.equals(c3)){
					setIcon(new ImageIcon(getClass().getResource("/img/run.png")));
				}
				if(ViewConstants.NEW.equals(c3)){
					setIcon(new ImageIcon(getClass().getResource("/img/new.png")));
				}
				if(ViewConstants.STOPED.equals(c3)){
					setIcon(new ImageIcon(getClass().getResource("/img/stopsmall.png")));
				}
				if(ViewConstants.FINISHED.equals(c3)){
					setIcon(new ImageIcon(getClass().getResource("/img/finish.png")));
				}
				if(ViewConstants.FAILED.equals(c3)){
					setIcon(new ImageIcon(getClass().getResource("/img/fail.png")));
				}
				if(ViewConstants.WAITING.equals(c3)){
					setIcon(new ImageIcon(getClass().getResource("/img/waiting.png")));
				}
				setForeground(Color.blue);
			}
			
			
			
			if(column==2){
				if(ViewConstants.Online.equals(value)){
					setForeground(new Color(0,100,0));
					setIcon(new ImageIcon(getClass().getResource("/img/conn.png")));
				}
				if(ViewConstants.Offline.equals(value)){
					setIcon(new ImageIcon(getClass().getResource("/img/disconn.png")));
				}
			}
			
			if(column==4){
				if(ViewConstants.FAILED.equals(value)) setForeground(Color.red);
				if(ViewConstants.FINISHED.equals(value)) setForeground(new Color(0,128,0));
				if(ViewConstants.RUNNING.equals(value)) setForeground(new Color(0,0,139));
				if(ViewConstants.NEW.equals(value)) setForeground(new Color(210,105,30));
				if(ViewConstants.STOPED.equals(value)) setForeground(new Color(128,0,0));
				if(ViewConstants.WAITING.equals(value)) setForeground(new Color(108,166,205));
			}
			
			if(column==9){
				if("默认分组".equals(value)){
					setForeground(new Color(0x99,0x32,0xcc));
				}
			}

//			/* 继承Label类的方法, 设置table的单元格对齐方式 */
//			setHorizontalAlignment((int) Component.LEFT_ALIGNMENT); // 水平居中
//			setHorizontalTextPosition((int) Component.CENTER_ALIGNMENT); // 垂直居中

			// table.getTableHeader().setBackground(new Color(206, 231,
			// 255));//设置表头的字体色
			
			return reder;
		}
	}
	
	
	//表格数据
	@SuppressWarnings("rawtypes")
	public class MyDefaultTableModel extends AbstractTableModel{
		private static final long serialVersionUID = 1L;
		private List<Vector> data;
		private Map<String,Integer> terminalList=new ConcurrentHashMap<String,Integer>();
		private List<String> columnNames;
		private TaskManager taskManager;
		private ConnectXmlResolver connectXmlResolver;
		private TaskXmlResolver taskXmlResolver;
		private TaskGroupDao taskGroupDao;
		public MyDefaultTableModel(){
			connectXmlResolver=(ConnectXmlResolver) 
					SpringBeanUtil.getBean("connectXmlResolver");
			taskManager=(TaskManager) 
					SpringBeanUtil.getBean("taskManager");
			taskXmlResolver=(TaskXmlResolver)SpringBeanUtil.getBean("taskXmlResolver");
			taskGroupDao=(TaskGroupDao)SpringBeanUtil.getBean("taskGroupDao");
			columnNames=new Vector<String>();
			columnNames.add(ViewConstants.TaskId);
			columnNames.add(ViewConstants.TerminalAddress);
			columnNames.add(ViewConstants.IsOnline);
			columnNames.add(ViewConstants.TaskType);
			columnNames.add(ViewConstants.TaskStatus);
			columnNames.add(ViewConstants.CreateTime);
			columnNames.add(ViewConstants.FinishTime);
			columnNames.add(ViewConstants.ActionNow);
			columnNames.add(ViewConstants.TerminalReturn);
			columnNames.add(ViewConstants.TaskStep);
			columnNames.add(ViewConstants.GroupTag);
			data=new Vector<Vector>();
		}
		
		
		public synchronized void setData(List<TaskImpl> tasks) {
			data.clear();
			terminalList.clear();
			Map<String,String> onlineList=taskManager.getOnlineList();
			Map<String,TaskImpl> taskcache=taskManager.getTaskcache();
			int index=0;
			for(TaskImpl task:tasks){
				Vector row=new Vector();
				String isOnline=onlineList.get(task.getId());
				if(StringUtils.isBlank(isOnline)){
					isOnline=ViewConstants.Unavailable;
				}
				TaskImpl showtask=task;
				if(taskcache!=null && taskcache.containsKey(task.getId())){
					showtask=taskcache.get(task.getId());
				}
				row.add(showtask.getId());
				terminalList.put(showtask.getId(),index);
				index++;
				row.add(decorateTerminalAddress(showtask.getTerminalAddress()));
				row.add(isOnline);
				row.add(showtask.getTaskName());
				row.add(showtask.getTaskStatus());
				row.add(DateTool.getDateString(showtask.getCreateTime()));
				row.add(DateTool.getDateString(showtask.getFinishTime()));
				row.add(showtask.getActionNow());
				row.add(showtask.getTerminalReturn());
				try{
					row.add(taskXmlResolver.getTasks().get(showtask.getTaskName())
							.getCommands().get(showtask.getCurrentStepIndex()));
				}catch(Exception e){
					row.add("");
				}
				try{
					row.add(taskGroupDao.get(showtask.getTaskGroupTag()).getName());
				}catch(Exception e){
					row.add("");
				}
				row.add(showtask.getAdditionalParam1());
				row.add(showtask.getAdditionalParam2());
				row.add(showtask.getAdditionalParam3());
				data.add(row);
			}
			ActionNowDisplay.refreshTable();
		}

		@Override
		public String getColumnName(int column){
			return columnNames.get(column);
		}
		
		public boolean isTerminalInTable(String terminalAddress){
			return terminalList.containsKey(terminalAddress);
		}
		
		public String decorateTerminalAddress(String address){
			String addressShowDecimal=AppParameterDao.getValue(ConfigConstants.ViewAddressTableDecimal);
			String addressShowsplit=AppParameterDao.getValue(ConfigConstants.ViewAddressTableAddressSplit);
			String terminalAddressString=new String(address);
			//对地址进行显示修饰
			if(terminalAddressString.length()==8 && 
					addressShowDecimal.equals(ViewConstants.Decimal)){
				String tmp=StringUtils.leftPad(String.valueOf(
						Integer.parseInt(terminalAddressString.substring(4), 16)), 5,'0');
				terminalAddressString=terminalAddressString.substring(0, 4)+tmp;
			}
			if(addressShowsplit.equals(ViewConstants.AddressSplit)){
				terminalAddressString=terminalAddressString.substring(0, 4)+"-"+
						terminalAddressString.substring(4);
			}
			return terminalAddressString;
		}
		
		
		
		@SuppressWarnings("unchecked")
		public synchronized void updateValue(TaskImpl task){
			Integer index=terminalList.get(task.getId());
			if(index!=null){
				Map<String,String> onlineList=taskManager.getOnlineList();
				String isOnline=onlineList.get(task.getId());
				if(StringUtils.isBlank(isOnline)){
					isOnline=ViewConstants.Unavailable;
				}
				Vector v=data.get(index);
				v.set(2, isOnline);
				v.set(3, task.getTaskName());
				v.set(4, task.getTaskStatus());
				if(task.getFinishTime()!=null){
					v.set(6, DateTool.getDateString(task.getFinishTime()));
				}
				v.set(7, task.getActionNow());
				v.set(8, task.getTerminalReturn());
				try{
					v.set(9,taskXmlResolver.getTasks().
							get(task.getTaskName()).getCommands().get(task.getCurrentStepIndex()));
				}catch(Exception e){
					v.set(9, "setp out of boundary");
				}
				ActionNowDisplay.refreshTable();
			}
	
		}
		@Override
		public boolean isCellEditable(int row, int column)
        {
            return false;
        }

		@Override
		public int getRowCount() {
			if(data!=null){
				return data.size();
			}else{
				return 0;
			}
			
		}

		@Override
		public int getColumnCount() {
			return columnNames.size();
		}
		
		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			if(data!=null){
				if(rowIndex>=data.size()){
					return "";
				}else{
					if(columnIndex>=data.get(rowIndex).size()){
						return "";
					}else{
						return data.get(rowIndex).get(columnIndex);
					}
				}
			}else{
				return null;
			}
		}
	}
	

	
}