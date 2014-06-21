package de.sydsoft.sg_gol.gui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import de.sydsoft.libst.gui.SProgressDialog;
import de.sydsoft.libst.sfl.SFileChooser;
import de.sydsoft.libst.sfl.StdFileNExtFilter;
import de.sydsoft.libst.util.Console;
import de.sydsoft.sg_gol.lang.Localizer;
import de.sydsoft.sg_gol.model.Constants;
import de.sydsoft.sg_gol.model.GoLPattern;
import de.sydsoft.sg_gol.system.AppSettings;
import de.sydsoft.sg_gol.system.SaveSettings;
import de.sydsoft.sg_gol.world.AlienWorld;

/**
 * 
 * @author syd
 */
@SuppressWarnings("serial")
public class GuiGameOfLife extends JFrame {

	private static GuiGameOfLife	instance;
	/** Buttons fuer die Toolbar */
	private JButton[]				cBarButton		= new JButton[4];
	/** MenuItems fuer das DateiMenu */
	private JMenuItem[]				oMenuItems;
	/** Fachkonzept */
	private AlienWorld				alienWorld;
	/** hoehe,breite */
	private int						h				= 480, w = 640;
	/** ob in Vollbild, */
	private boolean					fullscreen		= false;
	/** ob in beiden seiten Maximiert */
	private boolean					maxboth			= false;
	/**
	 * Attribut welches sich die vorherige Fenstergroesse speichert (fuer
	 * vollbild)
	 */
	private Dimension				size;
	/**
	 * Attribut welches sich die vorherige Fensterposition speichert (fuer
	 * vollbild)
	 */
	private Point					lastLoc;
	/** Dialog in dem Die Pixelanzahl pro Alien festgestellt werden kann */
	private JDialog					pixelDialog		= new JDialog(this, Localizer.get("dialog.pixel"));
	/** TextFeld fuer Pixel-Dialog */
	private JTextField				pixelDTextField	= new JTextField();
	/** bestaetigen Button fuer Pixel-Dialog */
	private JButton					pixelDOKButton	= new JButton("OK");
	/** ueberschriftsLabel fuer Pixel-Dialog */
	private Label					pixelDheadling;
	/** Dialog in dem TimerGeschwindigkeit */
	private JDialog					speedDialog		= new JDialog(this, Localizer.get("dialog.speed"));
	/** Textfield fuer speed-Dialog */
	private JTextField				speedDTextField	= new JTextField();
	/** Bestaetigen button fuer Speed-Dialog */
	private JButton					speedDOKButton	= new JButton("OK");
	/** ueberschriftslabel fuer Speed-Dialog */
	private Label					speedDheadling;
	/** Dialog fuer den Speicherfortschritt */
	private SProgressDialog			progDia;
	JToolBar patternBar;
	private File					fileName;
	private static String			uniqueAppName	= "de/sydsoft/sg_gol";
	private static AppSettings		appSettings = new AppSettings(true);
	private static Logger logger = Logger.getLogger(GuiGameOfLife.class.getName());

	/**
	 * Konstruktor fuer dieses Fenster
	 * 
	 * @param appSettings
	 *            null, wenn standard Settings genommen werden sollen.
	 * */
	private GuiGameOfLife() {
		try {
			appSettings.load(uniqueAppName);
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
		appSettings.setTitle(Localizer.get("dialog.header") + " " + Localizer.get("version") + " (© Astrid Fiedler, Sythelux Rikd)");
		appSettings.turnOnAutoSave(uniqueAppName);
		try {
			super.setIconImage(ImageIO.read(GuiGameOfLife.class.getResource("/gui/Logo.png")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		super.setLocation(lastLoc = appSettings.getLocation());
		super.setSize(size = appSettings.getSize());
		super.setTitle(appSettings.getTitle());
		super.setExtendedState(appSettings.getExtendedState());
		Constants.ALIENBORDERACTIVE = appSettings.getAlienBorderActive();
		
		JMenuBar mainMBar = new JMenuBar();
		setJMenuBar(mainMBar);
		setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JMenu fileMenu = new JMenu(Localizer.get("Menu.File"));
		JMenuItem[] fMenuItems = new JMenuItem[3];

		// oeffnen MenuItem
		fMenuItems[0] = new JMenuItem(Localizer.get("Menu.File.open"));
		fMenuItems[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		fMenuItems[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileName = new SFileChooser().showSimpleFileDialog(FileDialog.LOAD, StdFileNExtFilter.GOL);
				if (fileName != null) {
					load();
					alienWorld.repaint();
				}
			}
		});

		// speichern MenuItem
		fMenuItems[1] = new JMenuItem(Localizer.get("Menu.File.save"));
		fMenuItems[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
		fMenuItems[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				fileName = new SFileChooser().showSimpleFileDialog(FileDialog.SAVE, StdFileNExtFilter.GOL);
				if (fileName != null) {
					save();
				}
			}
		});

		// beenden MenuItem
		fMenuItems[2] = new JMenuItem(Localizer.get("Menu.File.close"));
		fMenuItems[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		fMenuItems[2].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		// einlesen der menuitems ins DateiMenu
		for (int i = 0; i < fMenuItems.length; i++) {
			fileMenu.add(fMenuItems[i]);
		}

		// Options Menu
		JMenu optionMenu = new JMenu(Localizer.get("Menu.Option"));
		oMenuItems = new JMenuItem[6];

		// Pixel pro Alien aendern Menuitem
		oMenuItems[0] = new JMenuItem(Localizer.get("Menu.Option.PpA"));
		oMenuItems[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				rulesJDialog();
				pixelDialog.setVisible(true);
			}
		});

		// Farbe der lebenden Aliens Menuitem
		oMenuItems[1] = new JMenuItem(Localizer.get("Menu.Option.ColA"));
		oMenuItems[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(null, Localizer.get("Menu.Option.ColA"), Constants.DEFAULTALIENALIVECOLOR);
				if (c == null) c = Constants.ALIENALIVECOLOR;
				alienWorld.setAlienColor(c);
				alienWorld.repaint();
			}
		});

		// Hintergrundfarbe des Feldes MenuItem
		oMenuItems[2] = new JMenuItem(Localizer.get("Menu.Option.BgC"));
		oMenuItems[2].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Color c = JColorChooser.showDialog(null, Localizer.get("Menu.Option.BgC"), Constants.DEFAULTALIENDEATHCOLOR);
				alienWorld.setBackgroundColor(c);
				repaint();
			}
		});

		// Geschwindigkeit ï¿½ndern MenuItem
		oMenuItems[3] = new JMenuItem(Localizer.get("Menu.Option.Speed"));
		oMenuItems[3].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				getSpeedDialog();
				speedDialog.setVisible(true);
			}
		});

		// Alienrahmen MenuItem
		oMenuItems[4] = new JMenuItem(Localizer.get("Menu.Option.hideAb"));
		oMenuItems[4].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				changebordact();
			}
		});

		// Vollbild Menuitem
		oMenuItems[5] = new JMenuItem(Localizer.get("Menu.Option.Fullscreen"));
		oMenuItems[5].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, Event.CTRL_MASK, true));
		oMenuItems[5].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				changeFullscreen();
			}
		});

		// optionsMenuitems auf Optionsmenu schreiben
		for (int i = 0; i < oMenuItems.length; i++) {
			optionMenu.add(oMenuItems[i]);
		}
		oMenuItems[3].setVisible(false);

		// Datei und optionsmenu auf Menubar adden
		mainMBar.add(fileMenu);
		mainMBar.add(optionMenu);

		// initialisierung der Alienworld
		alienWorld = new AlienWorld(15);

		// initialisierung der Toolbox
		JToolBar controlBar = new JToolBar();
		controlBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		controlBar.setOrientation(JToolBar.HORIZONTAL);

		patternBar = new JToolBar();
		patternBar.setOrientation(JToolBar.VERTICAL);
		GoLPattern.fill(patternBar);

		// naechster schritt Button
		cBarButton[0] = new JButton(Localizer.get("toolBox.next"));
		cBarButton[0].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				alienWorld.callNextOrbicularStep();
			}
		});

		// leeren Button
		cBarButton[1] = new JButton(Localizer.get("toolBox.clear"));
		cBarButton[1].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(alienWorld.isPatternInUse()) alienWorld.resetPatternInUse();
				alienWorld.clearAll();
			}
		});

		// zufï¿½lig button
		cBarButton[2] = new JButton(Localizer.get("toolBox.random"));
		cBarButton[2].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(alienWorld.isPatternInUse()) alienWorld.resetPatternInUse();
				alienWorld.random();
			}
		});

		// starten/stoppen umschalten Button
		cBarButton[3] = new JButton(Localizer.get("toolBox.start"));
		cBarButton[3].addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cBarButton[3].getText().equals(Localizer.get("toolBox.start"))) {
					start();
				} else {
					stop();
				}
			}
		});

		// Buttons auf die Toolbox einlesen
		for (int i = 0; i < cBarButton.length; i++) {
			controlBar.add(cBarButton[i]);
		}

		addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				appSettings.setResolution(getWidth(), getHeight());
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				appSettings.setLocation(getX(), getY());
			}
		});
		
		add(patternBar, "East");
		// Toolbox in Fenster einhaengen
		add(controlBar, "North");
		// hintergrund setzen
		this.setBackground(Constants.ALIENDEATHCOLOR);
		// alieneld einbinden
		this.add(alienWorld, "Center");
		
		fullscreen = !appSettings.isFullscreen();
		changeFullscreen();
	}

	public void start() {
		alienWorld.setCurrentPattern(null);
		cBarButton[3].setText(Localizer.get("toolBox.stop"));
		if (alienWorld.getTimer() == null) {
			alienWorld.setTimer(100);
		}
		alienWorld.getTimer().startThread();
		oMenuItems[3].setVisible(true);
	}

	public void stop() {
		cBarButton[3].setText(Localizer.get("toolBox.start"));
		if (alienWorld.getTimer() != null) {
			alienWorld.getTimer().stopThread();
		}
	}

	// Konstruktor ende
	/**
	 * Methode zum aendern ob die Rahmen um die aliens sind
	 */
	private void changebordact() {
		if (Constants.ALIENBORDERACTIVE) {
			oMenuItems[4].setText(Localizer.get("Menu.Option.showAb"));
		} else {
			oMenuItems[4].setText(Localizer.get("Menu.Option.hideAb"));
		}
		Constants.ALIENBORDERACTIVE = !Constants.ALIENBORDERACTIVE;
		appSettings.setAlienBorderActive(Constants.ALIENBORDERACTIVE);
		alienWorld.repaint();
	}

	/**
	 * Methode zum Vollbild wechsel
	 */
	protected void changeFullscreen() {
		dispose();
		if (fullscreen) {
			setUndecorated(false);
			if (maxboth) {
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				maxboth = false;
			} else {
				setExtendedState(JFrame.NORMAL);
			}
			setLocation(lastLoc);
			setSize(size);
		} else {
			if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
				maxboth = true;
				size = new Dimension(320, 240);
				lastLoc = new Point(10, 10);
			}
			size = getSize();
			lastLoc = getLocation();
			dispose();
			setUndecorated(true);
			setExtendedState(JFrame.MAXIMIZED_BOTH);
		}
		fullscreen = !fullscreen;
		appSettings.setFullscreen(fullscreen);
		setVisible(true);
	}

	/**
	 * 
	 * @param fileName
	 * @param sp
	 */
	public void initSave(File fileName, JProgressBar sp) {
		SaveSettings gol = new SaveSettings(alienWorld.getH(), alienWorld.getW(), alienWorld.getAnzx(), alienWorld.getAnzy(), alienWorld.getAlienSize(), alienWorld.getAl(), Constants.ALIENALIVECOLOR, Constants.ALIENDEATHCOLOR);
		SaveSettings.save(fileName, gol, sp);
	}

	/**
	 * Methode zum Laden der Daten im GameOfLife
	 * 
	 * @param fileName
	 * @param sp
	 */
	public void initLoad(File fileName, JProgressBar sp) {
		SaveSettings gol = SaveSettings.load(fileName, sp);
		if (fullscreen) {
			fullscreen = !fullscreen;
			changeFullscreen();
		}
		alienWorld.setAlienColor(gol.getAlienColor());
		alienWorld.setBackgroundColor(gol.getBackgroundColor());
		alienWorld.setH(gol.getPH());
		alienWorld.setW(gol.getPW());
		alienWorld.setAnzx(gol.getAnzX());
		alienWorld.setAnzy(gol.getAnzY());
		alienWorld.setAlienSize(gol.getPixel());
		alienWorld.createAlien(false);
		alienWorld.setAl(gol.getAl());
		alienWorld.repaint();
	}

	/**
	 * Methode fï¿½r den Farbauswaehler
	 * 
	 * @return
	 */

	public SProgressDialog getSaveProg() {
		return progDia;
	}

	/**
	 * 
	 * @return
	 */
	public AlienWorld getAlienWorld() {
		return alienWorld;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isFullscreen() {
		return fullscreen;
	}

	/**
	 * 
	 * @param fullscreen
	 */
	public void setFullscreen(boolean fullscreen) {
		appSettings.setFullscreen(fullscreen);
		this.fullscreen = fullscreen;
	}

	/**
	 * Methode fuer den Dialog zum aendern der groesse der Aliens
	 */
	private void rulesJDialog() {
		pixelDialog.setLocationRelativeTo(null);
		pixelDialog.setLayout(new BorderLayout());
		pixelDialog.setSize(300, 115);
		pixelDheadling = new Label(Localizer.get("Menu.Option.PpA") + Localizer.get("dialog.bracket", alienWorld.getAlienSize(), ""));
		pixelDialog.add(pixelDheadling, "North");
		pixelDTextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!((Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))) {
					e.consume();
				}
			}
		});
		pixelDialog.add(pixelDTextField, "Center");
		pixelDOKButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int alienSize = Integer.parseInt(pixelDTextField.getText());
				if (alienSize < 2) {
					Constants.ALIENBORDERACTIVE = false;
					appSettings.setAlienBorderActive(false);
				}
				alienWorld.setAlienSize(alienSize);
				alienWorld.setResized(true);
				alienWorld.repaint();
				pixelDheadling.setText(Localizer.get("Menu.File.open") + Localizer.get("dialog.bracket", alienWorld.getAlienSize(), ""));
				pixelDialog.setVisible(false);
			}
		});
		pixelDialog.add(pixelDOKButton, "South");
	}

	/**
	 * Dialog zum aendern der geschwindigkeit de timers
	 */
	private void getSpeedDialog() {
		speedDialog.setLocation(100, 100);
		speedDialog.setLayout(new BorderLayout());
		speedDialog.setSize(300, 115);
		speedDheadling = new Label(Localizer.get("Menu.Option.Speed") + Localizer.get("dialog.bracket", alienWorld.getTimer().getSleepTime(), "ms"));
		speedDialog.add(speedDheadling, "North");
		speedDTextField.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!((Character.isDigit(c) || (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE)))) {
					e.consume();
				}
			}
		});
		speedDialog.add(speedDTextField, "Center");
		speedDOKButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				alienWorld.getTimer().setSleepTime(Integer.parseInt(speedDTextField.getText()));
				alienWorld.repaint();
				speedDheadling.setText(Localizer.get("Menu.Option.Speed") + Localizer.get("dialog.bracket", alienWorld.getTimer().getSleepTime(), "ms"));
				speedDialog.setVisible(false);
			}
		});
		speedDialog.add(speedDOKButton, "South");

	}

	/**
	 * Programm startmethode
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			logger.log(Level.SEVERE, "a Problem with the Look and feel occured", e.getCause());
		}

		instance = new GuiGameOfLife();
		instance.setVisible(true);
	}

	/**
	 * Methode zum speichern der Daten
	 * 
	 */
	public void writeData() {
		initSave(fileName, progDia.getSaveProgress());
	}

	private void save() {
		progDia = new SProgressDialog(this, Localizer.get("fileC.progr"), Localizer.get("fileC.opening"));
		progDia.setVisible(true);
		Timer saveT = new Timer(100);
		saveT.start();
		logger.info("saving initialized.");
	}

	/**
	 * Methode zum Laden der Daten
	 * 
	 */
	public void readData() {
		initLoad(fileName, progDia.getSaveProgress());
	}

	private void load() {
		progDia = new SProgressDialog(this, Localizer.get("fileC.progr"), Localizer.get("fileC.saving"));
		progDia.setVisible(true);
		Timer loadT = new Timer(101);
		loadT.start();
	}

	private class Timer extends Thread {

		private long	sleeptime;
		private boolean	running	= true;

		public Timer(int speed) {
			sleeptime = speed;
		}

		@Override
		public void run() {
			while (running) {
				try {
					sleep(sleeptime);
					alienWorld.setVisible(false);
					switch ((int) sleeptime) {
						case 100:
							writeData();
							running = false;
							sleep(1000);
							progDia.setVisible(false);
							break;
						case 101:
							readData();
							running = false;
							sleep(1000);
							progDia.setVisible(false);
							break;
						default:
							break;
					}
					alienWorld.setVisible(true);
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "in Timer.run():", e.fillInStackTrace());
				}
			}
		}
	}

	public JToolBar getPatternBar() {
		return patternBar;
	}
	
	public static GuiGameOfLife getInstance() {
		return instance;
	}

	@Override
	public void setLocation(int x, int y) {
		appSettings.setLocation(x, y);
		super.setLocation(x, y);
	}

	@Override
	public void setLocation(Point p) {
		appSettings.setLocation(p.x, p.y);
		super.setLocation(p);
	}

	@Override
	public void setSize(Dimension d) {
		appSettings.setResolution(d.width, d.height);
		super.setSize(d);
	}

	public void setSize(int width, int height) {
		appSettings.setResolution(width, height);
		super.setSize(width, height);
	}

	@Override
	public void setTitle(String title) {
		appSettings.setTitle(title);
		super.setTitle(title);
	}
	
	@Override
	public void setExtendedState(int state) {
		appSettings.setExtendedState(state);
		super.setExtendedState(state);
	}

}
