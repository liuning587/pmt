package com.sx.mmt.swingUI.cqdw;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sx.mmt.constants.AppParameterDao;
import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.task.TaskGroup;
import com.sx.mmt.internal.task.TaskImpl;
import com.sx.mmt.internal.task.TaskImplDao;
import com.sx.mmt.internal.task.command.CommandState;
import com.sx.mmt.internal.util.CSVUtil;
import com.sx.mmt.internal.util.FileTool;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SimpleBytes;
import com.sx.mmt.internal.util.SpringBeanUtil;
import com.sx.mmt.swingUI.MyMask;
import com.sx.mmt.swingUI.TaskManagerController;
import com.sx.mmt.swingUI.AddTaskController.Address;

@Component(value="cqaddTaskController")
public class CQAddTaskController {
	private CQAddTaskView addTaskView;
	@Autowired
	private TaskImplDao taskImplDao;

	public CQAddTaskView getAddTaskView() {
		return addTaskView;
	}

	public void setAddTaskView(CQAddTaskView addTaskView) {
		this.addTaskView = addTaskView;
	}
	
	public void addTaskHandler(ActionEvent e){
		String district=addTaskView.getDistrictField().getText();
		String upgradeId=addTaskView.getPermitNoField().getText();
		String addressField=addTaskView.getAddressField().getText();
		if(!Pattern.compile("[0-9a-fA-F]{1,8}").matcher(district).matches()) return;
		List<Address> adds=new ArrayList<Address>();
		if(addTaskView.getAddressFormat().getSelectedItem().equals(ViewConstants.Decimal)){
			if(!Pattern.compile("[0-9]{1,8}").matcher(addressField).matches()) return;
			adds.clear();
			Address address=new Address();
			address.setDistinct(new SimpleBytes((short)Integer.parseInt(district,16)));
			address.setAddress(new SimpleBytes((short)Integer.parseInt(addressField)));
			address.setUpgradeId(upgradeId);
			adds.add(address);
			addAddress(adds);
		}else{
			if(!Pattern.compile("[0-9a-fA-F]{1,8}").matcher(addressField).matches()) return;
			adds.clear();
			Address address=new Address();
			address.setDistinct(new SimpleBytes((short)Integer.parseInt(district,16)));
			address.setAddress(new SimpleBytes((short)Integer.parseInt(addressField,16)));
			address.setUpgradeId(upgradeId);
			adds.add(address);
			addAddress(adds);
			
		}
	}
	
	public void addAddress(final List<Address> adds){
		addTaskView.getTerminalListModel().add(adds);
		SwingUtilities.invokeLater(new Runnable() {	
			@Override
			public void run() {
				addTaskView.getTerminalList().validate();
				addTaskView.getTerminalList().updateUI();
			}
		});
		ExecutorService thread=Executors.newSingleThreadExecutor();
		thread.execute(new Runnable() {
			@Override
			public void run() {
				for(Address a:adds){
					if(taskImplDao.isExist(a.getUpgradeId())){
						a.setWarn("该终端已在任务列表中");
					}
				}
				SwingUtilities.invokeLater(new Runnable() {	
					@Override
					public void run() {
						addTaskView.getTerminalList().validate();
						addTaskView.getTerminalList().updateUI();
					}
				});
			}
		});
	}
	
	public void removeTerminal(MouseEvent e){
		int[] list=addTaskView.getTerminalList().getSelectedIndices();
		final List<Address> addressList=new ArrayList<Address>();
		for(int i:list){
			addressList.add((Address) addTaskView.getTerminalListModel().getElementAt(i));
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				addTaskView.getTerminalListModel().remove(addressList);
				SwingUtilities.invokeLater(new Runnable() {	
					@Override
					public void run() {
						addTaskView.getTerminalList().validate();
						addTaskView.getTerminalList().setSelectedIndices(new int[]{});
						addTaskView.getTerminalList().updateUI();
					}
				});
			}
		}).start();
		SwingUtilities.invokeLater(new Runnable() {	
			@Override
			public void run() {
				addTaskView.getTerminalList().validate();
				addTaskView.getTerminalList().setSelectedIndices(new int[]{});
				addTaskView.getTerminalList().updateUI();
			}
		});
		
	}
	
	public void popupMenu(MouseEvent e){
		JList<String> terminalList=addTaskView.getTerminalList();
		if(terminalList.getSelectedIndices().length>0 && e.getButton()==MouseEvent.BUTTON3
				&& e.getClickCount()==1){
			addTaskView.getRemoveMenu().setVisible(true);
			addTaskView.getRemoveMenu().show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	public void importTaskHandler(ActionEvent e){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("选择导入文件");
		String path=AppParameterDao.getValue(ConfigConstants.ImportFilePath);
		File defaultPath;
		if(!StringUtils.isBlank(path)){
			defaultPath=new File(path);
			if(defaultPath.isDirectory()){
				fileChooser.setCurrentDirectory(defaultPath);
			}
		}else{
			defaultPath=new File(System.getProperty("user.dir"));
		}
		
		fileChooser.setFileFilter(new FileFilter(){
			@Override
			public boolean accept(File file) {
				if(file.isDirectory() 
						|| file.getName().toLowerCase().lastIndexOf(".txt")>0
						|| file.getName().toLowerCase().lastIndexOf(".xls")>0
						|| file.getName().toLowerCase().lastIndexOf(".xlsx")>0){
					return true;
				}else{
					return false;
				}
			}
			@Override
			public String getDescription() {
				return ".txt";
			}	
		});
		int i = fileChooser.showOpenDialog((java.awt.Component) e.getSource());  
        if(i==JFileChooser.APPROVE_OPTION)  
        {
            File selectedFile = fileChooser.getSelectedFile();
            if(selectedFile.getName().toLowerCase().lastIndexOf(".xls")>0 
            		|| selectedFile.getName().toLowerCase().lastIndexOf(".xlsx")>0){
            	loadFromXls(selectedFile);
            }else if(selectedFile.getName().toLowerCase().lastIndexOf(".txt")>0){
            	loadFromTxt(selectedFile);
            }else if(selectedFile.getName().toLowerCase().lastIndexOf(".csv")>0){
            	loadFromCsv(selectedFile);
            }
            
            //设置默认路径
            if(!selectedFile.getParent().equals(defaultPath.getPath())){
            	AppParameterDao.update(ConfigConstants.ImportFilePath, 
            			selectedFile.getParent());
            }
        }
	}
	
	private void loadFromXls(File file){
		try{
			InputStream inp = new FileInputStream(file);
			Workbook wb = WorkbookFactory.create(inp);
			Sheet sheet = wb.getSheetAt(0);
			int rowb=sheet.getFirstRowNum();
			int rowl=sheet.getLastRowNum();
			int districtColumn=PropertiesUtil.parseToInt(ConfigConstants.ImportDistrictColumn);
			int addressColumn=PropertiesUtil.parseToInt(ConfigConstants.ImportAddressColumn);
			List<Address> adds=new ArrayList<Address>();
			for(int i=rowb;i<=rowl;i++){
				Row row = sheet.getRow(i);
				Address address=new Address();
				Cell cell1=row.getCell(districtColumn-1);
				Cell cell2=row.getCell(addressColumn-1);
				String districtString=null;
				String addressString=null;
				if(cell1!=null){
					districtString=cell1.getStringCellValue();
				}else{
					continue;
				}
				
				if(districtString!=null){
					districtString=districtString.trim();
				}else{
					continue;
				}
				
				if(cell2!=null){
					addressString=cell2.getStringCellValue();
				}else{
					continue;
				}
				
				if(addressString!=null){
					addressString=addressString.trim();
				}else{
					continue;
				}
				
				Pattern pattern=Pattern.compile("[0-9a-fA-F]{1,20}");
				if(pattern.matcher(districtString).matches() &&
						pattern.matcher(addressString).matches()){
					address.setUpgradeId(districtString.trim());
					address.setDistinct(new SimpleBytes((short)Integer.parseInt(addressString.substring(0, 4),16)));
					address.setAddress(new SimpleBytes((short)Integer.parseInt(addressString.substring(4),16)));
				}else{
					continue;
				}
				
				adds.add(address);
			}
			addAddress(adds);
		}catch(Exception e){
			e.printStackTrace();
		}
		return;
	}
	
	private void loadFromCsv(File file){
		return;
	}
	
	private void loadFromTxt(File file){
		try{
			List<String> lists=FileTool.readFileToList(file);
			List<Address> adds=new ArrayList<Address>();
			for(String s:lists){
				Address address=new Address();
				String[] list=s.split(",");
				address.setUpgradeId(list[0].trim());
				String addString=list[1].trim();
				address.setDistinct(new SimpleBytes((short)Integer.parseInt(addString.substring(0, 4),16)));
				address.setAddress(new SimpleBytes((short)Integer.parseInt(addString.substring(4),16)));
				adds.add(address);
			}
			addAddress(adds);
		}catch(Exception e){

		}
	}
	
	public void confirmHandler(ActionEvent e){
		final MyMask bar=new MyMask();
		bar.setVisible(true);
		final TaskManagerController taskManagerController=(TaskManagerController) 
				SpringBeanUtil.getBean("taskManagerController");
		
		Thread thread=new Thread(new Runnable() {	
			@Override
			public void run() {
				String tasktype=(String) addTaskView.getTaskType().getSelectedItem();

				DefaultMutableTreeNode node=(DefaultMutableTreeNode)
						taskManagerController.getTaskManagerLeftTreeView().getTaskGroupTree().getLastSelectedPathComponent();
				if(node==null){
					node=((DefaultMutableTreeNode)taskManagerController.getTaskManagerLeftTreeView().getTreeModel().getRoot()).getFirstLeaf();
				}
				String groupTag=((TaskGroup)node.getUserObject()).getTaskTag();
				addTaskView.dispose();
				TaskImplDao taskImplDao=(TaskImplDao)SpringBeanUtil.getBean("taskImplDao");
				Collection<Address> adds=addTaskView.getTerminalListModel().getData().values();
				int addsNum=adds.size();
				int addsIndex=0;
				List<TaskImpl> tasks=new ArrayList<TaskImpl>();
				for(Address address:adds){
					addsIndex++;
					TaskImpl task=new TaskImpl();
					task.setId(address.getUpgradeId());
					task.setTerminalAddress(address.getDistinct().toHexString("")+address.getAddress().toHexString(""));
					task.setTaskName(tasktype);
					task.setCurrentStepIndex(0);
					task.setNextActionTime(System.currentTimeMillis());
					task.setCounter(0);
					task.setTaskStatus(ViewConstants.NEW);
					task.setStepStatus(CommandState.Start);
					task.setPfc(0);
					task.setTaskGroupTag(groupTag);
					task.setCreateTime(new Date());
					task.setRetryCount(0);
					task.setPacketIndex(0);
					task.setPriority(10);
					tasks.add(task);
					final int processNow=addsIndex*100/addsNum;
					if(processNow>bar.getValue()){
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								bar.setValue(processNow);
							}
						});
						taskImplDao.add(tasks);
						tasks.clear();
					}
				}
				bar.dispose();
				taskManagerController.loadTree();
				taskManagerController.loadTaskTable();
				addTaskView.getTerminalListModel().clear();
			}
		});
		thread.start();
	}
	
	
	public void cancelHandler(ActionEvent e){
		addTaskView.dispose();
	}
	
	public class Address{
		private SimpleBytes distinct;
		private SimpleBytes address;
		private String upgradeId;
		private String warn;
		
		public String getWarn() {
			return warn;
		}
		public void setWarn(String warn) {
			this.warn = warn;
		}
		public SimpleBytes getDistinct() {
			return distinct;
		}
		public void setDistinct(SimpleBytes distinct) {
			this.distinct = distinct;
		}
		
		public SimpleBytes getAddress() {
			return address;
		}
		public void setAddress(SimpleBytes address) {
			this.address = address;
		}
		public String getFullAddress(){
			return distinct.toHexString("")+address.toHexString("");
		}
		public String getId(){
			return upgradeId;
		}
		
		
		public String getUpgradeId() {
			return upgradeId;
		}
		public void setUpgradeId(String upgradeId) {
			this.upgradeId = upgradeId;
		}
		@Override
		public String toString() {
			StringBuilder sb=new StringBuilder();
			sb.append(upgradeId).append("(").append(distinct.toHexString(""))
			.append(" ").append(address.toHexString("")).append(")");
			if(warn!=null){
				sb.append(warn);
			}
			return sb.toString();
		}
	}
}