package com.sx.mmt.swingUI;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class MyMask extends JFrame{
	private static final long serialVersionUID = 1L;
	private JProgressBar bar;
	private JPanel pane;

	public MyMask(){
		ini();
	}
	
	public void setValue(int value){
		bar.setValue(value);
	}
	public int getValue(){
		return bar.getValue();
	}
	
	public void ini(){
		setBounds(300, 100, 300, 100);
		pane = new JPanel();
		pane.setLayout(new BorderLayout());
		getContentPane().add(pane);
		bar = new JProgressBar();
		bar.setMinimum(0);
		bar.setMaximum(100);
		bar.setValue(0);
		bar.setStringPainted(true);
		bar.setPreferredSize(new Dimension(250, 30));
		pane.add(bar, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setAlwaysOnTop(true);
		this.setResizable(false);
		this.setTitle("完成进度");
		this.pack();
		this.setLocationRelativeTo(getOwner());
	}
	
	

}
