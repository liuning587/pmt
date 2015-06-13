package com.sx.mmt.swingUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

import org.springframework.asm.Label;

import com.sx.mmt.constants.AppParameterDao;
import com.sx.mmt.constants.ConfigConstants;
import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.util.PropertiesUtil;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class TaskManagerFootView extends JToolBar{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<Integer> pagePerNumberComboBox;
	private JLabel taskStatic;
	private JPanel hSpacer;
	private JButton firstPageButton;
	private JButton pageUpButton;
	private JTextField pageNumberField;
	private JButton pageDownButton;
	private JButton lastPageButton;
	private JTextField totalPage;
	
	public TaskManagerFootView(){
		ini();
		setAttribute();
		setListener();
	}
	
	public void ini(){
		totalPage=new JTextField("1");
		Integer[] pageNumberForSelect=new Integer[]{100,200,500,1000};
		pagePerNumberComboBox = new JComboBox<Integer>(pageNumberForSelect);
		hSpacer = new JPanel(null);
		firstPageButton = new JButton();
		pageUpButton = new JButton();
		pageNumberField = new JTextField();
		pageDownButton = new JButton();
		lastPageButton = new JButton();
	}
	
	public void setAttribute(){
		this.setBorder(new EmptyBorder(5, 5, 15, 15));

		this.add(new JLabel("每页任务数"));
		taskStatic=new JLabel();
		pagePerNumberComboBox.setPreferredSize(new Dimension(60, 27));
		pagePerNumberComboBox.setMinimumSize(new Dimension(60, 27));
		pagePerNumberComboBox.setMaximumSize(new Dimension(60, 27));
		this.add(pagePerNumberComboBox);
		this.add(new JLabel("   共"));
		totalPage.setPreferredSize(new Dimension(35, 27));
		totalPage.setMaximumSize(new Dimension(35, 27));
		totalPage.setMinimumSize(new Dimension(35, 27));
		totalPage.setEditable(false);
		this.add(totalPage);
		this.add(new JLabel("页       "));
		this.add(taskStatic);
		this.add(hSpacer);
		
		Dimension dimension2727=new Dimension(27,27);
		firstPageButton.setText("<<");
		firstPageButton.setMaximumSize(dimension2727);
		firstPageButton.setMinimumSize(dimension2727);
		firstPageButton.setPreferredSize(dimension2727);
		firstPageButton.setToolTipText("转至第一页");
		this.add(firstPageButton);

		pageUpButton.setText("<");
		pageUpButton.setMaximumSize(dimension2727);
		pageUpButton.setMinimumSize(dimension2727);
		pageUpButton.setPreferredSize(dimension2727);
		pageUpButton.setToolTipText("上一页");
		this.add(pageUpButton);

		pageNumberField.setPreferredSize(new Dimension(35, 27));
		pageNumberField.setMaximumSize(new Dimension(35, 27));
		pageNumberField.setMinimumSize(new Dimension(35, 27));
		pageNumberField.setText("1");
		this.add(pageNumberField);

		pageDownButton.setText(">");
		pageDownButton.setMaximumSize(dimension2727);
		pageDownButton.setMinimumSize(dimension2727);
		pageDownButton.setPreferredSize(dimension2727);
		pageDownButton.setToolTipText("下一页");
		this.add(pageDownButton);

		lastPageButton.setText(">>");
		lastPageButton.setMaximumSize(dimension2727);
		lastPageButton.setMinimumSize(dimension2727);
		lastPageButton.setPreferredSize(dimension2727);
		lastPageButton.setToolTipText("转至最后一页");
		this.add(lastPageButton);
		for(java.awt.Component component:this.getComponents()){
			component.setFont(ViewConstants.TextFont);
		}
	}
	
	public void setListener(){
		final TaskManagerController taskManagerController=(TaskManagerController) 
				SpringBeanUtil.getBean("taskManagerController");
		
		firstPageButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.firstPageHandler(e);
			}
        });
		pageUpButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.pageUpButton(e);
			}
        });
		pageNumberField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e){
				if(e.getKeyChar()==KeyEvent.VK_ENTER){
					taskManagerController.gotoSpecifiedPage(e);
				}
			}
		});
		pageDownButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.pageDownButton(e);
			}
        });
		lastPageButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.lastPageButton(e);
			}
        });
		
		pagePerNumberComboBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange()==ItemEvent.SELECTED){
					taskManagerController.numberPerPageChangeHandler(e);
				}
			}
		});
		
	}

	public int getPage(){
		int page=1;
		try{
			page=Integer.parseInt(pageNumberField.getText());
		}catch(Exception e){
			page=1;
			pageNumberField.setText("1");
		}
		if(page<=0){
			page=1;
			pageNumberField.setText("1");
		}
		return page;
	}
	
	public void loadValue(){
		pagePerNumberComboBox.setSelectedItem(
				AppParameterDao.getIntValue(ConfigConstants.ViewPagePerTask));
	}

	public JComboBox<Integer> getPagePerNumberComboBox() {
		return pagePerNumberComboBox;
	}

	public void setPagePerNumberComboBox(JComboBox<Integer> pagePerNumberComboBox) {
		this.pagePerNumberComboBox = pagePerNumberComboBox;
	}

	public JButton getFirstPageButton() {
		return firstPageButton;
	}

	public void setFirstPageButton(JButton firstPageButton) {
		this.firstPageButton = firstPageButton;
	}

	public JButton getPageUpButton() {
		return pageUpButton;
	}

	public void setPageUpButton(JButton pageUpButton) {
		this.pageUpButton = pageUpButton;
	}

	public JTextField getPageNumberField() {
		return pageNumberField;
	}

	public void setPageNumberField(JTextField pageNumberField) {
		this.pageNumberField = pageNumberField;
	}

	public JButton getPageDownButton() {
		return pageDownButton;
	}

	public void setPageDownButton(JButton pageDownButton) {
		this.pageDownButton = pageDownButton;
	}

	public JButton getLastPageButton() {
		return lastPageButton;
	}

	public void setLastPageButton(JButton lastPageButton) {
		this.lastPageButton = lastPageButton;
	}

	public JTextField getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(JTextField totalPage) {
		this.totalPage = totalPage;
	}

	public JLabel getTaskStatic() {
		return taskStatic;
	}

	public void setTaskStatic(JLabel taskStatic) {
		this.taskStatic = taskStatic;
	}
	
	
	
	
	
	
}
