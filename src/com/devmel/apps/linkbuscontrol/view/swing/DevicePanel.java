package com.devmel.apps.linkbuscontrol.view.swing;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.devmel.apps.linkbuscontrol.controller.DeviceController;
import com.devmel.apps.linkbuscontrol.modules.IModule;
import com.devmel.apps.linkbuscontrol.view.IDeviceView;
import com.devmel.apps.linkbuscontrol.view.IStatusView;

import java.awt.BorderLayout;
import java.awt.Component;

public class DevicePanel extends JPanel implements IDeviceView{
	private static final long serialVersionUID = -780416073382508188L;
	private final JPanel listContainer = new JPanel();
	private final JPanel outWrapper = new JPanel(new BorderLayout());
	private final JScrollPane listScrollPane = new JScrollPane(outWrapper);
	private final DeviceStatusPanel statusPanel = new DeviceStatusPanel();
	private DeviceController controller;

	/**
	 * Create the panel
	 */
	public DevicePanel() {
		setLayout(new BorderLayout());
		outWrapper.add(listContainer, BorderLayout.PAGE_START);
		listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
		listScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		listScrollPane.setBorder(null);
		add(statusPanel, BorderLayout.NORTH);
		add(listScrollPane, BorderLayout.CENTER);
	}
	
	public void setController(DeviceController controller) {
		this.controller = controller;
		statusPanel.setController(this.controller);
	}

	@Override
	public IStatusView getStatusView(){
		return statusPanel;
	}

	@Override
	public void setAddress(String value) {
		statusPanel.setAddress(value);
	}
	@Override
	public void setList(IModule... lst){
		if(lst!=null){
			listContainer.removeAll();
			for(int i=0;i<lst.length;i++){
				Component cpn = lst[i].getCell(i);
				if(cpn != null)
					listContainer.add(cpn);
			}
		}
	}
}
