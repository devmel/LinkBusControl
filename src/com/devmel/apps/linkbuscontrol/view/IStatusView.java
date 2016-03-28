package com.devmel.apps.linkbuscontrol.view;

public interface IStatusView {
	public void startProgress();
	public void stopProgress();
	public void disconnected(String error);
}
