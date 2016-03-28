package com.devmel.apps.linkbuscontrol.view.swing;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.devmel.apps.linkbuscontrol.R;
import com.devmel.apps.linkbuscontrol.controller.DeviceController;
import com.devmel.apps.linkbuscontrol.view.IStatusView;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DeviceStatusPanel extends JPanel implements IStatusView {
	private static final long serialVersionUID = -8341191353474169184L;
	private DeviceController controller;
	private JProgressBar progressBar;
	private JButton btnConnect;
	private JLabel lblStringIP;
	private JLabel lblMsg;

	/**
	 * Create the panel.
	 */
	public DeviceStatusPanel() {
		GridBagLayout gbl_panelCommands = new GridBagLayout();
		gbl_panelCommands.columnWidths = new int[]{75, 146, 88, 0};
		gbl_panelCommands.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		setLayout(gbl_panelCommands);
				
		lblStringIP = new JLabel();
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 0;
		add(lblStringIP, gbc_lblNewLabel);
		
		progressBar = new JProgressBar();
		GridBagConstraints gbc_progressBar = new GridBagConstraints();
		gbc_progressBar.anchor = GridBagConstraints.NORTH;
		gbc_progressBar.insets = new Insets(0, 0, 5, 5);
		gbc_progressBar.gridx = 1;
		gbc_progressBar.gridy = 1;
		progressBar.setIndeterminate(true);
		add(progressBar, gbc_progressBar);
		progressBar.setVisible(false);
		
		btnConnect = new JButton(R.bundle.getString("connect"));
		btnConnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(controller!=null){
					controller.connectClick();
				}
			}
		});
		GridBagConstraints gbc_btnConnect = new GridBagConstraints();
		gbc_btnConnect.anchor = GridBagConstraints.NORTH;
		gbc_btnConnect.insets = new Insets(0, 0, 5, 5);
		gbc_btnConnect.gridx = 1;
		gbc_btnConnect.gridy = 1;
		add(btnConnect, gbc_btnConnect);
		
		lblMsg = new JLabel();
		lblMsg.setForeground(Color.RED);
		GridBagConstraints gbc_lblMsg = new GridBagConstraints();
		gbc_lblMsg.insets = new Insets(0, 0, 0, 5);
		gbc_lblMsg.gridx = 1;
		gbc_lblMsg.gridy = 2;
		add(lblMsg, gbc_lblMsg);
		btnConnect.setVisible(false);
		
	}
	
	protected void setController(DeviceController controller) {
		this.controller = controller;
	}

	protected void setAddress(String value){
		if(value != null)
			lblStringIP.setText(R.bundle.getString("address")+" : "+value);
	}

	@Override
	public void startProgress(){
		lblMsg.setVisible(false);
		progressBar.setVisible(true);
		btnConnect.setVisible(false);
	}
	@Override
	public void stopProgress(){
		progressBar.setVisible(false);
		btnConnect.setVisible(false);
	}
	@Override
	public void disconnected(String error){
		if(error!=null){
			lblMsg.setText(error);
			lblMsg.setVisible(true);
		}else{
			lblMsg.setVisible(false);
		}
		progressBar.setVisible(false);
		btnConnect.setVisible(true);
	}
}
