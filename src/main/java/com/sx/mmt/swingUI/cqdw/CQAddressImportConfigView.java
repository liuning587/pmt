package com.sx.mmt.swingUI.cqdw;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class CQAddressImportConfigView extends JDialog{
	private static final long serialVersionUID = 1L;
	private JFormattedTextField district;
	private JFormattedTextField address;
	private JTextField separator;
	private JButton ok;
	private JButton cancel;
	
	public CQAddressImportConfigView(JDialog owner){
		super(owner,"导入配置",true);
		ini();
		setAttribute();
		loadValue();
		setListener();
	}
	
	public void ini(){
		district=new JFormattedTextField(NumberFormat.getIntegerInstance());
		address=new JFormattedTextField(NumberFormat.getIntegerInstance());
		separator=new JTextField();
		ok=new JButton("确定");
		cancel=new JButton("取消");
	}
	
	public void setAttribute(){
		this.setMinimumSize(new Dimension(200, 100));
		this.setPreferredSize(new Dimension(300, 200));
		GridBagLayout layout=new GridBagLayout();
		layout.columnWidths=new int[]{40,80,60,20,60,40};
		layout.rowHeights=new int[]{25,25,25,25,25,25};
		this.setLayout(layout);
		this.add(new JLabel("终端编码所在列"),getGBC(1,1,1,1));
		this.add(district,getGBC(2,1,3,1));
		this.add(new JLabel("终端地址所在列"),getGBC(1,2,1,1));
		this.add(address, getGBC(2,2,3,1));
//		this.add(new JLabel("分隔符"),getGBC(1,3,1,1));
//		this.add(separator, getGBC(2,3,3,1));
		this.add(ok, getGBC(2,4,1,1));
		this.add(cancel, getGBC(4,4,1,1));
		
		this.pack();
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(getOwner());
	}
	
	private GridBagConstraints getGBC(int x,int y,int xn,int yn){
		Insets insets=new Insets(2,2,2,2);
		return new GridBagConstraints(x,y,xn,yn,0.0,0.0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0);
	}
	
	public void loadValue(){
		district.setValue(Integer.parseInt(
				PropertiesUtil.parseToStr(ConfigConstants.ImportDistrictColumn)));
		address.setValue(Integer.parseInt(
				PropertiesUtil.parseToStr(ConfigConstants.ImportAddressColumn)));
		separator.setText(PropertiesUtil.parseToStr(ConfigConstants.ImportSeparator));
	}
	
	public void setListener(){
		final CQAddressImportConfigView me=this;
		ok.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
            	PropertiesUtil propertiesUtil=
                		(PropertiesUtil)SpringBeanUtil.getBean("propertiesUtil");
            	propertiesUtil.modify(ConfigConstants.ImportDistrictColumn,
            			String.valueOf(district.getValue()));
            	propertiesUtil.modify(ConfigConstants.ImportAddressColumn,
            			String.valueOf(address.getValue()));
            	propertiesUtil.modify(ConfigConstants.ImportSeparator,
            			separator.getText());
            	propertiesUtil.save();
            	me.setVisible(false);
			}
			
        });
		
		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				me.setVisible(false);
			}
        });
	}
	
}