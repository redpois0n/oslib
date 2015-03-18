package com.redpois0n.oslib;

import java.util.List;

import com.redpois0n.oslib.windows.WindowsOperatingSystem;
import com.redpois0n.oslib.windows.WindowsVersion;

public class DEDetector {

	public static DesktopEnvironment detect() {
		AbstractOperatingSystem os = OperatingSystem.getOperatingSystem();
		
		DesktopEnvironment de = DesktopEnvironment.UNKNOWN;
		
		if (os.getType() == OperatingSystem.WINDOWS) {
			WindowsOperatingSystem wos = (WindowsOperatingSystem) os;
			
			if (wos.getVersion() == WindowsVersion.WIN7 || wos.getVersion() == WindowsVersion.WIN8 || wos.getVersion() == WindowsVersion.WIN81 || wos.getVersion() == WindowsVersion.WINSERVER2012 || wos.getVersion() == WindowsVersion.WINVISTA) {
				de = DesktopEnvironment.AERO;
			} else {
				de = DesktopEnvironment.LUNA;
			}
		} else if (os.getType() == OperatingSystem.OSX) {
			de = DesktopEnvironment.AQUA;
		} else {
			if (isSet("XDG_CURRENT_DESKTOP") && System.getenv("XDG_CURRENT_DESKTOP").equals("X-Cinnamon")) {
				de = DesktopEnvironment.CINNAMON;
			} else if (isSet("GNOME_DESKTOP_SESSION_ID")) {
				de = DesktopEnvironment.GNOME;
				
				boolean exists = false;
				
				try {
					exists = Utils.readProcess(new String[] { "type", "-p", "xprop" }).size() > 0;
					
					if (exists) {
						List<String> unity = Utils.readProcess(new String[] { "xprop", "-name", "unity-launcher" });
						
						if (!unity.get(0).toLowerCase().contains("error")) {
							de = DesktopEnvironment.UNITY;
							de.setVersion(Utils.readProcess(new String[] { "unity", "--version" }).get(0).split(" ")[0]);
						} else {
							unity = Utils.readProcess(new String[] { "xprop", "-name", "launcher" });
							
							if (!unity.get(0).toLowerCase().contains("error")) {
								unity = Utils.readProcess(new String[] { "xprop", "-name", "panel" });
								if (!unity.get(0).toLowerCase().contains("error")) {
									de = DesktopEnvironment.UNITY;
									de.setVersion(Utils.readProcess(new String[] { "unity", "--version" }).get(0).split(" ")[0]);
								}
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				if (de == DesktopEnvironment.GNOME) {
					String version = null;
					
					try {
						version = Utils.readProcess(new String[] { "gnome-session-properties", "--version" }).get(0).split(" ")[0];
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					
					if (version == null) {
						try {
							version = Utils.readProcess(new String[] { "gnome-session", "--version" }).get(0).split(" ")[0];
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
					
					de.setVersion(version);
				}
			} else if (isSet("MATE_DESKTOP_SESSION_ID")) {
				de = DesktopEnvironment.MATE;
				try {
					de.setVersion(Utils.readProcess(new String[] { "mate-session", "--version" }).get(0).split(" ")[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (isSet("KDE_SESSION_VERSION")) {
				String s = System.getenv("KDE_SESSION_VERSION");
				
				if (s.equals("4")) {
					de = DesktopEnvironment.KDE4;
				} else if (s.equals("5")) {
					de = DesktopEnvironment.KDE5;
				}
			} else if (isSet("KDE_FULL_SESSION")) {
				de = DesktopEnvironment.KDE;
			} else if (isSet("DESKTOP_SESSION")) {
				String s = System.getenv("DESKTOP_SESSION");
				
				if (s.equals("budgie-desktop")) {
					de = DesktopEnvironment.BUDGIE;
				} else if (s.equals("Cinnamon")) {
					de = DesktopEnvironment.CINNAMON;
					try {
						de.setVersion(Utils.readProcess(new String[] { "cinnamon", "--version" }).get(0).split(" ")[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return de;
	}
	
	public static boolean isSet(String s) {
		return System.getenv(s) != null;
	}
}
