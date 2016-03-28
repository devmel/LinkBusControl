package com.devmel.apps.linkbuscontrol.controller;

import com.devmel.apps.linkbuscontrol.devices.IDevice;
import com.devmel.apps.linkbuscontrol.devices.LinkBusIO;
import com.devmel.apps.linkbuscontrol.devices.MachineIO;
import com.devmel.apps.linkbuscontrol.view.IDeviceView;
import com.devmel.storage.Node;
import com.devmel.storage.SimpleIPConfig;

public class DeviceController {
	private final ModuleController module;
	private final Node devices;
	private final IDeviceView deviceView;
	private final IDevice device;
	
	public DeviceController(Node devices, String name, IDeviceView deviceView) {
		this.devices = devices;
		SimpleIPConfig config = SimpleIPConfig.createFromNode(devices, name);
		if(config == null){
			config = new SimpleIPConfig(name);
		}
		this.deviceView = deviceView;
		this.device = initializeDevice(name, config);
		this.module = new ModuleController(deviceView.getStatusView(), device, this.devices);
		this.deviceView.setAddress(config.getIpAsText());
		
		this.module.initialize();
		if(this.module.modules != null)
			this.deviceView.setList(this.module.modules);
	}

	public void initialize(){
		module.start();
	}

	public void stop(){
		module.stop();
	}
	
	public void connectClick() {
		module.start();
	}

	private IDevice initializeDevice(String name, SimpleIPConfig config){
		IDevice ret = null;
		//Search device into MachineIO
		String[] sysDeviceList = MachineIO.list();
		for(String devStr:sysDeviceList){
			if(devStr.equals(name)){
				ret = new MachineIO(devStr);
				break;
			}
		}
		//Search device into LinkBus config
		if(ret == null){
			ret = new LinkBusIO(config);
		}
		return ret;
	}

}
