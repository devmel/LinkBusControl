package com.devmel.apps.linkbuscontrol.modules;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.devmel.apps.linkbuscontrol.R;
import com.devmel.apps.linkbuscontrol.controller.ModuleController;
import com.devmel.apps.linkbuscontrol.devices.IDevice;

public class Digital implements IModule {
	private final ModuleController controller;
	private final JPanel panel = new JPanel();
	private final JToggleButton dirBtn = new JToggleButton();
	private final JToggleButton valBtn = new JToggleButton();
	private int pin = -1;
	private int lock = 0;

	public Digital(ModuleController controller) {
		this.controller = controller;
		panel.setLayout(new GridBagLayout());
		JLabel direction = new JLabel(R.bundle.getString("direction")+" : ");
		JLabel status = new JLabel(R.bundle.getString("status")+" : ");
		setInterface(false, false);
		setLock(true);
		panel.add(direction, createGridBag(0, 0));
		panel.add(dirBtn, createGridBag(1, 0));
		panel.add(status, createGridBag(2, 0));
		panel.add(valBtn, createGridBag(3, 0));

		ActionListener dirActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton) actionEvent
						.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				set(selected, false);
			}
		};
		dirBtn.addActionListener(dirActionListener);
		ActionListener valActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				AbstractButton abstractButton = (AbstractButton) actionEvent
						.getSource();
				boolean selected = abstractButton.getModel().isSelected();
				set(true, selected);
			}
		};
		valBtn.addActionListener(valActionListener);
	}

	@Override
	public void setLock(boolean enable) {
		if(enable == true){
			if(dirBtn.isEnabled()){
				lock |= 0x1;
			}
			if(valBtn.isEnabled()){
				lock |= 0x2;
			}
			dirBtn.setEnabled(false);
			valBtn.setEnabled(false);
		}else{
			if((lock&0x1) != 0){
				dirBtn.setEnabled(true);
			}
			if((lock&0x2) != 0){
				valBtn.setEnabled(true);
			}
			lock = 0;
		}
	}

	@Override
	public boolean addAnalogPin(int number) {
		return false;
	}

	@Override
	public boolean addPin(int number) {
		if(pin == -1){
			pin = number;
			String io = "I/O";
			IDevice device = controller.getDevice();
			if (device.isWritable() == false && device.isReadable() == true) {
				dirBtn.setEnabled(false);
				lock &= ~0x1;
				io = R.bundle.getString("input");
			} else if (device.isReadable() == false && device.isWritable() == true) {
				setInterface(true, false);
				dirBtn.setEnabled(false);
				lock &= ~0x1;
				io = R.bundle.getString("output");
			}
			panel.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(),
					R.bundle.getString("digital")+" " + io + " " + pin));
			return true;
		}
		return false;
	}

	@Override
	public void update() throws IOException {
		IDevice device = controller.getDevice();
		if (device.isOpen()) {
			boolean direction = ((device.getDirection() & (1 << pin)) != 0) ? true
					: false;
			boolean value = ((device.getValue() & (1 << pin)) != 0) ? true
					: false;
			setInterface(direction, value);
		}
	}

	@Override
	public Component getCell(int index) {
		return panel;
	}

	private void setInterface(boolean direction, boolean value) {
		if (direction == true) {
			if((lock&0x2) != 0)
				valBtn.setEnabled(true);
			lock |= 0x2;
			dirBtn.setText(R.bundle.getString("write"));
			dirBtn.setSelected(true);
		} else {
			lock &= ~0x2;
			valBtn.setEnabled(false);
			dirBtn.setText(R.bundle.getString("read"));
			dirBtn.setSelected(false);
		}
		if (value == true) {
			valBtn.setSelected(true);
			valBtn.setText(R.bundle.getString("on"));
		} else {
			valBtn.setSelected(false);
			valBtn.setText(R.bundle.getString("off"));
		}
	}

	private void set(final boolean direction, final boolean value) {
		controller.execute(new Runnable() {
			public void run() {
				IDevice device = controller.getDevice();
				if (device.isOpen()) {
					byte v = (byte) 0xff;
					if (value == false)
						v = 0;
					try {
						if (direction == true) {
							if (device.write(pin, new byte[] { v }) > 0) {
								setInterface(true, value);
							}else{
								setInterface(true, !value);
							}
						} else {
							byte[] val = device.read(pin, 1);
							if (val != null && val.length > 0) {
								if ((val[0]&0xff) > 0)
									setInterface(false, true);
								else
									setInterface(false, false);
							}else{
								setInterface(false, false);
							}
						}
					} catch (IOException e) {
						controller.setError(e.getMessage());
					}
				}
			}
		});
	}

	private static GridBagConstraints createGridBag(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		return gbc;
	}

}
