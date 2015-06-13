package com.sx.mmt.swingUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;
import java.io.File;
import java.io.UnsupportedEncodingException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.util.FileTool;

public class HelpView extends JDialog{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea textArea;
	
	public HelpView(Frame owner){
		super(owner,"软件配置",true);
		ini();
		setAttribute();
	}
	
	public void ini(){
		textArea=new JTextArea();
	}
	
	public void setAttribute(){
		this.setMinimumSize(new Dimension(600, 500));
		this.setLayout(new BorderLayout(5, 5));
		this.setTitle("软件帮助");
		Container contentPane = getContentPane();
		
		
		JScrollPane areaScrollPane=new JScrollPane();
		areaScrollPane.setBorder(new EmptyBorder(15, 15, 15, 15));
		areaScrollPane.setViewportView(textArea);
		textArea.setFont(ViewConstants.TextFont);
		textArea.setText(getInfo());
		contentPane.add(areaScrollPane,BorderLayout.CENTER);
		
		this.pack();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(getOwner());
		
	}
	public String getInfo(){
		String info=null;
		try {
			info = new String(
					FileTool.readFile(new File(System.getProperty("user.dir")+"/config/help.txt"),"UTF-8"));
		} catch (Exception e) {
			info="帮助文件读取失败";
		}
		return info;
		
	}
}
