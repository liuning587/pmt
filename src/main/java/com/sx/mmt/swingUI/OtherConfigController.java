package com.sx.mmt.swingUI;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.springframework.stereotype.Component;

import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SpringBeanUtil;


@Component
public class OtherConfigController {
	private OtherConfigView otherConfigView;

	public OtherConfigView getOtherConfigView() {
		return otherConfigView;
	}

	public void setOtherConfigView(OtherConfigView otherConfigView) {
		this.otherConfigView = otherConfigView;
	}
	
	public void saveHandler(ActionEvent e){
		PropertiesUtil propertiesUtil=
				(PropertiesUtil)SpringBeanUtil.getBean("propertiesUtil");
		propertiesUtil.save(otherConfigView.getParamArea().getText());
		JOptionPane.showMessageDialog(otherConfigView, "保存成功");
	}
}
