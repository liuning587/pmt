package com.sx.mmt.swingUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class TaskManagerHeadView extends JToolBar{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton newTaskButton;
	private JButton startTaskButton;
	private JButton stopTaskButton;
	private JButton deleteTaskButton;
	private JButton showTerminalButton;
	private JButton exportTaskButton;
	private JButton helpButton;
	private JButton configButton;
	private JButton exitButton;
	private JPanel hSpacer;
	public TaskManagerHeadView(){
		ini();
		setAttribute();
		setListener();
	}
	
	public void ini(){
		newTaskButton=new JButton();
		startTaskButton=new JButton();
		stopTaskButton=new JButton();
		deleteTaskButton=new JButton();
		showTerminalButton=new JButton();
		exportTaskButton=new JButton();
		helpButton=new JButton();
		configButton=new JButton();
		exitButton=new JButton();
		hSpacer=new JPanel(null);
		
	}
	
	public void setAttribute(){
		this.setBorder(new EmptyBorder(15, 5, 5, 15));
		newTaskButton.setFont(ViewConstants.TitleFont);
		newTaskButton.setText("新建");
		newTaskButton.setIcon(new ImageIcon(getClass().getResource("/img/Add.png")));
		newTaskButton.setPreferredSize(new Dimension(85, 29));
		newTaskButton.setToolTipText("新建任务");
		this.add(newTaskButton);

		
		startTaskButton.setIcon(new ImageIcon(getClass().getResource("/img/Play.png")));
		startTaskButton.setToolTipText("开始所选分组任务");
		this.add(startTaskButton);

		
		stopTaskButton.setIcon(new ImageIcon(getClass().getResource("/img/Pause.png")));
		stopTaskButton.setToolTipText("暂停所选分组任务");
		this.add(stopTaskButton);

		
		deleteTaskButton.setIcon(new ImageIcon(getClass().getResource("/img/Remove.png")));
		deleteTaskButton.setToolTipText("移除所选分组任务");
		this.add(deleteTaskButton);
		
		
		showTerminalButton.setIcon(new ImageIcon(getClass().getResource("/img/Terminal.png")));
		showTerminalButton.setToolTipText("显示报文");
		this.add(showTerminalButton);
		this.add(hSpacer);

		
		exportTaskButton.setIcon(new ImageIcon(getClass().getResource("/img/Export.png")));
		exportTaskButton.setToolTipText("导出当前分类任务到csv文件");
		this.add(exportTaskButton);

		
		helpButton.setIcon(new ImageIcon(getClass().getResource("/img/Help.png")));
		helpButton.setToolTipText("软件帮助");
		this.add(helpButton);

	
		configButton.setIcon(new ImageIcon(getClass().getResource("/img/Config.png")));
		configButton.setToolTipText("软件配置");
		this.add(configButton);
		
		exitButton.setIcon(new ImageIcon(getClass().getResource("/img/exit.png")));
		exitButton.setToolTipText("保存任务状态并退出程序");
		this.add(exitButton);
		
		
	}
	
	public void setListener(){
		final TaskManagerController taskManagerController=(TaskManagerController) 
				SpringBeanUtil.getBean("taskManagerController");
		newTaskButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.addNewTaskHandler(e);
			}
        });

		startTaskButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.startAllTaskHandler(e);
			}
        });
		stopTaskButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.stopAllTaskHandler(e);
			}
        });
		deleteTaskButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.deleteAllTaskHandler(e);
			}
        });
		showTerminalButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.showTerminalHandler(e);
			}
        });
		exportTaskButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.exportTaskHandler(e);
			}
        });
		helpButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.helpHandler(e);
			}
        });
		configButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.configHandler(e);
			}
        });
		
		exitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.exitHandler(e);
			}
        });
		
	}

	public JButton getNewTaskButton() {
		return newTaskButton;
	}

	public void setNewTaskButton(JButton newTaskButton) {
		this.newTaskButton = newTaskButton;
	}

	public JButton getStartTaskButton() {
		return startTaskButton;
	}

	public void setStartTaskButton(JButton startTaskButton) {
		this.startTaskButton = startTaskButton;
	}

	public JButton getStopTaskButton() {
		return stopTaskButton;
	}

	public void setStopTaskButton(JButton stopTaskButton) {
		this.stopTaskButton = stopTaskButton;
	}

	public JButton getDeleteTaskButton() {
		return deleteTaskButton;
	}

	public void setDeleteTaskButton(JButton deleteTaskButton) {
		this.deleteTaskButton = deleteTaskButton;
	}

	public JButton getShowTerminalButton() {
		return showTerminalButton;
	}

	public void setShowTerminalButton(JButton showTerminalButton) {
		this.showTerminalButton = showTerminalButton;
	}

	public JButton getExportTaskButton() {
		return exportTaskButton;
	}

	public void setExportTaskButton(JButton exportTaskButton) {
		this.exportTaskButton = exportTaskButton;
	}

	public JButton getHelpButton() {
		return helpButton;
	}

	public void setHelpButton(JButton helpButton) {
		this.helpButton = helpButton;
	}

	public JButton getConfigButton() {
		return configButton;
	}

	public void setConfigButton(JButton configButton) {
		this.configButton = configButton;
	}
	
	
	
}
