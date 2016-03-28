package com.devmel.apps.linkbuscontrol.devices;

import java.io.IOException;

import com.devmel.communication.linkbus.IOPort;
import com.devmel.storage.SimpleIPConfig;

public class LinkBusIO extends IOPort implements IDevice {

	public LinkBusIO(SimpleIPConfig device) {
		super(device);
	}

	@Override
	public boolean isAnalogReadable() {
		return true;
	}

	@Override
	public boolean isAnalogWritable() {
		return false;
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean isWritable() {
		return true;
	}

	@Override
	public int getAnalogPinCount() {
		return 3;
	}

	@Override
	public int getIOPinCount() {
		return 8;
	}

	@Override
	public int writeAnalog(int pinNumber, double[] data) throws IOException {
		return 0;
	}

}
