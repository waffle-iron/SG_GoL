package de.sydsoft.sg_gol.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JProgressBar;
import javax.swing.ProgressMonitorInputStream;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.paint.Color;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import de.sydsoft.sg_gol.entities.Alien;

public class SaveGame{
	private Color		alienColor;
	private Color		backgroundColor;
	private int			pH;
	private int			pW;
	private int			anzX;
	private int			anzY;
	private int			pixel;
	private Alien[][]	aliens;
	private static Logger logger = Logger.getLogger(SaveGame.class.getName());

	public SaveGame(int pH, int pW, int anzX, int anzY, int pixel, Alien[][] aliens, Color alienColor, Color backgroundColor) {
		this.alienColor = alienColor;
		this.backgroundColor = backgroundColor;
		this.pH = pH;
		this.pW = pW;
		this.anzX = anzX;
		this.anzY = anzY;
		this.pixel = pixel;
		this.aliens = aliens;
	}

	public static Task<SaveGame> load(final File filePath) {
		Task<SaveGame> loadTask = new Task<SaveGame>() {
			@Override
			protected SaveGame call() throws Exception {
				updateProgress(1, 3);
				XStream xStream = new XStream(new DomDriver());
				updateProgress(2, 3);
				SaveGame sg = (SaveGame) xStream.fromXML(filePath);
				updateProgress(3, 3);
				return sg;
			}
		};
		return loadTask;
	}

	public static Task<SaveGame> save(final File file, final SaveGame gol) {
		Task<SaveGame> saveTask = new Task<SaveGame>() {
			@Override
			protected SaveGame call() throws Exception {
				updateProgress(1, 4);
				XStream xStream = new XStream(new DomDriver());
				updateProgress(2, 4);
				String xmlString = xStream.toXML(gol);
				updateProgress(3, 4);
				try {
					Files.write(file.toPath(), xmlString.getBytes(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
					updateProgress(4, 4);
				} catch (IOException e) {
					logger.log(Level.SEVERE, "in SaveSettings.save()", e.fillInStackTrace());
					updateProgress(0, 4);
				}
				return gol;
			}
		};
		return saveTask;
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
