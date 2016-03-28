package com.devmel.apps;

import java.awt.EventQueue;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.devmel.apps.linkbuscontrol.R;
import com.devmel.apps.linkbuscontrol.controller.MainController;
import com.devmel.apps.linkbuscontrol.view.swing.MainView;
import com.devmel.storage.java.UserPrefs;


public class LinkBusControl {
	public final static String name = LinkBusControl.class.getSimpleName();
	private MainView mainView;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				//Load ressource
				Locale systemLocale = Locale.getDefault();
				Locale systemLang = new Locale(systemLocale.getDisplayLanguage(), "");
				Locale defaultLang = new Locale("en", "");
				try {
					R.bundle = ResourceBundle.getBundle("res."+name, systemLocale);
				} catch (Exception e) {
					try {
						R.bundle = ResourceBundle.getBundle("res."+name, systemLang);
					} catch (Exception e1) {
						try {
							R.bundle = ResourceBundle.getBundle("res."+name, defaultLang);
						} catch (Exception e2) {
							System.exit(-1);
						}
					}
				}
				java.net.URL icon = null;
				try {
					icon = getClass().getResource("/res/icon_app_32x32.png");
				} catch (Exception e) {
				}
				try {
					LinkBusControl window = new LinkBusControl();
					if(icon!=null){
						ImageIcon devmelIcon = new ImageIcon(icon);
						window.mainView.setIconImage(devmelIcon.getImage());
					}
					window.mainView.setLocationRelativeTo(null);
					window.mainView.setVisible(true);
				} catch (Throwable e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		});
	}
	
	public LinkBusControl(){
		//Build Model
		UserPrefs userPrefs = new UserPrefs(null);
//		userPrefs.clearAll();
		//Splash screen
		if(userPrefs.getString("configStart") == null){
			String[] options = {"Computer", "LinkBus"};
			int ret = JOptionPane.showOptionDialog(null, R.bundle.getString("select_device_port"), R.bundle.getString("select_port"), JOptionPane.NO_OPTION, JOptionPane.DEFAULT_OPTION, null, options , options[0]);
			if(ret < options.length){
				userPrefs.saveString("configStart", options[ret]);
			}
		}
		//Start controller
		mainView = new MainView();
		final MainController controller = new MainController(userPrefs, mainView);
		mainView.setController(controller);
		controller.initialize();
		mainView.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				controller.quitClick();
			}
		});
	}

}
