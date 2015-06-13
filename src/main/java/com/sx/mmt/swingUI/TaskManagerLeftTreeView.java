package com.sx.mmt.swingUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import com.sx.mmt.constants.ViewConstants;
import com.sx.mmt.internal.task.TaskXmlResolver;
import com.sx.mmt.internal.util.SpringBeanUtil;

public class TaskManagerLeftTreeView extends JPanel{

	private static final long serialVersionUID = 1L;
	
	private JTree taskGroupTree;
	private JScrollPane taskGroupTreeScrollPane;
	private JToolBar taskSearchToolBar;
	private JTextField taskSearchField;
	private JButton taskSearchButton;
	private JPopupMenu taskGroupTreePopupMenu;
	private JMenuItem newGroupItem;
	private JMenuItem renameGroupItem;
	private JMenuItem deleteGroupItem;
	private JMenu changeTaskMenu;
	
	public TaskManagerLeftTreeView(){
		ini();
		setAttribute();
		setListener();
		loadMenuChangeTaskMenu();
	}
	public void ini(){
		this.setPreferredSize(new Dimension(200, 600));
		this.setLayout(new BorderLayout(5, 5));
		taskGroupTree = new JTree();
		taskGroupTreeScrollPane=new JScrollPane();
		taskSearchToolBar=new JToolBar();
		taskSearchField = new JTextField();
		taskSearchButton= new JButton();
		taskGroupTreePopupMenu=new JPopupMenu();
		newGroupItem=new JMenuItem("新建分组");
		renameGroupItem=new JMenuItem("重命名");
		deleteGroupItem=new JMenuItem("删除分组");
		changeTaskMenu=new JMenu("更改任务类型");
	}
	
	public void setAttribute(){
		taskGroupTree.setFont(ViewConstants.TextFont);
		taskGroupTree.putClientProperty("JTree.lineStyle", "None");
		taskGroupTree.setShowsRootHandles(false);
		taskGroupTree.setEditable(false);
		taskGroupTree.setCellRenderer(new MyTreeCellRenderer());
		taskGroupTreeScrollPane.setPreferredSize(new Dimension(200, 600));
		taskGroupTreeScrollPane.setBorder(new EmptyBorder(5, 15, 15, 5));
		taskGroupTreeScrollPane.setViewportView(taskGroupTree);
		this.add(taskGroupTreeScrollPane, BorderLayout.CENTER);
		taskSearchToolBar.setBorder(new EmptyBorder(15, 15, 5, 5));
		taskSearchToolBar.add(taskSearchField);
		taskSearchField.setToolTipText("任务过滤");
		taskSearchField.setFont(ViewConstants.TitleFont);
		taskSearchButton.setIcon(new ImageIcon(getClass().getResource("/img/Search.png")));
		taskSearchToolBar.add(taskSearchButton);
		this.add(taskSearchToolBar, BorderLayout.NORTH);
		UIManager.put("Tree.collapsedIcon", new ImageIcon(getClass().getResource("/img/Collapsed.png")));
		UIManager.put("Tree.expandedIcon", new ImageIcon(getClass().getResource("/img/Expanded.png")));
		newGroupItem.setIconTextGap(20);
		renameGroupItem.setIconTextGap(20);
		deleteGroupItem.setIconTextGap(20);
		changeTaskMenu.setIconTextGap(20);
		taskGroupTreePopupMenu.setBorderPainted(true);
		taskGroupTreePopupMenu.setPopupSize(150, 120);
		taskGroupTreePopupMenu.add(newGroupItem);
		taskGroupTreePopupMenu.add(renameGroupItem);
		taskGroupTreePopupMenu.add(deleteGroupItem);
		taskGroupTreePopupMenu.addSeparator();
		taskGroupTreePopupMenu.add(changeTaskMenu);
		taskGroupTree.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}
	
	public void setListener(){
		final TaskManagerController taskManagerController=(TaskManagerController) 
				SpringBeanUtil.getBean("taskManagerController");
		taskGroupTree.addMouseListener(new MouseAdapter() {		
			@Override
			public void mouseReleased(MouseEvent e) {
				taskManagerController.treePopMenuHandler(e);
			}
		});
		
		taskGroupTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				taskManagerController.treeSelectionChange(e);
			}
		});
		
		newGroupItem.addMouseListener(new MouseAdapter() {		
			@Override
			public void mousePressed(MouseEvent e) {
				taskManagerController.addANewgroupHandler(e);
			}
		});
		renameGroupItem.addMouseListener(new MouseAdapter() {		
			@Override
			public void mousePressed(MouseEvent e) {
				taskManagerController.renameAGroupHandler(e);
			}
		});
		deleteGroupItem.addMouseListener(new MouseAdapter() {		
			@Override
			public void mousePressed(MouseEvent e) {
				taskManagerController.deleteAGroupHandler(e);
			}
		});
		
		taskSearchButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				taskManagerController.loadTree();
				taskManagerController.loadTaskTable();
			}
        });
		
		taskSearchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e){
				if(e.getKeyChar()==KeyEvent.VK_ENTER){
					taskManagerController.loadTree();
					taskManagerController.loadTaskTable();
				}
			}
		});
	}
	
	public void loadMenuChangeTaskMenu(){
		changeTaskMenu.removeAll();
		final TaskManagerController taskManagerController=(TaskManagerController) 
				SpringBeanUtil.getBean("taskManagerController");
		TaskXmlResolver taskXmlResolver=
				(TaskXmlResolver)SpringBeanUtil.getBean("taskXmlResolver");
		for(final String s:taskXmlResolver.getTasks().keySet()){
			JMenuItem item=new JMenuItem(s);
			item.setIconTextGap(20);
			item.addMouseListener(new MouseAdapter() {		
				@Override
				public void mousePressed(MouseEvent e) {
					taskManagerController.changeTaskTypeByGroupHandler(s);
				}
			});
			changeTaskMenu.add(item);
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void expandAll(JTree tree, TreePath parent, boolean expand) {
	   TreeNode node = (TreeNode)parent.getLastPathComponent();
	   if (node.getChildCount() >= 0) {
	       for (Enumeration e=node.children(); e.hasMoreElements(); ) {
	           TreeNode n = (TreeNode)e.nextElement();
	           TreePath path = parent.pathByAddingChild(n);
	           expandAll(tree, path, expand);
	       }
	   }
	   if(parent!=null){
		   if (expand) {
		       tree.expandPath(parent);
		   } else {
		       tree.collapsePath(parent);
		   }
	   }
	}
	
	public DefaultTreeModel getTreeModel(){
	    return (DefaultTreeModel)taskGroupTree.getModel();
	}
	
	public JTree getTaskGroupTree() {
		return taskGroupTree;
	}
	public void setTaskGroupTree(JTree taskGroupTree) {
		this.taskGroupTree = taskGroupTree;
	}

	public JTextField getTaskSearchField() {
		return taskSearchField;
	}
	public void setTaskSearchField(JTextField taskSearchField) {
		this.taskSearchField = taskSearchField;
	}
	public JButton getTaskSearchButton() {
		return taskSearchButton;
	}
	public void setTaskSearchButton(JButton taskSearchButton) {
		this.taskSearchButton = taskSearchButton;
	}
	public JPopupMenu getTaskGroupTreePopupMenu() {
		return taskGroupTreePopupMenu;
	}
	public void setTaskGroupTreePopupMenu(JPopupMenu taskGroupTreePopupMenu) {
		this.taskGroupTreePopupMenu = taskGroupTreePopupMenu;
	}
	public JMenuItem getNewGroupItem() {
		return newGroupItem;
	}
	public void setNewGroupItem(JMenuItem newGroupItem) {
		this.newGroupItem = newGroupItem;
	}
	public JMenuItem getRenameGroupItem() {
		return renameGroupItem;
	}
	public void setRenameGroupItem(JMenuItem renameGroupItem) {
		this.renameGroupItem = renameGroupItem;
	}
	public JMenuItem getDeleteGroupItem() {
		return deleteGroupItem;
	}
	public void setDeleteGroupItem(JMenuItem deleteGroupItem) {
		this.deleteGroupItem = deleteGroupItem;
	}
	
	public class MyTreeCellRenderer extends DefaultTreeCellRenderer{
		private static final long serialVersionUID = 1L;

		@Override
		public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected,boolean expanded,boolean leaf, int row,boolean hasFocus){
			super.getTreeCellRendererComponent(tree, value, selected, expanded,leaf, row, hasFocus);
			if(selected){
				ImageIcon ii=new ImageIcon(getClass().getResource("/img/foldOpen.png"));
				this.setIcon(ii);
			}else{
				this.setIcon(new ImageIcon(getClass().getResource("/img/foldClose.png")));
			}
			return this;
		}

	}
	
	
	
	

}
