package com.sx.mmt.swingUI;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.util.FileTool;

public class AboutView extends JTabbedPane{

	private static final long serialVersionUID = 1L;
	private JPanel aboutPanel;
	private JPanel updateInfoPanel;
	private JTextArea aboutArea;
	private JTextArea updateInfoArea;

	public AboutView(){
		ini();
		setAttribute();
	}
	public void ini(){
		aboutPanel=new JPanel(new BorderLayout(5, 5));
		updateInfoPanel=new JPanel(new BorderLayout(5, 5));
		aboutArea=new JTextArea();
		updateInfoArea=new JTextArea();

	}
	public void setAttribute(){
		JScrollPane aboutScrollPane=new JScrollPane();
		aboutScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		aboutScrollPane.setViewportView(aboutArea);
		aboutArea.setFont(ViewConstants.TextFont);
		aboutArea.setEditable(false);
		aboutArea.setText(getAbout());
		aboutPanel.add(aboutScrollPane,BorderLayout.CENTER);
		
		JScrollPane updateInfoScrollPane=new JScrollPane();
		updateInfoScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		updateInfoScrollPane.setViewportView(updateInfoArea);
		updateInfoArea.setFont(ViewConstants.TextFont);
		updateInfoArea.setEditable(false);
		updateInfoArea.setText(getUpdateInfo());
		updateInfoPanel.add(updateInfoScrollPane,BorderLayout.CENTER);
		this.setFont(ViewConstants.TextFont);
		this.addTab("关于", aboutPanel);
		this.addTab("更新记录", updateInfoPanel);
		
	}
	
	private String getAbout(){
		String text="2.5.5 三星终端远程升级工具";
		return text;
	} 
	private String getUpdateInfo(){
		String info=null;
		try {
			info = new String(
					FileTool.readFile(new File(System.getProperty("user.dir")+"/config/history.txt"),"UTF-8"));
		} catch (Exception e) {
			info="读取更新历史失败";
		}
		return info;
	}
}
