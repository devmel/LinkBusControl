package com.devmel.apps.linkbuscontrol.modules;

import java.awt.Component;
import java.io.IOException;

public interface IModule {
	public boolean addAnalogPin(int number);
	public boolean addPin(int number);
	public void setLock(boolean enable);
	public void update() throws IOException;
	public Component getCell(int index);
}
