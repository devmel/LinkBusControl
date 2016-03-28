package com.devmel.apps.linkbuscontrol.devices;

import java.io.IOException;

public interface IDevice {	
	public boolean open() throws IOException;
	public void close();
	public boolean isOpen();
	
	public boolean isAnalogReadable();
	public boolean isAnalogWritable();
	public boolean isReadable();
	public boolean isWritable();
	
	public int getAnalogPinCount();
	public int getIOPinCount();
	
	public void setSamplePeriod(int periodUs);
	public void setSampleRate(int rateHz);
	public int getDirection() throws IOException;
	public int getValue() throws IOException;
	public byte[] read(int size) throws IOException;
	public byte[] read(int pinNumber, int size) throws IOException;
	public double[] readAnalog(int pinNumber, int size) throws IOException;
	public int write(byte[] data) throws IOException;
	public int write(int pinNumber, byte[] data) throws IOException;
	public int writeAnalog(int pinNumber, double[] data) throws IOException;
	
}
