package com.sx.mmt.swingUI;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.sx.mmt.internal.connection.ConnectConfig;
import com.sx.mmt.internal.connection.ConnectXmlResolver;
import com.sx.mmt.internal.connection.ConnectionManager;
import com.sx.mmt.internal.util.ErrorTool;
import com.sx.mmt.internal.util.SpringBeanUtil;
import com.sx.mmt.swingUI.ConnectionConfigView.ConfigPanel;


@Component
public class ConnectionConfigController {
	private ConnectionConfigView connectionConfigView;
	@Autowired
	private ConnectXmlResolver ConnectXmlResolver;
	@Autowired
	private ConnectionManager connectionManager;

	public ConnectionConfigView getConnectionConfigView() {
		return connectionConfigView;
	}

	public void setConnectionConfigView(ConnectionConfigView connectionConfigView) {
		this.connectionConfigView = connectionConfigView;
	}
	
	public void saveHandler(ActionEvent e){
		ConnectConfig config=connectionConfigView.getConnectList().getSelectedValue();
		ConfigPanel panel=connectionConfigView.getConnectPanelList().get(config.getName());
		config.setAttr(panel.getValue());
		List<String> keys=Lists.newArrayList(ConnectXmlResolver.getconnects().keySet());
		for(String s:keys){
			ConnectConfig cf=ConnectXmlResolver.get(s);
			if(cf.isUse()){
				cf.setUse(false);
				ConnectXmlResolver.modify(cf);
			}
		}
		config.setUse(true);
		ConnectXmlResolver.modify(config);
		connectionConfigView.getConnectList().updateUI();
		
		try{
			connectionManager.switchConnection(config);
			JOptionPane.showMessageDialog(connectionConfigView, "保存并切换成功");
		}catch(Exception e1){
			ErrorTool.getErrorInfoFromException(e1);
			JOptionPane.showMessageDialog(connectionConfigView, "保存成功,但连接失败");
		}
		
		
	}
	
	public void testConnectHandler(ActionEvent e){
		ConnectConfig config=connectionConfigView.getConnectList().getSelectedValue();
		if(connectionManager.testConnect(config)){
			JOptionPane.showMessageDialog(connectionConfigView, "连接成功");
		}else{
			JOptionPane.showMessageDialog(connectionConfigView, "连接失败");
			
		}
	}
}
