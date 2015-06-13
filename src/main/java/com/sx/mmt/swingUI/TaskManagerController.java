package com.sx.mmt.swingUI;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.sx.mmt.application.MainApp;
import com.sx.mmt.constants.AppParameterDao;
import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.EncodedDataSendingDelayQueue;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.connection.ConnectXmlResolver;
import com.sx.mmt.internal.connection.ConnectionManager;
import com.sx.mmt.internal.task.TaskGroup;
import com.sx.mmt.internal.task.TaskGroupDao;
import com.sx.mmt.internal.task.TaskImpl;
import com.sx.mmt.internal.task.TaskImplDao;
import com.sx.mmt.internal.task.command.CommandState;
import com.sx.mmt.internal.util.CSVUtil;
import com.sx.mmt.internal.util.DateTool;
import com.sx.mmt.internal.util.SpringBeanUtil;
import com.sx.mmt.swingUI.TaskManagerBodyView.MyDefaultTableModel;
import com.sx.mmt.swingUI.TaskManagerBodyView.MySortHead;
import com.sx.mmt.swingUI.cqdw.CQAddTaskView;

@Component
public class TaskManagerController {
	private TaskManagerLeftTreeView taskManagerLeftTreeView;
	private TaskManagerHeadView taskManagerHeadView;
	private TaskManagerBodyView taskManagerBodyView;
	private TaskManagerFootView taskManagerFootView;
	private TaskManagerView taskManagerView;
	public final Icon upIcon=new UpDownArrow(UpDownArrow.UP);
	public final Icon downIcon = new UpDownArrow(UpDownArrow.DOWN);
	private DefaultTableCellRenderer defaultHeaderRenderer;
	@Autowired
	private TaskGroupDao taskGroupDao;
	@Autowired
	private TaskImplDao taskImplDao;
	@Autowired
	private ConnectXmlResolver ConnectXmlResolver;
	@Autowired
	private TaskManager taskManager;
	@Autowired
	private EncodedDataSendingDelayQueue encodedDataSendingDelayQueue;
	@Autowired
	private ConnectionManager connectionManager;
	
	public void treePopMenuHandler(MouseEvent e){
		if(e.isPopupTrigger() && taskManagerLeftTreeView.getTaskGroupTree().getSelectionCount()>0){
			taskManagerLeftTreeView.getTaskGroupTreePopupMenu().setVisible(true);
			taskManagerLeftTreeView.getTaskGroupTreePopupMenu()
						.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	public void treeSelectionChange(TreeSelectionEvent e){
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)
				taskManagerLeftTreeView.getTaskGroupTree().getLastSelectedPathComponent();
		if(node!=null){
			loadTaskTable();
		}
	}
	
	/**
	 * 生成分组号
	 * @param node
	 * @return
	 */
	private String getNewGroupTag(DefaultMutableTreeNode node){
		int nodeMax=-1;
		for(int i=0;i<node.getChildCount();i++){
			DefaultMutableTreeNode child=
					(DefaultMutableTreeNode) node.getChildAt(i);
			String strid=((TaskGroup)child.getUserObject()).getTaskTag();
			strid=strid.substring(strid.length()-3, strid.length());
			int id=Integer.valueOf(strid);
			if(id>nodeMax){
				nodeMax=id;
			}
		}
		if(nodeMax>=999){return null;}
		String nodeid=StringUtils.leftPad(String.valueOf(nodeMax+1), 3, '0');
		return ((TaskGroup)node.getUserObject()).getTaskTag()+
				nodeid.substring(nodeid.length()-3, nodeid.length());
	}
	
	public void addANewgroupHandler(MouseEvent e){
		if(e.getClickCount()==1 &&
				e.getButton()==MouseEvent.BUTTON1){
			String inputValue = JOptionPane.showInputDialog("输入分组名");
			if(!StringUtils.isBlank(inputValue)){
				DefaultMutableTreeNode node=(DefaultMutableTreeNode)
						taskManagerLeftTreeView.getTaskGroupTree().getLastSelectedPathComponent();
				TaskGroup tg=new TaskGroup();
				tg.setName(inputValue);
				tg.setNameWithNumber(inputValue+"(0)");
				tg.setParentTag(((TaskGroup) node.getUserObject()).getTaskTag());
				String newGroupTag=getNewGroupTag(node);
				if(newGroupTag==null){
					JOptionPane.showMessageDialog(taskManagerBodyView, "超过最大分组数量支持", "警告", 1);
					return;
				}
				tg.setTaskTag(newGroupTag);
				DefaultMutableTreeNode childNode=new DefaultMutableTreeNode(tg);
				node.add(childNode);
				taskGroupDao.save(tg);
				loadTree();
				taskManagerBodyView.loadMoveToMenu();
			}
		}
	}
	
	public void renameAGroupHandler(MouseEvent e){
		if(e.getClickCount()==1 &&
				e.getButton()==MouseEvent.BUTTON1){
			DefaultMutableTreeNode node=(DefaultMutableTreeNode)
					taskManagerLeftTreeView.getTaskGroupTree().getLastSelectedPathComponent();
			String selectedGroupTag=((TaskGroup)node.getUserObject()).getTaskTag();
			if(selectedGroupTag.equals("000000") || selectedGroupTag.equals("000")) return;
			String tag=node.toString();
			tag=tag.substring(0, tag.indexOf("("));
			String inputValue = JOptionPane.showInputDialog("输入分组",tag);
			
			if(inputValue!=null){
				TaskGroup tg=(TaskGroup) node.getUserObject();
				tg.setName(inputValue);
				node.setUserObject(tg);
				taskGroupDao.update(tg);
				loadTree();
				taskManagerBodyView.loadMoveToMenu();
			}
		}
	}
	
	public void deleteAGroupHandler(MouseEvent e){
		if(e.getButton()==MouseEvent.BUTTON1){
			DefaultMutableTreeNode node=(DefaultMutableTreeNode)
					taskManagerLeftTreeView.getTaskGroupTree().getLastSelectedPathComponent();
			node.removeFromParent();
			String selectedGroupTag=((TaskGroup)node.getUserObject()).getTaskTag();
			if(selectedGroupTag.equals("000000") || selectedGroupTag.equals("000")) return;
			String tag=node.toString();
			int choose=-1;
			if(Integer.parseInt(tag.substring(tag.indexOf("(")+1, tag.indexOf(")")))!=0){
				taskManagerLeftTreeView.getTaskGroupTreePopupMenu().setVisible(false);
				choose=JOptionPane.showConfirmDialog(taskManagerBodyView,
						"删除分组将同时删除分组下的所有任务，确定继续?", "警告", 2);
			}else{
				choose=0;
			}
			if(choose==0){
				taskImplDao.deleteAllTask(selectedGroupTag);
				taskGroupDao.delete((TaskGroup)node.getUserObject());
				loadTree();
				loadTaskTable();
				taskManagerBodyView.loadMoveToMenu();
			}
		}
	}
	
	//新建任务窗口
	public void addNewTaskHandler(ActionEvent e){
		String connectionType=ConnectXmlResolver.getInUseConfig().getName();
		if(connectionType.equals(ViewConstants.StationCQDareWay) ||
				connectionType.equals(ViewConstants.StationCQDareWaySocket)){
			CQAddTaskView addTaskView=new CQAddTaskView(taskManagerView);
			addTaskView.setVisible(true);
		}else{
			AddTaskView addTaskView=new AddTaskView(taskManagerView);
			addTaskView.setVisible(true);
		}
		
	}
	
	
	
	
	//开始任务
	public void startTaskHandler(MouseEvent e){
		final int[] select=taskManagerBodyView.getTaskListTable().getSelectedRows();
		taskImplDao.execute(new Runnable() {
			
			@Override
			public void run() {
				for(int i:select){
					String id=(String)taskManagerBodyView
							.getTaskListTable().getValueAt(i, 0);
					taskManager.removeFromCache(id);
					taskImplDao.changeTaskStatus(new String[]{ViewConstants.NEW
							,ViewConstants.STOPED},ViewConstants.WAITING,id);
				}
				taskManager.updateCacheNotice();
				loadTree();
				loadTaskTable();
			}
		});

	}
	
	public void startAllTaskHandler(ActionEvent e){
		final String finalGroupTap=getDefaultGroupTag();
		taskImplDao.execute(new Runnable() {
			@Override
			public void run() {
				taskImplDao.changeAllTaskStatus(new String[]{ViewConstants.NEW
						,ViewConstants.STOPED},ViewConstants.WAITING,finalGroupTap);
				taskManager.updateCacheNotice();
				loadTree();
				loadTaskTable();
				
			}
		});
	}
	
	public void stopTaskHandler(MouseEvent e){
		
		final int[] select=taskManagerBodyView.getTaskListTable().getSelectedRows();
		taskImplDao.execute(new Runnable() {
			@Override
			public void run() {
				List<TaskImpl> tasks=Lists.newArrayList();
				for(int i:select){
					String id=(String)taskManagerBodyView.getTaskListTable().getValueAt(i, 0);
					TaskImpl task=taskManager.removeFromCache(id);
					if(task!=null){
						task.setTaskStatus(ViewConstants.STOPED);
						encodedDataSendingDelayQueue.removePacket(id);
						tasks.add(task);
					}else{
						taskImplDao.changeTaskStatus(new String[]{ViewConstants.RUNNING,ViewConstants.WAITING},
								ViewConstants.STOPED, id);
					}
					
				}
				taskImplDao.update(tasks);
				taskManager.updateCacheNotice();
				loadTree();
				loadTaskTable();
				
			}
		});

	}
	
	public void restartTaskHandler(MouseEvent e){
		final int[] select=taskManagerBodyView.getTaskListTable().getSelectedRows();
		taskImplDao.execute(new Runnable() {
			
			@Override
			public void run() {
				List<TaskImpl> tasks=Lists.newArrayList();
				for(int i:select){
					String id=
							(String)taskManagerBodyView.getTaskListTable().getValueAt(i, 0);
					TaskImpl task=taskManager.removeFromCache(id);
					encodedDataSendingDelayQueue.removePacket(id);
					if(task==null){
						task=taskImplDao.getTaskImpl(id);
					}
					
					task.setStepStatus(CommandState.Start);
					task.setCurrentStepIndex(0);
					task.setTaskStatus(ViewConstants.WAITING);
					task.setNextActionTime(System.currentTimeMillis());
					task.setTerminalReturn("");
					task.setActionNow("");
					task.setPfc(0);
					task.setCounter(0L);
					task.setPacketIndex(0);
					task.setRetryCount(0);
					task.setPriority(10);
					tasks.add(task);
					
					ActionNowDisplay.update(task);
				}
				taskImplDao.update(tasks);
				taskManager.updateCacheNotice();
				loadTree();
			}
		});

		
		
	}
	
	public void stopAllTaskHandler(ActionEvent e){
		final String finalGroupTap=getDefaultGroupTag();
		taskImplDao.execute(new Runnable() {
			@Override
			public void run() {
				taskManager.removeAllFromCache(finalGroupTap);
				taskImplDao.changeAllTaskStatus(new String[]{ViewConstants.RUNNING,ViewConstants.WAITING}
				,ViewConstants.STOPED, finalGroupTap);
				loadTree();
				loadTaskTable();
				
			}
		});
	}
	
	private String getDefaultGroupTag(){
		DefaultMutableTreeNode node=(DefaultMutableTreeNode)
				taskManagerLeftTreeView.getTaskGroupTree().getLastSelectedPathComponent();
		if(node==null){
			node=((DefaultMutableTreeNode)taskManagerLeftTreeView.getTreeModel().getRoot()).getFirstLeaf();
			JOptionPane.showMessageDialog(taskManagerBodyView, "未选中任何分组，将只对默认分组操作", "警告", 1);
		}
		String groupTag=((TaskGroup)node.getUserObject()).getTaskTag();
		return groupTag;
	}
	
	public void deleteTaskHandler(MouseEvent e){
		taskManagerBodyView.getTablePopupMenu().setVisible(false);
		int choose=JOptionPane.showConfirmDialog(taskManagerBodyView, "确定删除选中的任务？","警告",2);
		if(choose==0){
			final int[] select=taskManagerBodyView.getTaskListTable().getSelectedRows();
			taskImplDao.execute(new Runnable() {			
				@Override
				public void run() {
					for(int i:select){
						String id=(String)taskManagerBodyView
								.getTaskListTable().getValueAt(i, 0);
						taskManager.removeFromCache(id);
						encodedDataSendingDelayQueue.removePacket(id);
						taskImplDao.deleteTask(id);
					}
					taskManager.updateCacheNotice();
					loadTree();
					loadTaskTable();
				}
			});
		}
	}
	
	public void deleteAllTaskHandler(ActionEvent e){
		int choose=JOptionPane.showConfirmDialog(taskManagerBodyView, "确定删除所选分组的所有任务？","警告",2);
		if(choose==0){
			final String finalGroupTap=getDefaultGroupTag();
			taskImplDao.execute(new Runnable() {
				@Override
				public void run() {
					taskManager.removeAllFromCache(finalGroupTap);
					taskImplDao.deleteAllTask(finalGroupTap);
					loadTree();
					loadTaskTable();
				}
			});
		}
	}
	
	
	public void changeTaskTypeByGroupHandler(final String taskType){
		taskImplDao.execute(new Runnable() {
			@Override
			public void run() {
				DefaultMutableTreeNode node=(DefaultMutableTreeNode)
						getTaskManagerLeftTreeView().getTaskGroupTree().getLastSelectedPathComponent();
				if(node==null){
					node=((DefaultMutableTreeNode)getTaskManagerLeftTreeView().getTreeModel().getRoot()).getFirstLeaf();
				}
				String groupTag=((TaskGroup)node.getUserObject()).getTaskTag();
				List<TaskImpl> tasks=taskImplDao.getTaskList(groupTag, 1, Integer.MAX_VALUE, "", "", "");
				int size=tasks.size();
				for(int i=0;i<size;i++){
					TaskImpl task=tasks.get(i);
					String terminalAddress=task.getTerminalAddress();
					taskManager.removeFromCache(terminalAddress);
					task.setTaskName(taskType);
					task.setStepStatus(CommandState.Start);
					task.setTaskStatus(ViewConstants.STOPED);
					task.setCurrentStepIndex(0);
					task.setPfc(0);
					task.setTerminalReturn("");
					task.setActionNow("");
					task.setCounter(0L);
					task.setPacketIndex(0);
					task.setRetryCount(0);
					task.setPriority(10);
					task.setNextActionTime(System.currentTimeMillis());
					tasks.add(task);
					encodedDataSendingDelayQueue.removePacket(terminalAddress);
					ActionNowDisplay.update(task);
				}
				taskImplDao.update(tasks);
				taskManager.updateCacheNotice();
				loadTree();
			}
		});
	}
	
	//变更任务
	public void changeTaskTypeHandler(final String taskType){
		final int[] select=taskManagerBodyView.getTaskListTable().getSelectedRows();
		taskImplDao.execute(new Runnable() {
			@Override
			public void run() {
				List<TaskImpl> tasks=Lists.newArrayList();
				for(int i:select){
					String terminalAddress=
							(String)taskManagerBodyView.getTaskListTable().getValueAt(i, 0);
					TaskImpl task=taskManager.removeFromCache(terminalAddress);
					if(task==null){
						task=taskImplDao.getTaskImpl(terminalAddress);
					}
					task.setTaskName(taskType);
					task.setStepStatus(CommandState.Start);
					task.setTaskStatus(ViewConstants.STOPED);
					task.setCurrentStepIndex(0);
					task.setPfc(0);
					task.setTerminalReturn("");
					task.setActionNow("");
					task.setCounter(0L);
					task.setPacketIndex(0);
					task.setRetryCount(0);
					task.setPriority(10);
					task.setNextActionTime(System.currentTimeMillis());
					tasks.add(task);
						
					encodedDataSendingDelayQueue.removePacket(terminalAddress);
					ActionNowDisplay.update(task);
				}
				taskImplDao.update(tasks);
				taskManager.updateCacheNotice();
				loadTree();
			}
		});
	}
	
	/**
	 * 变更所属分组
	 * @param node
	 */
	public void moveToMenuHandler(final TaskGroup node){
		final int[] select=taskManagerBodyView.getTaskListTable().getSelectedRows();
		taskImplDao.execute(new Runnable() {
			@Override
			public void run() {
				for(int i:select){
					String id=
							(String)taskManagerBodyView.getTaskListTable().getValueAt(i, 0);
					TaskImpl task=taskImplDao.getTaskImpl(id);
					task.setTaskGroupTag(node.getTaskTag());
					taskImplDao.update(Lists.newArrayList(task));
					ActionNowDisplay.update(task);
				}
				loadTree();
				
			}
		});

	}
	
	
	/**
	 * 显示报文监控面板
	 * @param e
	 */
	public void showTerminalHandler(ActionEvent e){
		ShowTerminalController showTerminalController=
				(ShowTerminalController)SpringBeanUtil.getBean("showTerminalController");
		showTerminalController.show();
	}
	
	
	/**
	 * 导出文件
	 * @param e
	 */
	public void exportTaskHandler(ActionEvent e){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("导出当前分组任务到文件");
		String path=AppParameterDao.getValue(ConfigConstants.FileDefaultExportPath);
		File defaultPath;
		if(!StringUtils.isBlank(path)){
			defaultPath=new File(path);
		}else{
			defaultPath=new File(System.getProperty("user.dir"));
		}

		if(defaultPath.isDirectory()){
			fileChooser.setCurrentDirectory(defaultPath);
		}
		
		//设置文件类型过滤
		fileChooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file) {
				if(file.isDirectory() || file.getName().toLowerCase().lastIndexOf(".csv")>0){
					return true;
				}else{
					return false;
				}
			}
			@Override
			public String getDescription() {
				return ".csv";
			}
		});
		int i = fileChooser.showSaveDialog((java.awt.Component) e.getSource());  
        if(i==JFileChooser.APPROVE_OPTION)  
        {
            File selectedFile = fileChooser.getSelectedFile();
            if(!selectedFile.getName().endsWith(".csv") && 
            		!selectedFile.getName().endsWith(".CSV")){
            	selectedFile=new File(selectedFile.getAbsolutePath()+".csv");
            }
            List<String> dataList=new ArrayList<String>();
            //添加表头
            StringBuilder sb=new StringBuilder();
            sb.append(ViewConstants.TaskId).append(",");
        	sb.append(ViewConstants.TerminalAddress).append(",")
        	.append(ViewConstants.TaskType).append(",")
        	.append(ViewConstants.TaskStatus).append(",")
        	.append(ViewConstants.CreateTime).append(",")
        	.append(ViewConstants.FinishTime).append(",")
        	.append(ViewConstants.ActionNow).append(",")
        	.append(ViewConstants.TerminalReturn).append(",")
        	.append(ViewConstants.GroupTag).append(",")
        	.append("参数I").append(",")
        	.append("参数II").append(",")
        	.append("参数III").append(",");
        	dataList.add(sb.toString());
        	
        	//添加数据
    		DefaultMutableTreeNode node=(DefaultMutableTreeNode)
    				taskManagerLeftTreeView.getTaskGroupTree().getLastSelectedPathComponent();
    		if(node==null){
    			node=(DefaultMutableTreeNode)taskManagerLeftTreeView.getTreeModel().getRoot();
    		}
    		String groupTag=((TaskGroup)node.getUserObject()).getTaskTag();
    		String filter=taskManagerLeftTreeView.getTaskSearchField().getText();
    		String column="";
    		String direction="";
    		if(defaultHeaderRenderer!=null){
    			MySortHead head=((SortHeaderRenderer)defaultHeaderRenderer).getSortHead();
    			if(head.getSortStatu()==1){
    				column=head.getSortName();
    				direction="asc";
    			}else if(head.getSortStatu()==2){
    				column=head.getSortName();
    				direction="desc";
    			}
    		}
    		
            List<TaskImpl> tasks=taskImplDao.getTaskList(groupTag, 1, Integer.MAX_VALUE,column,direction,filter);
            for(TaskImpl t:tasks){
            	sb=new StringBuilder();
            	TaskGroup tg=taskGroupDao.get(t.getTaskGroupTag());
            	sb.append(t.getId()).append(",");
            	String terminalAddressString=t.getTerminalAddress();
    			String addressShowDecimal=AppParameterDao.getValue(ConfigConstants.ViewAddressTableDecimal);
    			String addressShowsplit=AppParameterDao.getValue(ConfigConstants.ViewAddressTableAddressSplit);
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
    			
            	sb.append(terminalAddressString).append(",")
            	.append(t.getTaskName()).append(",")
            	.append(t.getTaskStatus()).append(",")
            	.append(DateTool.getDateString(t.getCreateTime())).append(",");
            	if(t.getFinishTime()!=null){
            		sb.append(DateTool.getDateString(t.getCreateTime())).append(",");
            	}else{
            		sb.append("").append(",");
            	}
            	
            	sb.append(t.getActionNow()).append(",")
            	.append(t.getTerminalReturn()).append(",")
            	.append(tg.getName()).append(",")
            	.append(t.getAdditionalParam1()).append(",")
            	.append(t.getAdditionalParam2()).append(",")
            	.append(t.getAdditionalParam3()).append(",");
            	dataList.add(sb.toString());
            }
            boolean isSucess=CSVUtil.exportCsv(selectedFile, dataList);
            if(isSucess){
            	JOptionPane.showMessageDialog(taskManagerBodyView, "保存成功");
            }else{
            	JOptionPane.showMessageDialog(taskManagerBodyView, "保存失败");
            }
            
            //设置默认路径
            if(!selectedFile.getParent().equals(defaultPath.getPath())){
            	AppParameterDao.update(ConfigConstants.FileDefaultExportPath, 
            			selectedFile.getParent());
            }
        }
	}
	
	public void helpHandler(ActionEvent e){
		HelpView helpView=new HelpView(taskManagerView);
		helpView.setVisible(true);
	}
	
	//显示配置面板
	public void configHandler(ActionEvent e){
		JDialog config=new ConfigView(taskManagerView);
		config.setVisible(true);
	}
	
	//退出程序
	public void exitHandler(ActionEvent e){
		taskManager.clearCache();
		AppParameterDao.update(ConfigConstants.ApplicationStartupWidth, 
		String.valueOf(taskManagerView.getWidth()));
		AppParameterDao.update(ConfigConstants.ApplicationStartupHeight, 
		String.valueOf(taskManagerView.getHeight()));
		taskManager.stopTaskManager();
		connectionManager.stopCurrentService();
		ActionNowDisplay.stop();
		taskManagerView.dispose();
		MainApp.dispose();
		System.exit(0);
	}
	
	public void firstPageHandler(ActionEvent e){
		taskManagerFootView.getPageNumberField().setText("1");
		loadTaskTable();
	}
	
	public void pageUpButton(ActionEvent e){
		int currentPage=Integer.parseInt(taskManagerFootView.getPageNumberField().getText());
		int totalPage=Integer.parseInt(taskManagerFootView.getTotalPage().getText());
		currentPage--;
		if(currentPage<1){
			currentPage=1;
		}
		if(currentPage>totalPage){
			currentPage=totalPage;
		}
		taskManagerFootView.getPageNumberField().setText(String.valueOf(currentPage));
		loadTaskTable();
	}
	
	public void pageDownButton(ActionEvent e){
		int currentPage=Integer.parseInt(taskManagerFootView.getPageNumberField().getText());
		currentPage++;
		int totalPage=Integer.parseInt(taskManagerFootView.getTotalPage().getText());
		if(currentPage>totalPage){
			currentPage=totalPage;
		}
		taskManagerFootView.getPageNumberField().setText(String.valueOf(currentPage));
		loadTaskTable();
	}
	
	public void lastPageButton(ActionEvent e){
		String totalPage=taskManagerFootView.getTotalPage().getText();
		taskManagerFootView.getPageNumberField().setText(totalPage);
		loadTaskTable();
	}
	
	public void numberPerPageChangeHandler(ItemEvent e){
		taskManagerFootView.getPageNumberField().setText("1");
		AppParameterDao.update(ConfigConstants.ViewPagePerTask, String.valueOf(e.getItem()));
		loadTaskTable();
	}
	
	
	public void gotoSpecifiedPage(KeyEvent e){
		String toPage=taskManagerFootView.getPageNumberField().getText();
		int totalPage=Integer.parseInt(taskManagerFootView.getTotalPage().getText());
		int page=1;
		try{
			page=Integer.parseInt(toPage);
		}catch(Exception ep){
			
		}
		if(page>=1 && page<=totalPage){
			loadTaskTable();
		}else{
			taskManagerFootView.getPageNumberField().setText("1");
		}
	}
	
	//表格排序
	public void tableSort(MouseEvent e){
		int columnIndex = taskManagerBodyView.getTaskListTable().getTableHeader().columnAtPoint(e.getPoint());
		if(columnIndex>9){
			return;
		}
		if(columnIndex==2){
			return;
		}
		MySortHead head=taskManagerBodyView.getHeads().get(columnIndex);
		head.setSortStatu(head.getSortStatu()+1);
		if(head.getSortStatu()>2){
			head.setSortStatu(0);
		}
		JTableHeader jtableheader = taskManagerBodyView.getTaskListTable().getTableHeader();
		
		defaultHeaderRenderer = new SortHeaderRenderer(head);
		jtableheader.setDefaultRenderer(defaultHeaderRenderer);
		taskManagerBodyView.getTaskListTable().updateUI();
		loadTaskTable();
	}
	
	//加载表格数据
	public void loadTaskTable(){
		taskImplDao.execute(new Runnable() {
			@Override
			public void run() {
				DefaultMutableTreeNode node=(DefaultMutableTreeNode)
						taskManagerLeftTreeView.getTaskGroupTree().getLastSelectedPathComponent();
				if(node==null){
					node=(DefaultMutableTreeNode)taskManagerLeftTreeView.getTreeModel().getRoot();
				}
				String groupTag=((TaskGroup)node.getUserObject()).getTaskTag();
				int limit=(int)taskManagerFootView.getPagePerNumberComboBox().getSelectedItem();
				int page=taskManagerFootView.getPage();
				String filter=taskManagerLeftTreeView.getTaskSearchField().getText();
				String column="";
				String direction="";
				if(defaultHeaderRenderer!=null){
					MySortHead head=((SortHeaderRenderer)defaultHeaderRenderer).getSortHead();
					if(head.getSortStatu()==1){
						column=head.getSortName();
						direction="asc";
					}else if(head.getSortStatu()==2){
						column=head.getSortName();
						direction="desc";
					}
				}
				List<TaskImpl> tasks=taskImplDao.getTaskList(groupTag, page, limit,column,direction,filter);
				((MyDefaultTableModel)taskManagerBodyView.getTableModel()).setData(tasks);
				long totalTask=taskImplDao.getTreeTaskCount(groupTag,filter);
				long totalPage=totalTask%limit==0?totalTask/limit:totalTask/limit+1;
				taskManagerFootView.getTotalPage().setText(String.valueOf(totalPage));
				
			}
		});

	}
	
	/**
	 * 分组前添加任务数量
	 * @param taskGroup
	 */
	public void addTaskNumberShow(TaskGroup taskGroup){
		String filter=taskManagerLeftTreeView.getTaskSearchField().getText();
		long taskNumber=taskImplDao.getTreeTaskCount(taskGroup.getTaskTag(),filter);
		taskGroup.setNameWithNumber(taskGroup.getName()+"("+taskNumber+")");
	}
	
	/**
	 * 加载分组树
	 */
	public void loadTree(){
		DefaultMutableTreeNode treeNode=
				(DefaultMutableTreeNode) taskManagerLeftTreeView.getTaskGroupTree().getLastSelectedPathComponent();
		String selectedGroupTag=null;
		if(treeNode!=null){
			selectedGroupTag=((TaskGroup)treeNode.getUserObject()).getTaskTag();
		}
		TaskGroup tgRoot=taskGroupDao.get("000");
		addTaskNumberShow(tgRoot);
		final DefaultMutableTreeNode root=new DefaultMutableTreeNode(tgRoot);
		final DefaultTreeModel treeModel= taskManagerLeftTreeView.getTreeModel();

		final String finalselectedGroupTag=selectedGroupTag;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				treeModel.setRoot(root);
				loadChild(root);
				taskManagerLeftTreeView.expandAll(taskManagerLeftTreeView.getTaskGroupTree(),new TreePath(root),true);
				taskManagerLeftTreeView.getTaskGroupTree().updateUI();
				if(finalselectedGroupTag!=null){
					selectNodeByGroupTag(finalselectedGroupTag,
							(DefaultMutableTreeNode) treeModel.getRoot());
				}
			}
		});
	}
	
	/**
	 * 分组刷新后保持选中
	 * @param GroupTag
	 * @param node
	 */
	private void selectNodeByGroupTag(String GroupTag,DefaultMutableTreeNode node){
		for(int i=0;i<node.getChildCount();i++){
			DefaultMutableTreeNode child=
					(DefaultMutableTreeNode) node.getChildAt(i);
			if(((TaskGroup)child.getUserObject()).getTaskTag().equals(GroupTag)){	
				TreePath path=new TreePath(taskManagerLeftTreeView.getTreeModel().getPathToRoot(child));
				taskManagerLeftTreeView.getTaskGroupTree().getSelectionModel().setSelectionPath(path);
				return;
			}
			if(!child.isLeaf()){
				selectNodeByGroupTag(GroupTag,child);
			}
		}
	}
	
	/**
	 * 递归加载分组
	 * @param node
	 */
	public void loadChild(DefaultMutableTreeNode node){
		TaskGroup taskGroup=(TaskGroup)node.getUserObject();
		for(TaskGroup tg:taskGroupDao.getChildren(taskGroup.getTaskTag())){
			addTaskNumberShow(tg);
			DefaultMutableTreeNode child=new DefaultMutableTreeNode(tg);
			node.add(child);
			if(!taskGroupDao.isLeaf(tg.getTaskTag())){
				loadChild(child);
			}
		}
	}
	
	public void refreshFootLabel(){
		DefaultMutableTreeNode node=(DefaultMutableTreeNode)
				getTaskManagerLeftTreeView().getTaskGroupTree().getLastSelectedPathComponent();
		if(node==null){
			node=((DefaultMutableTreeNode)getTaskManagerLeftTreeView().getTreeModel().getRoot()).getFirstLeaf();
		}
		String groupTag=((TaskGroup)node.getUserObject()).getTaskTag();
		String groupString=((TaskGroup)node.getUserObject()).getName();
		Map sta=taskImplDao.getFootCount(groupTag);
		String text=String.format("当前分组:%s  %s:%s  %s:%s  %s:%s  %s:%s  %s:%s  %s:%s", groupString,
				ViewConstants.RUNNING,nulltoZero(sta.get(ViewConstants.RUNNING)),
				ViewConstants.WAITING,nulltoZero(sta.get(ViewConstants.WAITING)),
				ViewConstants.FINISHED,nulltoZero(sta.get(ViewConstants.FINISHED)),
				ViewConstants.FAILED,nulltoZero(sta.get(ViewConstants.FAILED)),
				ViewConstants.NEW,nulltoZero(sta.get(ViewConstants.NEW)),
				ViewConstants.STOPED,nulltoZero(sta.get(ViewConstants.STOPED))
				);
		
		taskManagerFootView.getTaskStatic().setText(text);
	}
	
	private int nulltoZero(Object o){
		if(o==null){
			return 0;
		}else{
			return (int) o;
		}
	}
	
	public TaskManagerLeftTreeView getTaskManagerLeftTreeView() {
		return taskManagerLeftTreeView;
	}

	public void setTaskManagerLeftTreeView(
			TaskManagerLeftTreeView taskManagerLeftTreeView) {
		this.taskManagerLeftTreeView = taskManagerLeftTreeView;
	}

	public TaskManagerHeadView getTaskManagerHeadView() {
		return taskManagerHeadView;
	}

	public void setTaskManagerHeadView(TaskManagerHeadView taskManagerHeadView) {
		this.taskManagerHeadView = taskManagerHeadView;
	}

	public TaskManagerBodyView getTaskManagerBodyView() {
		return taskManagerBodyView;
	}

	public void setTaskManagerBodyView(TaskManagerBodyView taskManagerBodyView) {
		this.taskManagerBodyView = taskManagerBodyView;
	}

	public TaskManagerFootView getTaskManagerFootView() {
		return taskManagerFootView;
	}

	public void setTaskManagerFootView(TaskManagerFootView taskManagerFootView) {
		this.taskManagerFootView = taskManagerFootView;
	}
	
	


	public TaskManagerView getTaskManagerView() {
		return taskManagerView;
	}

	public void setTaskManagerView(TaskManagerView taskManagerView) {
		this.taskManagerView = taskManagerView;
	}

	public TaskGroupDao getTaskGroupDao() {
		return taskGroupDao;
	}

	public TaskImplDao getTaskImplDao() {
		return taskImplDao;
	}

	public void setTaskGroupDao(TaskGroupDao taskGroupDao) {
		this.taskGroupDao = taskGroupDao;
	}

	public void setTaskImplDao(TaskImplDao taskImplDao) {
		this.taskImplDao = taskImplDao;
	}

	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}
	


	public ConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public void setConnectionManager(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}



	/**
	 * 排序箭头变换
	 * @author peter
	 *
	 */
	public class SortHeaderRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -8059140121463202150L;
		
		private MySortHead sortHead;

		public SortHeaderRenderer(){}
		
		public MySortHead getSortHead() {
			return sortHead;
		}

		public SortHeaderRenderer(MySortHead sortHead){
			this.sortHead=sortHead;
			this.setHorizontalAlignment(0);
			this.setHorizontalTextPosition(2);
		}
		
		
		public java.awt.Component getTableCellRendererComponent(JTable jtable,
				Object obj, boolean isSelected, boolean hasFocus, int row, int column) {
			//设置表格头样式
			if (jtable != null) {
				JTableHeader jtableheader = jtable.getTableHeader();
				if (jtableheader != null) {
					setForeground(jtableheader.getForeground());
					setBackground(jtableheader.getBackground());
					setFont(jtableheader.getFont());
				}
			}
			setText(obj != null ? obj.toString() : "");
			int k = jtable.convertColumnIndexToModel(column);
			if (k == sortHead.getColumnIndex()) {
				if(sortHead.getSortStatu()==1){
					setIcon(upIcon);
				}else if(sortHead.getSortStatu()==2){
					setIcon(downIcon);
				}else{
					setIcon(null);
				}
			} else {
				setIcon(null);
			}
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			return this;
		}
		
	}
	
	/**
	 * 表格排序箭头
	 * @author peter
	 *
	 */
	public class UpDownArrow implements Icon{
		private int size = 12;
		public static final int UP = 0;
		public static final int DOWN = 1;
		private int direction;
		public UpDownArrow(int i) {
			direction = i;
		}
		public int getIconHeight() {
			return size;
		}
		public int getIconWidth() {
			return size;
		}
		@Override
		public void paintIcon(java.awt.Component component, Graphics g, int i, int j) {
			int k = i + size / 2;
			int l = i + 1;
			int i1 = (i + size) - 2;
			int j1 = j + 1;
			int k1 = (j + size) - 2;
			Color color = (Color) UIManager.get("controlDkShadow");
			if (direction == 0) {
				g.setColor(Color.white);
				g.drawLine(l, k1, i1, k1);
				g.drawLine(i1, k1, k, j1);
				g.setColor(color);
				g.drawLine(l, k1, k, j1);
			} else {
				g.setColor(color);
				g.drawLine(l, j1, i1, j1);
				g.drawLine(l, j1, k, k1);
				g.setColor(Color.white);
				g.drawLine(i1, j1, k, k1);
			}
		}
	}
}