package com.sx.mmt.swingUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ApplicationStartMask extends JFrame{
	private static final long serialVersionUID = 1L;
	private JLabel label;
	
	public ApplicationStartMask(String info) {
		JPanel panel=new JPanel();
		label=new JLabel(info);
		label.setFont(new Font("宋体",Font.PLAIN,20));
		panel.setPreferredSize(new Dimension(300, 100));
		panel.setLayout(new BorderLayout(5, 5));
		panel.add(label,BorderLayout.CENTER);
		this.add(panel);
		this.pack();
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocationRelativeTo(null);
	}
	
	public void setLabel(String info){
		label.setText(info);
	}
	
}
