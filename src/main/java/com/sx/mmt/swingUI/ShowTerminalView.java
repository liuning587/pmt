package com.sx.mmt.swingUI;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ViewConstants;

public class ShowTerminalView extends JFrame{

	private static final long serialVersionUID = 1L;
	private JTextField filterField;
	private JTextArea textArea;
	
	public ShowTerminalView(){
		ini();
		setAttribute();
	}
	public void ini(){
		filterField=new JTextField();
		textArea=new JTextArea();
	}
	public void setAttribute(){
		this.setMaximizedBounds(new Rectangle(0, 0, 1920, 1080));
		this.setMinimumSize(new Dimension(600, 500));
		this.setLayout(new BorderLayout(5, 5));
		this.setTitle("报文监控");
		Container contentPane = getContentPane();
		
		JToolBar filterPanel=new JToolBar();
		filterPanel.add(new JLabel("报文过滤"));
		filterPanel.add(filterField);
		filterPanel.setBorder(new EmptyBorder(15, 15, 5, 15));
		
		JScrollPane areaScrollPane=new JScrollPane();
		areaScrollPane.setBorder(new EmptyBorder(5, 15, 15, 15));
		textArea.setAutoscrolls(true);
		textArea.setEditable(false);
		areaScrollPane.setViewportView(textArea);
		textArea.setFont(ViewConstants.TextFont);
		
		contentPane.add(filterPanel,BorderLayout.NORTH);
		contentPane.add(areaScrollPane,BorderLayout.CENTER);
		
		this.pack();
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setLocationRelativeTo(getOwner());
		
	}

	public JTextField getFilterField() {
		return filterField;
	}
	public void setFilterField(JTextField filterField) {
		this.filterField = filterField;
	}
	public JTextArea getTextArea() {
		return textArea;
	}
	public void setTextArea(JTextArea textArea) {
		this.textArea = textArea;
	}
	
	
}
