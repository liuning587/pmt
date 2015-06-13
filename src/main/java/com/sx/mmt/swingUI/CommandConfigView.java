package com.sx.mmt.swingUI;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class CommandConfigView extends JTabbedPane{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Font defaultFont=new Font("宋体",Font.PLAIN,14);
	private FileUpdateCommandView fileUpdateCommandView;
	private QueryVersionCommandView queryVersionCommandView;
	private PortShiftCommandView portShiftCommandView;
	private DelayCommandView delayCommandView;
	private FileTransferCommandView fileTransferCommandView;
	private TerminalParameterReadCommandView terminalParameterReadCommandView;
	private CustomMessageCommandView customMessageCommandView;
	
	public CommandConfigView(){
		ini();
		setAttribute();
		setListener();
	}
	
	public void ini(){
		fileUpdateCommandView=new FileUpdateCommandView();
		queryVersionCommandView=new QueryVersionCommandView();
		portShiftCommandView=new PortShiftCommandView();
		fileTransferCommandView=new FileTransferCommandView();
		delayCommandView=new DelayCommandView();
		terminalParameterReadCommandView=new TerminalParameterReadCommandView();
		customMessageCommandView=new CustomMessageCommandView();
	}
	
	public void setAttribute(){
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.addTab("文件升级",fileUpdateCommandView);
		this.addTab("版本号查询", queryVersionCommandView);
		this.addTab("端口切换", portShiftCommandView);
		this.addTab("文件传输", fileTransferCommandView);
		this.addTab("自定义报文",customMessageCommandView);
		this.addTab("终端档案读取", terminalParameterReadCommandView);
		this.addTab("延时命令", delayCommandView);
		this.setFont(defaultFont);
		
	}
	
	public void setListener(){
		this.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				if(((JTabbedPane)e.getSource()).getSelectedIndex()==1){
					queryVersionCommandView.refreshValue();
				}
			}
		});
	}

	public FileUpdateCommandView getFileUpdateCommandView() {
		return fileUpdateCommandView;
	}

	public void setFileUpdateCommandView(FileUpdateCommandView fileUpdateCommandView) {
		this.fileUpdateCommandView = fileUpdateCommandView;
	}

	public QueryVersionCommandView getQueryVersionCommandView() {
		return queryVersionCommandView;
	}

	public void setQueryVersionCommandView(
			QueryVersionCommandView queryVersionCommandView) {
		this.queryVersionCommandView = queryVersionCommandView;
	}

	public PortShiftCommandView getPortShiftCommandView() {
		return portShiftCommandView;
	}

	public void setPortShiftCommandView(PortShiftCommandView portShiftCommandView) {
		this.portShiftCommandView = portShiftCommandView;
	}

	public DelayCommandView getDelayCommandView() {
		return delayCommandView;
	}

	public void setDelayCommandView(DelayCommandView delayCommandView) {
		this.delayCommandView = delayCommandView;
	}
	
	
}
