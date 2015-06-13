package com.sx.mmt.swingUI;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sx.mmt.constants.ViewConstants;

public class SystemConfigView extends JTabbedPane{

	private static final long serialVersionUID = 1L;
	private ConnectionConfigView connectionConfigView;
	private OverallParametersView overallParametersView;
	private OtherConfigView otherConfigView;
	
	public SystemConfigView(){
		ini();
		setAttribute();
		setListener();
	}
	
	public void ini(){
		connectionConfigView=new ConnectionConfigView();
		overallParametersView=new OverallParametersView();
		otherConfigView=new OtherConfigView();
	}
	
	public void setAttribute(){
		this.setFont(ViewConstants.TextFont);
		this.addTab("连接方式", connectionConfigView);
		this.addTab("全局参数", overallParametersView);
		this.addTab("其他参数", otherConfigView);
	}
	public void setListener(){
		this.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(((SystemConfigView)e.getSource()).getSelectedIndex()==1){
					otherConfigView.loadValue();
				}else if(((SystemConfigView)e.getSource()).getSelectedIndex()==0){
					connectionConfigView.loadValue();
				}
				
			}
		});
	}
}
