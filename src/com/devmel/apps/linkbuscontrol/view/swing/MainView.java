package com.devmel.apps.linkbuscontrol.view.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.devmel.apps.linkbuscontrol.R;
import com.devmel.apps.linkbuscontrol.controller.MainController;
import com.devmel.apps.linkbuscontrol.view.IDeviceView;
import com.devmel.apps.linkbuscontrol.view.IMainView;
import com.devmel.apps.linkbuscontrol.view.swing.IPDeviceAddPanel;

public class MainView extends JFrame implements IMainView {
	private static final long serialVersionUID = -5239942192238846273L;
	private MainController controller;
	private JPanel startPanel;
	private JComboBox<String> deviceSelect;
	private JButton btnAjouter;
	private JButton btnSupprimer;
	
	public MainView(){
		this.setTitle(R.bundle.getString("app_name"));
		this.setBounds(100, 100, 450, 600);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setResizable(false);
		
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		
		//Read data from model
		deviceSelect = new JComboBox<String>();
		deviceSelect.setToolTipText(R.bundle.getString("select_device"));
		deviceSelect.addItemListener(new ListItemState());
		toolBar.add(deviceSelect);

		btnAjouter = new JButton(R.bundle.getString("add"));
		btnAjouter.addActionListener(new AddListener());
		toolBar.add(btnAjouter);

		btnSupprimer = new JButton(R.bundle.getString("del"));
		btnSupprimer.addActionListener(new RemoveListener());
		toolBar.add(btnSupprimer);

		this.getContentPane().add(toolBar, BorderLayout.NORTH);

		
		//startPanel
		startPanel = new JPanel();
		startPanel.setBackground(java.awt.Color.WHITE);
		JLabel lblNewLabel = new JLabel();
		startPanel.add(lblNewLabel);
	}
	
	public void setController(MainController controller) {
		this.controller = controller;
	}

	@Override
	public void setStart() {
		btnSupprimer.setEnabled(false);
		replaceMainContent(startPanel);
	}

	@Override
	public void setListDevices(String[] list) {
		deviceSelect.removeAllItems();
		deviceSelect.addItem("");
		if(list!=null){
			for (String item : list) {
				deviceSelect.addItem(item);
			}
		}
		this.repaint();
	}

	@Override
	public void setContent(IDeviceView panel, boolean deletable){
		if(panel!=null && panel instanceof JPanel){
			replaceMainContent((JPanel)panel);
			btnSupprimer.setEnabled(deletable);
		}else{
			setStart();
		}
	}

	@Override
	public void addDeviceDialog() {
		addDeviceDialog(0 , "LinkBus_", "fe80::dcf6:e5ff:fe", null);
	}
	
	@Override
	public void addDeviceDialog(int error, String name, String ip, String password){
		String err = null;
		if(error==-1){
			err = R.bundle.getString("errorNameExists");
		}else if(error==-2){
			err = R.bundle.getString("errorNameInvalid");
		}else if(error==-3){
			err = R.bundle.getString("errorIPInvalid");
		}else if(error==-4){
			err = R.bundle.getString("errorPasswordInvalid");
		}else if(error==-5){
			err = R.bundle.getString("errorUnknown");
		}
		IPDeviceAddPanel panel = new IPDeviceAddPanel(err,name,ip,password);
        int ret = JOptionPane.showConfirmDialog(null, panel, R.bundle.getString("addLB"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ret == JOptionPane.OK_OPTION) {
			if(controller!=null){
				controller.addDeviceClick(panel.getName(), panel.getIP(), panel.getPassword());
			}
        }
	}

	@Override
	public void removeDeviceConfirm(final String name) {
		int dialogButton = JOptionPane.YES_NO_OPTION;
		int ret = JOptionPane.showConfirmDialog (null, R.bundle.getString("confirmDel")+" "+name+" ?",R.bundle.getString("confirm"),dialogButton);
		if(ret==JOptionPane.OK_OPTION){
			controller.removeDeviceClick(name, true);
		}
	}

	private void replaceMainContent(JPanel content){
		//Delete Content pane
		int lastItem = this.getContentPane().getComponentCount();
		if(lastItem>1){
			this.getContentPane().remove(lastItem-1);
		}
		//Add componant
		this.getContentPane().add(content, BorderLayout.CENTER);
		this.validate();
		this.repaint();
	}
	
	private class ListItemState implements ItemListener {
		public void itemStateChanged(ItemEvent e) {
			final String itemName = e.getItem().toString();
			if(deviceSelect.getSelectedItem()!=null && (deviceSelect.getSelectedItem().toString()).equals(itemName)){
				if(controller!=null)
					controller.deviceSelect(itemName, deviceSelect.getSelectedIndex());
			}
		}
	}

	private class AddListener implements ActionListener { 
		public void actionPerformed(ActionEvent e) {
			if(controller!=null)
				controller.addDeviceClick();
			
		}
	}

	private class RemoveListener implements ActionListener { 
		public void actionPerformed(ActionEvent e) {
			final String itemName = deviceSelect.getSelectedItem().toString();
			if(controller!=null)
				controller.removeDeviceClick(itemName, false);
		}
	}
}
