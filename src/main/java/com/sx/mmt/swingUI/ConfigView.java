package com.sx.mmt.swingUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ConfigView extends JDialog{

	private static final long serialVersionUID = 1L;
	private final Font defaultFont=new Font("宋体",Font.PLAIN,14);
	private JTabbedPane container;
	private CommandConfigView commandConfigView;
	private TaskConfigView taskConfigView;
	private SystemConfigView systemConfigView;
	private AboutView aboutView;
	
	public ConfigView(Frame owner){
		super(owner,"软件配置",true);
		ini();
		setAttribute();
		setListener();
	}
	
	public void ini(){
		container=new JTabbedPane();
		commandConfigView=new CommandConfigView();
		taskConfigView=new TaskConfigView();
		systemConfigView=new SystemConfigView();
		aboutView=new AboutView();
	}
	
	public void setAttribute(){
		this.setMinimumSize(new Dimension(800, 600));
		this.setLayout(new BorderLayout());
		container.setPreferredSize(new Dimension(800, 600));
		container.addTab("命令配置",commandConfigView);
		container.addTab("任务配置",taskConfigView);
		container.addTab("系统配置", systemConfigView);
		container.addTab("关于",aboutView);
		container.setFont(defaultFont);
		this.add(container,BorderLayout.CENTER);
		this.setLocationRelativeTo(getOwner());
		this.pack();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public void setListener(){	
		container.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(((JTabbedPane)e.getSource()).getSelectedIndex()==1){
					taskConfigView.refreshValue();
				}
				if(((JTabbedPane)e.getSource()).getSelectedIndex()==0){
					commandConfigView.getQueryVersionCommandView().refreshValue();
				}
			}
		});
	}
}
