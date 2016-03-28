package com.devmel.apps.linkbuscontrol.devices;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.ParallelPort;
import gnu.io.PortInUseException;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Vector;

/**
 * The MachineIO class represents an 8 bit output port
 * 
 * <p>
 * The physical port is a parallel port (Output Pins 2-9).
 * Pins BUSY and PE (Paper End) must be connected to ground.
 * </p>
 * 
 */

public class MachineIO implements IDevice{
	private final String name;
	private int timeout;
	private ParallelPort parallelPort;

	private byte write = 0;
	private int sampleRate = 1000000;

	/**
	 * Constructs a MachineIO object.
	 * @param  name A native system port name
	 * @param  timeout A timeout in milliseconds
	 */
	public MachineIO(String name, int timeout){
		this.name = name;
		this.timeout = timeout;
	}
	/**
	 * Constructs a MachineIO object.
	 * @param  name A native system port name
	 */
	public MachineIO(String name){
		this(name, 5000);
	}

	@Override
	public boolean open() throws IOException{
		close();
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(name);
			CommPort commPort = portIdentifier.open( this.getClass().getName(), timeout );
	        if( commPort instanceof ParallelPort ) {
	        	this.parallelPort = (ParallelPort) commPort;
	        	if(parallelPort.isPaperOut()){
	    			throw new IOException("Pin PE (Paper End) must be connected to ground");
	        	}else if(parallelPort.isPrinterBusy()){
	    			throw new IOException("Pin BUSY must be connected to ground");
	        	}else if(parallelPort.isPrinterError()){
	    			throw new IOException("The port return an error");
	        	}
				parallelPort.setOutputBufferSize(0);
				parallelPort.setInputBufferSize(0);
	        }else{
	        	throw new NoSuchPortException();
	        }
		} catch (PortInUseException e) {
			throw new IOException("The port requested is currently in use");
		} catch (NoSuchPortException e) {
			throw new IOException("The requested Port does not exist");
		}
		return true;
	}
	
	@Override
	public boolean isOpen(){
		if(parallelPort != null){
			return true;
		}
		return false;
	}
	
	@Override
	public void close() {
		if(parallelPort != null){
			parallelPort.close();
			parallelPort=null;
		}
	}
	
	@Override
	public boolean isAnalogReadable() {
		return false;
	}
	@Override
	public boolean isAnalogWritable() {
		return false;
	}
	@Override
	public boolean isReadable() {
		return false;
	}
	@Override
	public boolean isWritable() {
		return true;
	}
	@Override
	public int getAnalogPinCount() {
		return 0;
	}
	@Override
	public int getIOPinCount() {
		return 8;
	}
	
    /**
     * Sets the port sampling period
     *
     * @param periodUs the period in microseconds 
     */
	@Override
	public void setSamplePeriod(int periodUs){
		this.setSampleRate(1000000/periodUs);
	}
	
    /**
     * Sets the port sampling rate
     *
     * @param rateHz the rate in Hertz (Between 200Hz and 1Mhz)
     */
	@Override
	public void setSampleRate(int rateHz){
		sampleRate = rateHz;
	}

	@Override
	public int getDirection() throws IOException {
		return 0xff;
	}
	
	@Override
	public int getValue() throws IOException {
		return write&0xff;
	}

	/**
	 * Writes the specified byte array to the port
	 *
	 * @param  data the data
	 * @throws java.io.IOException if an I/O error occurs.
	 * @return the number of bytes written
	 */
	@Override
	public int write(byte[] data) throws IOException{
		int ret = 0;
		if(parallelPort!=null && data!=null){
            int period = 1000000;
            period /= sampleRate;
            period *= 1000;
            long end = System.nanoTime();
			OutputStream outputStream = parallelPort.getOutputStream();
            for(int i=0; i<data.length; i++){
            	write = data[i];
                outputStream.flush();
                while(end > System.nanoTime());
                outputStream.write(new byte[]{write});
                end = System.nanoTime() + period;
                ret++;
            }
		}
		return ret;
	}

	/**
	 * Writes the specified byte array to the pin
	 *
	 * @param  pinNumber the pin number (0-7)
	 * @param  data the data
	 * @throws java.io.IOException if an I/O error occurs.
	 * @return the number of bytes written
	 */
	@Override
	public int write(int pinNumber, byte[] data) throws IOException{
		int ret = 0;
		if(parallelPort!=null && data!=null){
            int period = 1000000;
            period /= sampleRate;
            period *= 1000;
            long end = System.nanoTime();
			OutputStream outputStream = parallelPort.getOutputStream();
            for(int i=0; i<data.length; i++){
                for(int j=7; j>=0; j--){
                	write &= ~(1 << pinNumber);
                	if((data[i]& (1<<j)) != 0){
                		write |= (1 << pinNumber);
                	}
                    outputStream.flush();
                    while(end > System.nanoTime());
                    outputStream.write(new byte[]{write});
                    end = System.nanoTime() + period;
                    ret++;
                }
            }
		}
		return ret;
	}
	
	@Override
	public byte[] read(int size) throws IOException {
		return null;
	}
	@Override
	public byte[] read(int pinNumber, int size) throws IOException {
		return null;
	}
	@Override
	public double[] readAnalog(int pinNumber, int size) throws IOException {
		return null;
	}
	@Override
	public int writeAnalog(int pinNumber, double[] data) throws IOException {
		return 0;
	}
	
	/**
	 * Returns an array of native system ports names
	 * 
	 * @return An array of native system ports names
	 */
	public static String[] list(){
		Vector<String> ports = new Vector<String>();
		Enumeration<?> portEnum = CommPortIdentifier.getPortIdentifiers();
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			if(currPortId.getPortType() == CommPortIdentifier.PORT_PARALLEL){
				ports.add(currPortId.getName());
			}
		}
		String[] ret = new String[ports.size()];
		ports.toArray(ret);
		return ret;
	}
}
