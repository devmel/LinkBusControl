package com.devmel.apps.linkbuscontrol.controller;

import java.io.IOException;
import java.util.LinkedList;

import com.devmel.apps.linkbuscontrol.R;
import com.devmel.apps.linkbuscontrol.devices.IDevice;
import com.devmel.apps.linkbuscontrol.modules.AirDemo;
import com.devmel.apps.linkbuscontrol.modules.Analog;
import com.devmel.apps.linkbuscontrol.modules.Digital;
import com.devmel.apps.linkbuscontrol.modules.IModule;
import com.devmel.apps.linkbuscontrol.view.IStatusView;
import com.devmel.storage.Node;

public class ModuleController {
	private final LinkedList<Runnable> actions = new LinkedList<Runnable>();
	private final IStatusView status;
	private final IDevice device;
	private final Node config;
	private boolean error = true;
	protected IModule[] modules;

	protected ModuleController(IStatusView status, IDevice device, Node config){
		this.status=status;
		this.device=device;
		this.config=config;
	}
	
	protected void initialize(){
		modules = new IModule[device.getAnalogPinCount() + device.getIOPinCount()];
		int i = 0;
		for(; i < device.getAnalogPinCount(); i++)
		{
			modules[i] = new Analog(this);
			modules[i].addAnalogPin(i);
		}
		for(int j = 0; i < modules.length; i++, j++)
		{
			if(j == 0){
				modules[i] = new AirDemo(this);
				modules[i].addPin(j);
			}else{
				modules[i] = new Digital(this);
				modules[i].addPin(j);
			}
		}
	}
	
	protected void start(){
		final long updateperiod = 20000;
		Thread t = new Thread(new Runnable() {
			public void run() {
				setLock(true);
				status.startProgress();
				actions.clear();
				if(device!=null){
					try {
						if(device.open()){
							error = false;
							update();
							long nextUpdate = System.currentTimeMillis() + updateperiod;
							setLock(false);
							status.stopProgress();
							while(actions!=null && error==false){
								Runnable act = actions.poll();
								if(act == null){
									if(System.currentTimeMillis() >= nextUpdate){
										update();
										nextUpdate = System.currentTimeMillis() + updateperiod;
									}
									act = actions.poll();
								}
								if(act == null && error==false){
									synchronized(actions){
										try {
											actions.wait(nextUpdate - System.currentTimeMillis());
										} catch (InterruptedException e) {
										}
										act = actions.poll();
									}
								}
								if(act!=null && error==false){
									act.run();
									if(error==false){
										setLock(false);
										status.stopProgress();
									}
								}
							}
						}else{
							setError(R.bundle.getString("error_open_failed"));
						}
					} catch (IOException e) {
						setError(e.getMessage());
					}finally{
						if(device!=null)
							device.close();
					}
				}
			}
		});
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}

	protected void stop(){
		setError(R.bundle.getString("error_closed"));
		actions.clear();
		synchronized(actions){
			actions.notify();
		}
	}

	protected void update() throws IOException{
		for(int i = 0; i< modules.length; i++){
			modules[i].update();
		}
	}

	protected void setLock(boolean lock){
		for(int i = 0; i< modules.length; i++){
			modules[i].setLock(lock);
		}
	}
	
	public void execute(final Runnable r) {
		setLock(true);
		status.startProgress();
		actions.add(r);
		synchronized(actions){
			actions.notify();
		}
	}
	
	public void setError(String err){
		error = true;
		setLock(true);
		status.disconnected(err);
	}
	
	public IDevice getDevice(){
		return device;
	}
	
	public Node getConfig(){
		return config;
	}
}
