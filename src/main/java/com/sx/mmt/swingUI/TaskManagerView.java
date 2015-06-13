package com.sx.mmt.swingUI;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import com.sx.mmt.constants.AppParameterDao;
import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.api.TaskManager;
import com.sx.mmt.internal.task.TaskManagerImpl;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class TaskManagerView extends JFrame{

	private static final long serialVersionUID = 1L;
	private TaskManagerLeftTreeView taskManagerLeftTreeView;
	private TaskManagerHeadView taskManagerHeadView;
	private TaskManagerBodyView taskManagerBodyView;
	private TaskManagerFootView taskManagerFootView;
	
	private JSplitPane splitPanel;
	private JPanel rightPanel;
	private SystemTray tray;
	private TrayIcon trayicon;
	private PopupMenu trayPopMenu;
	private MenuItem close; 
	public TaskManagerView(){
		ini();
		setAttribute();
		setListener();
	}
	
	public void ini(){
		UIManager.put("MenuItem.font",ViewConstants.TextFont);
		UIManager.put("Menu.font",ViewConstants.TextFont);
		UIManager.put("Button.font",ViewConstants.TextFont);
		UIManager.put("Label.font",ViewConstants.TextFont);
		UIManager.put("CheckBox.font",ViewConstants.TextFont);
		UIManager.put("ComboBox.font",ViewConstants.TextFont);
		taskManagerLeftTreeView=new TaskManagerLeftTreeView();
		taskManagerHeadView=new TaskManagerHeadView();
		taskManagerBodyView=new TaskManagerBodyView();
		taskManagerFootView=new TaskManagerFootView();
		splitPanel = new JSplitPane();
		rightPanel=new JPanel();
		tray = SystemTray.getSystemTray();
		
		trayPopMenu=new PopupMenu();
		close=new MenuItem("CLOSE");
		close.setFont(ViewConstants.TextFont);
		trayPopMenu.add(close);
		trayicon=new TrayIcon(new ImageIcon(getClass().getResource("/img/tray.png")).getImage(),"终端远程升级平台",trayPopMenu);
	}
	
	public void setAttribute(){
		
		this.setMaximizedBounds(new Rectangle(0, 0, 1920, 1080));
		this.setMinimumSize(new Dimension(600, 480));
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new BorderLayout(5, 5));
		this.setTitle("终端远程升级工具-三星电气股份有限公司");
		this.setIconImage((new ImageIcon(getClass().getResource("/img/head.png")).getImage()));
		splitPanel.setPreferredSize(new Dimension(
				AppParameterDao.getIntValue(ConfigConstants.ApplicationStartupWidth), 
				AppParameterDao.getIntValue(ConfigConstants.ApplicationStartupHeight)));
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		splitPanel.setLeftComponent(taskManagerLeftTreeView);
		rightPanel.setLayout(new BorderLayout(5, 5));
		rightPanel.add(taskManagerHeadView,BorderLayout.NORTH);
		rightPanel.add(taskManagerBodyView,BorderLayout.CENTER);
		rightPanel.add(taskManagerFootView,BorderLayout.SOUTH);
		splitPanel.setRightComponent(rightPanel);
		contentPane.add(splitPanel,BorderLayout.CENTER);
		this.pack();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(getOwner());
	}
	
	public void setListener(){
		final TaskManagerView me=this;
		final TaskManagerController taskManagerController=(TaskManagerController) 
				SpringBeanUtil.getBean("taskManagerController");
		taskManagerController.setTaskManagerLeftTreeView(taskManagerLeftTreeView);
		taskManagerController.setTaskManagerHeadView(taskManagerHeadView);
		taskManagerController.setTaskManagerBodyView(taskManagerBodyView);
		taskManagerController.setTaskManagerFootView(taskManagerFootView);
		taskManagerController.setTaskManagerView(this);
		taskManagerController.loadTree();
		taskManagerController.loadTaskTable();
		taskManagerController.getTaskManagerFootView().loadValue();
		close.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.exitHandler(e);
				
			}		
	
		});
		trayicon.addMouseListener(new MouseAdapter() {  
			  
            public void mouseClicked(MouseEvent e) {  
                if (e.getButton()==MouseEvent.BUTTON1) {  
                	me.setVisible(true);
                	me.setExtendedState(JFrame.NORMAL);
                	tray.remove(trayicon);
                }
            }
		});
		me.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				try{
					tray.add(trayicon);
					me.setVisible(false);
				}catch(AWTException e1){
					e1.printStackTrace();
				}
				
			}
		});
	}
}
