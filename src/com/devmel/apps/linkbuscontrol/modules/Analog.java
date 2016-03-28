package com.devmel.apps.linkbuscontrol.modules;

import java.awt.Component;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import com.devmel.apps.linkbuscontrol.R;
import com.devmel.apps.linkbuscontrol.controller.ModuleController;
import com.devmel.apps.linkbuscontrol.devices.IDevice;
import com.devmel.apps.linkbuscontrol.devices.LinkBusIO;

public class Analog implements IModule{
	private int pin = -1;
	private final ModuleController controller;
	private final JLabel valDisplay = new JLabel();
	
	public Analog(ModuleController controller){
		this.controller = controller;
	}
	
	@Override
	public void setLock(boolean enable) {
	}

	@Override
	public boolean addAnalogPin(int number){
		if(pin == -1){
			pin = number;
			return true;
		}
		return false;
	}
	@Override
	public boolean addPin(int number){
		return false;
	}
	
	@Override
	public void update() throws IOException{
		IDevice device = controller.getDevice();
		if(device.isOpen()){
			double value = round(readAnalogAVG(device, pin), 3);
			valDisplay.setText(value+"");
		}
	}
	
	@Override
	public Component getCell(int index){
		JPanel componant = new JPanel();
		String pinName = ""+pin;
		if(controller.getDevice() instanceof LinkBusIO){
			if(LinkBusIO.ANALOG_PAGEL == pin){
				pinName = "PAGEL";
			}else if(LinkBusIO.ANALOG_RDY == pin){
				pinName = "RDY";
			}else if(LinkBusIO.ANALOG_XTAL1 == pin){
				pinName = "XTAL1";
			}
		}
		componant.setLayout(new GridBagLayout());
		componant.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), R.bundle.getString("analog")+" "+pinName));
		componant.add(valDisplay);
		return componant;
	}
	
    public static double readAnalogAVG(IDevice ioPort, int channel) throws IOException {
        double[] read = ioPort.readAnalog(channel, 128);
        double val = 0;
        for(int i=0;i<read.length;i++){
            val += read[i];
        }
        val /= read.length;
        return val;
    }

	private static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}

}
