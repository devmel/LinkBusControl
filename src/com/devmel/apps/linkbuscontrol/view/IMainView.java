package com.devmel.apps.linkbuscontrol.view;

public interface IMainView {
	public void setStart();
	public void setContent(IDeviceView panel, boolean deletable);
	public void setListDevices(String[] list);
	public void addDeviceDialog();
	public void addDeviceDialog(int error, String name, String localIP, String password);
	public void removeDeviceConfirm(final String name);
}
