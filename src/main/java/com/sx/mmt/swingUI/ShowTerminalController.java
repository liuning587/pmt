package com.sx.mmt.swingUI;

import javax.swing.JTextArea;

import org.springframework.stereotype.Component;


@Component
public class ShowTerminalController {
	private ShowTerminalView showTerminalView;
	private static final int messagePoolSize=200;
	private static int count=0;
	
	public ShowTerminalController(){
		showTerminalView=new ShowTerminalView();
	}

	public ShowTerminalView getShowTerminalView() {
		return showTerminalView;
	}

	public void setShowTerminalView(ShowTerminalView showTerminalView) {
		this.showTerminalView = showTerminalView;
	}
	
	public void appendMessage(String message){
		JTextArea area=showTerminalView.getTextArea();
		if(count>messagePoolSize){
			area.setText("");
			count=0;
		}
		String filter=showTerminalView.getFilterField().getText();
		if(message.indexOf(filter)!=-1){
			count++;
			showTerminalView.getTextArea().append(message+"\r\n");
		}
	}
	
	public void show(){
		showTerminalView.setVisible(true);
	}
	
}
