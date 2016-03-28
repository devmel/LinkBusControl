package com.devmel.apps.linkbuscontrol.controller;

import java.util.HashSet;

import com.devmel.apps.linkbuscontrol.devices.MachineIO;
import com.devmel.apps.linkbuscontrol.view.IMainView;
import com.devmel.apps.linkbuscontrol.view.swing.DevicePanel;
import com.devmel.storage.IBase;
import com.devmel.storage.Node;
import com.devmel.storage.SimpleIPConfig;
import com.devmel.tools.IPAddress;

public class MainController {
	private final IBase baseStorage;
	private final IMainView view;
	private DeviceController subController;

	public MainController(IBase userPrefs, IMainView view) {
		this.baseStorage = userPrefs;
		this.view = view;
	}

	public void initialize() {
		if("LinkBus".equals(baseStorage.getString("configStart"))){
			addDeviceClick();
			baseStorage.saveString("configStart", "LB");
		}
		initializeDeviceList();
	}
	
	public void initializeDeviceList(){
		HashSet<String> list = new HashSet<String>();
		String[] sysDeviceList = MachineIO.list();
		if(sysDeviceList!=null){
			for(String devStr:sysDeviceList){
				list.add(devStr);
			}
		}
		Node devices = new Node(this.baseStorage, "Linkbus");
		String[] ipDeviceList = devices.getChildNames();
		if(ipDeviceList!=null){
			for(String devStr:ipDeviceList){
				list.add(devStr);
			}
		}
		String[] bList = new String[list.size()];
		list.toArray(bList);
		view.setListDevices(bList);
	}

	
	public void quitClick(){
		deviceUnselect();
		System.exit(0);
	}

	public void deviceSelect(final String name, int index) {
		deviceUnselect();
		if (name == null || name.equals("")) {
		}else{
			boolean deletable = true;
			Node devices = null;
			if (index < (MachineIO.list().length+1)){
				deletable = false;
				devices = new Node(this.baseStorage, "MachineIO");
			}else{
				devices = new Node(this.baseStorage, "Linkbus");
			}
			DevicePanel panel = new DevicePanel();
			subController = new DeviceController(devices, name, panel);
			panel.setController(subController);
			subController.initialize();
			this.view.setContent(panel, deletable);
		}
	}

	private void deviceUnselect(){
		if(subController!=null){
			subController.stop();
			subController = null;
		}
		this.view.setStart();
	}

	public void addDeviceClick() {
		this.view.addDeviceDialog();
	}

	public void addDeviceClick(final String name, final String localIP,
			final String password) {
		int err = 0;
		Node devices = new Node(this.baseStorage, "Linkbus");
		if (devices.isChildExist(name)) {
			err = -1;
		} else {
			try {
				byte[] ip = IPAddress.toBytes(localIP);
				if(name==null || name.length()==0){
					err = -2;
				}else if(ip==null){
					err = -3;
				}else if(password==null || password.length()==0){
					err = -4;
				}else{
					SimpleIPConfig device = new SimpleIPConfig(name);
					device.setIp(ip);
					device.setPassword(password);
					device.save(devices);
				}
				initializeDeviceList();
			} catch (Exception e1) {
				e1.printStackTrace();
				err = -5;
			}
		}
		if (err < 0) {
			this.view.addDeviceDialog(err, name, localIP, password);
		}
	}

	public void removeDeviceClick(final String name, final boolean confirm) {
		if (confirm == true) {
			deviceUnselect();
			Node devices = new Node(this.baseStorage, "Linkbus");
			devices.removeChild(name);
			initializeDeviceList();
			this.view.setStart();
		} else {
			this.view.removeDeviceConfirm(name);
		}
	}
}
