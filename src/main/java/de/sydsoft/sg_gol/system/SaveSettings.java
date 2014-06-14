package de.sydsoft.sg_gol.system;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JProgressBar;

import de.sydsoft.libst.util.Console;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.sydsoft.sg_gol.entities.Alien;

public class SaveSettings {
	private Color		alienColor;
	private Color		backgroundColor;
	private int			pH;
	private int			pW;
	private int			anzX;
	private int			anzY;
	private int			pixel;
	private Alien[][]	aliens;
	private static Logger logger = Logger.getLogger(SaveSettings.class.getName());

	public SaveSettings(int pH, int pW, int anzX, int anzY, int pixel, Alien[][] aliens, Color alienColor, Color backgroundColor) {
		this.alienColor = alienColor;
		this.backgroundColor = backgroundColor;
		this.pH = pH;
		this.pW = pW;
		this.anzX = anzX;
		this.anzY = anzY;
		this.pixel = pixel;
		this.aliens = aliens;
	}

	public static SaveSettings load(File fileName, JProgressBar sp) {
		sp.setValue(0);
		XStream xStream = new XStream(new DomDriver());
		sp.setValue(50);
		SaveSettings gol = (SaveSettings) xStream.fromXML(fileName);
		sp.setValue(100);
		return gol;
	}

	public static void save(File file, SaveSettings gol, JProgressBar sp) {
		sp.setValue(1);
		XStream xStream = new XStream(new DomDriver());
		sp.setValue(10);
		String xmlString = xStream.toXML(gol);
		sp.setValue(50);
		try {
			Files.write(file.toPath(), xmlString.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
			sp.setValue(100);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "in SaveSettings.save()", e.fillInStackTrace());
			sp.setValue(0);
		}
	}

	public int getPH() {
		return pH;
	}

	public int getPW() {
		return pW;
	}

	public int getAnzX() {
		return anzX;
	}

	public int getAnzY() {
		return anzY;
	}

	public int getPixel() {
		return pixel;
	}

	public Alien[][] getAl() {
		return aliens;
	}
	
	public Color getAlienColor() {
		return alienColor;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}
}
