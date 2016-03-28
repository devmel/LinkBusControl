package com.devmel.apps.linkbuscontrol.view;

import com.devmel.apps.linkbuscontrol.modules.IModule;


public interface IDeviceView {
	public void setAddress(String value);
	public void setList(IModule... lst);
	public IStatusView getStatusView();
	
}
