/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.sydsoft.sg_gol.system;

import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.JFrame;

@SuppressWarnings("serial")
public class AppSettings extends HashMap<String, Object> {

	private static final AppSettings	defaults			= new AppSettings(false);
	private String						settingsDialogImage	= "/media/gui/Logo.png";
	private String						uniqueAppName;

	static {
		defaults.put("FirstRun", true);
		defaults.put("XPos", 100);
		defaults.put("YPos", 100);
		defaults.put("Width", 640);
		defaults.put("Height", 480);
		defaults.put("Fullscreen", false);
		defaults.put("AlienborderActive", true);
		defaults.put("ExtendedState", JFrame.NORMAL);
		defaults.put("Title", "empty Titel");
	}

	/**
	 * Create Application settings use loadDefault=true, to load jME default
	 * values. use false if you want to change some settings but you would like
	 * the application to remind other settings from previous launches
	 * 
	 * @param loadDefaults
	 */
	public AppSettings(boolean loadDefaults) {
		if (loadDefaults) {
			putAll(defaults);
		}
	}

	public void copyFrom(AppSettings other) {
		this.putAll(other);
	}

	public void mergeFrom(AppSettings other) {
		for (String key : other.keySet()) {
			if (get(key) == null) {
				put(key, other.get(key));
			}
		}
	}

	public void load(InputStream in) throws IOException {
		Properties props = new Properties();
		props.load(in);
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			String key = (String) entry.getKey();
			String val = (String) entry.getValue();
			if (val != null) {
				val = val.trim();
			}
			if (key.endsWith("(int)")) {
				key = key.substring(0, key.length() - 5);
				int iVal = Integer.parseInt(val);
				putInteger(key, iVal);
			} else if (key.endsWith("(string)")) {
				putString(key.substring(0, key.length() - 8), val);
			} else if (key.endsWith("(bool)")) {
				boolean bVal = Boolean.parseBoolean(val);
				putBoolean(key.substring(0, key.length() - 6), bVal);
			} else {
				throw new IOException("Cannot parse key: " + key);
			}
		}
	}

	public void save(OutputStream out) throws IOException {
		Properties props = new Properties();
		for (Map.Entry<String, Object> entry : entrySet()) {
			Object val = entry.getValue();
			String type;
			if (val instanceof Integer) {
				type = "(int)";
			} else if (val instanceof String) {
				type = "(string)";
			} else if (val instanceof Boolean) {
				type = "(bool)";
			} else {
				throw new UnsupportedEncodingException();
			}
			props.setProperty(entry.getKey() + type, val.toString());
		}
		props.store(out, "GoL AppSettings");
	}

	public void load(String preferencesKey) throws BackingStoreException {
		Preferences prefs = Preferences.userRoot().node(preferencesKey);
		String[] keys = prefs.keys();
		if (keys != null) {
			for (String key : keys) {
				Object defaultValue = defaults.get(key);
				if (defaultValue instanceof Integer) {
					put(key, prefs.getInt(key, (Integer) defaultValue));
				} else if (defaultValue instanceof String) {
					put(key, prefs.get(key, (String) defaultValue));
				} else if (defaultValue instanceof Boolean) {
					put(key, prefs.getBoolean(key, (Boolean) defaultValue));
				}
			}
		}
	}

	public void save(String preferencesKey) throws BackingStoreException {
		Preferences prefs = Preferences.userRoot().node(preferencesKey);
		for (String key : keySet()) {
			prefs.put(key, get(key).toString());
		}
	}

	public int getInteger(String key) {
		Integer i = (Integer) get(key);
		if (i == null) { return 0; }

		return i.intValue();
	}

	public boolean getBoolean(String key) {
		Boolean b = (Boolean) get(key);
		if (b == null) { return false; }

		return b.booleanValue();
	}

	public String getString(String key) {
		String s = (String) get(key);
		if (s == null) { return null; }

		return s;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		super.putAll(m);
		if (uniqueAppName != null) {
			try {
				save(uniqueAppName);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, uniqueAppName+"", e);
			}
		}
	}

	@Override
	public Object put(String key, Object value) {
		Object o = super.put(key, value);
		if (uniqueAppName != null) {
			try {
				save(uniqueAppName);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, uniqueAppName+"", e);
			}
		}
		return o;
	}

	public void putInteger(String key, int value) {
		put(key, Integer.valueOf(value));
	}

	public void putBoolean(String key, boolean value) {
		put(key, Boolean.valueOf(value));
	}

	public void putString(String key, String value) {
		put(key, value);
	}

	public void setLocation(int x, int y) {
		setXPosition(x);
		setYPosition(y);
	}

	public boolean isFirstRun() {
		boolean b = getBoolean("FirstRun");
		if (b) {
			putBoolean("FirstRun", false);
		}
		return b;
	}
	
	public void setYPosition(int y) {
		putInteger("YPos", y);
	}

	public void setXPosition(int x) {
		putInteger("XPos", x);
	}

	public void setWidth(int value) {
		putInteger("Width", value);
	}

	public void setHeight(int value) {
		putInteger("Height", value);
	}

	public void setResolution(int width, int height) {
		setWidth(width);
		setHeight(height);
	}

	public void setTitle(String title) {
		putString("Title", title);
	}

	public void setFullscreen(boolean value) {
		putBoolean("Fullscreen", value);
	}

	public void setAlienBorderActive(boolean value) {
		putBoolean("AlienborderActive", value);
	}

	public boolean getAlienBorderActive() {
		return getBoolean("AlienborderActive");
	}

	public Point getLocation() {
		Point p = new Point();
		p.x = getInteger("XPos");
		p.y = getInteger("YPos");
		return p;
	}
	
	public int getWidth() {
		return getInteger("Width");
	}

	public int getHeight() {
		return getInteger("Height");
	}
	
	public Dimension getSize(){
		Dimension d = new Dimension();
		d.height = getHeight();
		d.width = getWidth();
		return d;
	}
	
	public int getExtendedState(){
		return getInteger("ExtendedState");
	}
	
	public void setExtendedState(int state){
		putInteger("ExtendedState", state);
	}

	public String getTitle() {
		return getString("Title");
	}

	public boolean isFullscreen() {
		return getBoolean("Fullscreen");
	}

	public void setSettingsDialogImage(String path) {
		settingsDialogImage = path;
	}

	public String getSettingsDialogImage() {
		return settingsDialogImage;
	}

	public void turnOnAutoSave(String uniqueAppName) {
		this.uniqueAppName = uniqueAppName;
	}

	public void turnOffAutoSave() {
		uniqueAppName = null;
	}

}