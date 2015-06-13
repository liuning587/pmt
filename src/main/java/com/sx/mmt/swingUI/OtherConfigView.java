package com.sx.mmt.swingUI;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class OtherConfigView extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private JToolBar saveToolBar;
	private JButton saveButton;
	private JTextArea paramArea;
	
	public OtherConfigView(){
		ini();
		setAttribute();
		loadValue();
		setListener();
	}
	
	public void ini(){
		saveToolBar=new JToolBar();
		saveButton=new JButton("保存");
		paramArea=new JTextArea();
	}
	
	public void setAttribute(){
		this.setLayout(new BorderLayout(5, 5));
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		JScrollPane paramScrollPane=new JScrollPane();
		paramScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		paramScrollPane.setViewportView(paramArea);
		paramArea.setFont(new Font("Times New Roman",Font.PLAIN,16));
		saveButton.setFont(ViewConstants.TextFont);
		saveToolBar.add(saveButton);
		saveToolBar.setFloatable(false);
		this.add(saveToolBar,BorderLayout.NORTH);
		this.add(paramScrollPane,BorderLayout.CENTER);
	}
	
	public void loadValue(){
		PropertiesUtil propertiesUtil=(PropertiesUtil)SpringBeanUtil.getBean("propertiesUtil");
		paramArea.setText(propertiesUtil.show());
	}
	
	public void setListener(){
		final OtherConfigController otherConfigController=
				(OtherConfigController)SpringBeanUtil.getBean("otherConfigController");
		otherConfigController.setOtherConfigView(this);
		
		saveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				otherConfigController.saveHandler(e);
			}
        });
	}

	public JToolBar getSaveToolBar() {
		return saveToolBar;
	}

	public void setSaveToolBar(JToolBar saveToolBar) {
		this.saveToolBar = saveToolBar;
	}

	public JButton getSaveButton() {
		return saveButton;
	}

	public void setSaveButton(JButton saveButton) {
		this.saveButton = saveButton;
	}

	public JTextArea getParamArea() {
		return paramArea;
	}

	public void setParamArea(JTextArea paramArea) {
		this.paramArea = paramArea;
	}
	
	
}
