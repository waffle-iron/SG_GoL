package de.sydsoft.sg_gol.gui.javafx;

import java.awt.Point;
import java.io.File;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Dimension2D;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import org.controlsfx.dialog.Dialogs;

import de.sydsoft.sg_gol.lang.Localizer;
import de.sydsoft.sg_gol.model.Constants;
import de.sydsoft.sg_gol.model.GoLPattern;
import de.sydsoft.sg_gol.system.AppSettings;
import de.sydsoft.sg_gol.system.SaveGame;
import de.sydsoft.sg_gol.world.AlienWorld;

/**
 * 
 * @author syd
 */
public class GuiGameOfLife extends Application {

	private static Stage			stage;
	private static GuiGameOfLife	instance;
	/** Buttons fuer die Toolbar */
	private Button[]				cBarButton		= new Button[4];
	/** MenuItems fuer das DateiMenu */
	private MenuItem[]				oMenuItems;
	/** Fachkonzept */
	private AlienWorld				alienWorld;
	/** ob in Vollbild, */
	// private boolean fullscreen = false;
	/** ob in beiden seiten Maximiert */
	// private boolean maxboth = false;
	/**
	 * Attribut welches sich die vorherige Fenstergroesse speichert (fuer
	 * vollbild)
	 */
//	private Dimension2D				size;
	/**
	 * Attribut welches sich die vorherige Fensterposition speichert (fuer
	 * vollbild)
	 */
//	private Point					lastLoc;
	/** Dialog in dem Die Pixelanzahl pro Alien festgestellt werden kann */
	// private SDialog pixelDialog = new JDialog(new JFrame(),
	// Localizer.get("dialog.pixel"));
	/** TextFeld fuer Pixel-Dialog */
	// private TextField pixelDTextField = new TextField();
	/** bestaetigen Button fuer Pixel-Dialog */
	// private Button pixelDOKButton = new Button("OK");
	/** ueberschriftsLabel fuer Pixel-Dialog */
	// private Label pixelDheadling;
	/** Dialog in dem TimerGeschwindigkeit */
	// private JDialog speedDialog = new JDialog(new JFrame(),
	// Localizer.get("dialog.speed"));
	/** Textfield fuer speed-Dialog */
	// private TextField speedDTextField = new TextField();
	/** Bestaetigen button fuer Speed-Dialog */
	// private Button speedDOKButton = new Button("OK");
	/** ueberschriftslabel fuer Speed-Dialog */
	// private Label speedDheadling;
	/** Dialog fuer den Speicherfortschritt */
	// private SProgressDialog progDia;
	ToolBar							patternBar;
	private File					fileName;
	private static String			uniqueAppName	= "de/sydsoft/sg_gol";
	private static AppSettings		appSettings		= new AppSettings(true);
	private static Logger			logger			= Logger.getLogger(GuiGameOfLife.class.getName());

	public void start() {
		alienWorld.setCurrentPattern(null);
		cBarButton[3].setText(Localizer.get("toolBox.stop"));
		alienWorld.unPause();
		oMenuItems[3].setVisible(true);
	}

	public void pause() {
		cBarButton[3].setText(Localizer.get("toolBox.start"));
		alienWorld.pause();
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
		// alienWorld.repaint();
	}

	/**
	 * Methode zum Vollbild wechsel
	 */
	protected void toggleFullscreen() {
		stage.setFullScreen(!stage.isFullScreen());
		appSettings.setFullscreen(stage.isFullScreen());
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
		return stage.isFullScreen();
	}

	/**
	 * 
	 * @param fullscreen
	 */
	public void setFullscreen(boolean fullscreen) {
		appSettings.setFullscreen(fullscreen);
		stage.setFullScreen(fullscreen);
	}

	/**
	 * Methode fuer den Dialog zum aendern der groesse der Aliens
	 */
	private void showRulesDialog() {
		Optional<String> response = Dialogs.create().owner(stage).title(Localizer.get("Menu.Option.PpA")).masthead(Localizer.get("Menu.Option.PpA") + Localizer.get("dialog.bracket", alienWorld.getAlienSize(), "")).showTextInput(alienWorld.getAlienSize() + "");
		// String retVal = SDialog.showSDialog("",
		// Localizer.get("Menu.Option.PpA") + Localizer.get("dialog.bracket",
		// alienWorld.getAlienSize(), ""));
		if (response.isPresent() && response.get().matches("\\d+")) {
			int alienSize = Integer.parseInt(response.get());
			if (alienSize < 2) {
				Constants.ALIENBORDERACTIVE = false;
				appSettings.setAlienBorderActive(false);
			}
			alienWorld.setAlienSize(alienSize);
			alienWorld.setResized(true);
		}
	}

	/**
	 * Dialog zum aendern der geschwindigkeit de timers
	 */
	private void showSpeedDialog() {
		Optional<String> response = Dialogs.create().owner(stage).title(Localizer.get("Menu.Option.Speed")).masthead(Localizer.get("dialog.bracket", alienWorld.getSleepTime(), "ms")).showTextInput(alienWorld.getSleepTime() + "");
		// String retVal = SDialog.showSDialog("",
		// Localizer.get("Menu.Option.Speed") + Localizer.get("dialog.bracket",
		// alienWorld.getTimer().getSleepTime(), "ms"));
		if (response.isPresent() && response.get().matches("\\d+")) {
			alienWorld.setSleepTime(Integer.parseInt(response.get()));
		}
	}

	/**
	 * Programm startmethode
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// try {
		// UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (Exception e) {
		// logger.log(Level.SEVERE, "a Problem with the Look and feel occured",
		// e.getCause());
		// }

		launch(args);

		// instance = new GuiGameOfLife();
		// instance.setVisible(true);
	}

	@Override
	public void init() throws Exception {
		instance = this;
	}

	private void save() {
		// progDia = new SProgressDialog(null, Localizer.get("fileC.progr"),
		// Localizer.get("fileC.saving"));
		// progDia.setVisible(true);
		// SaveGame gol = new SaveGame(alienWorld.getH(), alienWorld.getW(),
		// alienWorld.getAnzx(), alienWorld.getAnzy(),
		// alienWorld.getAlienSize(), alienWorld.getAl(),
		// Constants.ALIENALIVECOLOR, Constants.ALIENDEATHCOLOR);
		// SaveGame.save(fileName, gol, progDia.getProgressBar());

		Service<SaveGame> service = new Service<SaveGame>() {
			protected Task<SaveGame> createTask() {
				return SaveGame.save(fileName, new SaveGame(alienWorld.getH(), alienWorld.getW(), alienWorld.getAnzx(), alienWorld.getAnzy(), alienWorld.getAlienSize(), alienWorld.getAl(), Constants.ALIENALIVECOLOR, Constants.ALIENDEATHCOLOR));
			}
		};

		Dialogs.create().owner(stage).title(Localizer.get("fileC.progr")).masthead(Localizer.get("fileC.saving")).showWorkerProgress(service);

		service.start();
	}

	private void load() {
		Service<SaveGame> service = new Service<SaveGame>() {
			protected Task<SaveGame> createTask() {
				return SaveGame.load(fileName);
			}
		};

		// progDia = new SProgressDialog(null, Localizer.get("fileC.progr"),
		// Localizer.get("fileC.opening"));
		// progDia.setVisible(true);
		// Timer loadT = new Timer(101);
		// loadT.start();
		Dialogs.create().owner(stage).title(Localizer.get("fileC.progr")).masthead(Localizer.get("fileC.opening")).showWorkerProgress(service);

		service.start();

		SaveGame gol = service.getValue();
		alienWorld.setAlienColor(gol.getAlienColor());
		alienWorld.setBackgroundColor(gol.getBackgroundColor());
		alienWorld.setH(gol.getPH());
		alienWorld.setW(gol.getPW());
		alienWorld.setAnzx(gol.getAnzX());
		alienWorld.setAnzy(gol.getAnzY());
		alienWorld.setAlienSize(gol.getPixel());
		alienWorld.createAlien(false);
		alienWorld.setAl(gol.getAl());
		// alienWorld.repaint();
		// progDia.setVisible(false);

	}

	// private class Timer extends Thread {
	//
	// private long sleeptime;
	// private boolean running = true;
	//
	// public Timer(int speed) {
	// sleeptime = speed;
	// }
	//
	// @Override
	// public void run() {
	// while (running) {
	// try {
	// sleep(sleeptime);
	// alienWorld.setVisible(false);
	// switch ((int) sleeptime) {
	// case 100:
	// writeData();
	// running = false;
	// sleep(1000);
	// progDia.setVisible(false);
	// break;
	// case 101:
	// readData();
	// running = false;
	// sleep(1000);
	// progDia.setVisible(false);
	// break;
	// default:
	// break;
	// }
	// alienWorld.setVisible(true);
	// } catch (InterruptedException e) {
	// logger.log(Level.SEVERE, "in Timer.run():", e.fillInStackTrace());
	// }
	// }
	// }
	// }

	public ToolBar getPatternBar() {
		return patternBar;
	}

	public static GuiGameOfLife getInstance() {
		return instance;
	}

	public void setLocation(int x, int y) {
		appSettings.setLocation(x, y);
		stage.setX(x);
		stage.setY(y);
	}

	public void setLocation(Point p) {
		setLocation(p.x, p.y);
	}

	public void setSize(Dimension2D d) {
		setSize(d.getWidth(), d.getHeight());
	}

	public void setSize(double width, double height) {
		appSettings.setResolution(width, height);
		stage.setWidth(width);
		stage.setHeight(height);
	}

	public void setTitle(String title) {
		appSettings.setTitle(title);
		stage.setTitle(title);
	}

	@Override
	public void stop() throws Exception {
		alienWorld.stop();
	}

	public void start(Stage stage) throws Exception {
		GuiGameOfLife.stage = stage;
		try {
			appSettings.load(uniqueAppName);
		} catch (BackingStoreException e) {
			logger.severe(e.getLocalizedMessage());
		}
		appSettings.setTitle(Localizer.get("dialog.header") + " " + Localizer.get("version"));
		appSettings.turnOnAutoSave(uniqueAppName);
		stage.getIcons().add(new Image(GuiGameOfLife.class.getResourceAsStream("/de/sydsoft/sg_gol/gui/Logo.png")));

		setLocation(appSettings.getLocation());
		setSize(appSettings.getSize());
		setTitle(appSettings.getTitle());
		setFullscreen(appSettings.isFullscreen());
		Constants.ALIENBORDERACTIVE = appSettings.getAlienBorderActive();

		VBox root = new VBox();
		root.setAlignment(Pos.BASELINE_CENTER);
		Scene scene = new Scene(root);
		stage.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
				appSettings.setWidth(newSceneWidth);
			}
		});
		stage.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
				appSettings.setHeight(newSceneHeight);
			}
		});
		stage.xProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneX, Number newSceneX) {
				appSettings.setXPosition(newSceneX);
			}
		});
		stage.yProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneY, Number newSceneY) {
				appSettings.setYPosition(newSceneY);
			}
		});

		MenuBar mainMBar = new MenuBar();

		Menu fileMenu = new Menu(Localizer.get("Menu.File"));
		fileMenu.setOnShowing(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				alienWorld.stopRender();
			}});
		fileMenu.setOnHidden(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				alienWorld.startRender();				
			}});
		MenuItem[] fMenuItems = new MenuItem[3];
		//
		// // oeffnen MenuItem
		fMenuItems[0] = new Menu(Localizer.get("Menu.File.open"));
		// // fMenuItems[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
		// InputEvent.CTRL_MASK));
		fMenuItems[0].setOnAction(new EventHandler<ActionEvent>() {

			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setSelectedExtensionFilter(new ExtensionFilter("GoL", "*.gol"));
				fileName = fileChooser.showOpenDialog(null);
				if (fileName != null) {
					load();
					// alienWorld.repaint();
				}
			}
		});
		//
		// // speichern MenuItem
		fMenuItems[1] = new Menu(Localizer.get("Menu.File.save"));
		// // fMenuItems[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
		// InputEvent.CTRL_MASK));
		fMenuItems[1].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setSelectedExtensionFilter(new ExtensionFilter("GoL", "*.gol"));
				fileName = fileChooser.showSaveDialog(null);
				if (fileName != null) {
					save();
				}
			}
		});
		//
		// // beenden MenuItem
		fMenuItems[2] = new Menu(Localizer.get("Menu.File.close"));
		// fMenuItems[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
		// InputEvent.CTRL_MASK));
		fMenuItems[2].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Platform.exit();
			}
		});
		//
		fileMenu.getItems().addAll(fMenuItems);
		mainMBar.getMenus().add(fileMenu);

		//
		// // Options Menu
		Menu optionMenu = new Menu(Localizer.get("Menu.Option"));
		optionMenu.setOnShowing(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				alienWorld.stopRender();
			}});
		optionMenu.setOnHidden(new EventHandler<Event>() {
			@Override
			public void handle(Event event) {
				alienWorld.startRender();				
			}});
		oMenuItems = new MenuItem[6];
		//
		// // Pixel pro Alien aendern Menuitem
		oMenuItems[0] = new MenuItem(Localizer.get("Menu.Option.PpA"));
		oMenuItems[0].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				showRulesDialog();
				alienWorld.startRender();				
			}
		});
		//
		// // Farbe der lebenden Aliens Menuitem
		oMenuItems[1] = new MenuItem(Localizer.get("Menu.Option.ColA"));
		oMenuItems[1].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				alienWorld.stopRender();
				ColorPicker colorPicker = new ColorPicker(Constants.DEFAULTALIENALIVECOLOR);
				colorPicker.show();
				Color c = colorPicker.getValue();
				// Color c = ColorPicker.showDialog(null,
				// Localizer.get("Menu.Option.ColA"),
				// Constants.DEFAULTALIENALIVECOLOR);
				if (c == null) c = Constants.ALIENALIVECOLOR;
				alienWorld.setAlienColor(c);
				// alienWorld.repaint();
				alienWorld.startRender();				
			}
		});
		//
		// // Hintergrundfarbe des Feldes MenuItem
		oMenuItems[2] = new MenuItem(Localizer.get("Menu.Option.BgC"));
		oMenuItems[2].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				alienWorld.stopRender();
				ColorPicker colorPicker = new ColorPicker(Constants.DEFAULTALIENDEATHCOLOR);
				colorPicker.show();
				Color c = colorPicker.getValue();
				// Color c = JColorChooser.showDialog(null,
				// Localizer.get("Menu.Option.BgC"),
				// Constants.DEFAULTALIENDEATHCOLOR);
				alienWorld.setBackgroundColor(c);
				// alienWorld.repaint();
				alienWorld.startRender();				
			}
		});
		//
		// // Geschwindigkeit aendern MenuItem
		oMenuItems[3] = new MenuItem(Localizer.get("Menu.Option.Speed"));
		oMenuItems[3].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				alienWorld.stopRender();
				showSpeedDialog();
				alienWorld.startRender();				
			}
		});
		//
		// // Alienrahmen MenuItem
		oMenuItems[4] = new MenuItem(Localizer.get("Menu.Option.hideAb"));
		oMenuItems[4].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				changebordact();
			}
		});
		//
		// // Vollbild Menuitem
		oMenuItems[5] = new MenuItem(Localizer.get("Menu.Option.Fullscreen"));
		// //
		// oMenuItems[5].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11,s
		// Event.CTRL_MASK, true));
		oMenuItems[5].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				alienWorld.stopRender();
				toggleFullscreen();
				alienWorld.startRender();				
			}
		});
		//
		// // optionsMenuitems auf Optionsmenu schreiben
		optionMenu.getItems().addAll(oMenuItems);
		mainMBar.getMenus().add(optionMenu);
		root.getChildren().add(mainMBar);
		//
		// oMenuItems[3].setVisible(false);
		//
		// // Datei und optionsmenu auf Menubar adden
		//
		// // initialisierung der Alienworld
		ImageView iv = new ImageView();
		alienWorld = new AlienWorld(15, iv);
		iv.setImage(alienWorld.toJavaFXImage());
		//
		// // initialisierung der Toolbox
		ToolBar controlBar = new ToolBar();
		// //
		// controlBar.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		// controlBar.setOrientation(Orientation.HORIZONTAL);
		//
		//
		// // naechster schritt Button
		cBarButton[0] = new Button(Localizer.get("toolBox.next"));
		cBarButton[0].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				alienWorld.update();
			}
		});

		// leeren Button
		cBarButton[1] = new Button(Localizer.get("toolBox.clear"));
		cBarButton[1].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (alienWorld.isPatternInUse()) alienWorld.resetPatternInUse();
				alienWorld.clearAll();
			}
		});

		// zufaellig button
		cBarButton[2] = new Button(Localizer.get("toolBox.random"));
		cBarButton[2].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (alienWorld.isPatternInUse()) alienWorld.resetPatternInUse();
				alienWorld.random();
			}
		});

		// starten/stoppen umschalten Button
		cBarButton[3] = new Button(Localizer.get("toolBox.start"));
		cBarButton[3].setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (cBarButton[3].getText().equals(Localizer.get("toolBox.start"))) {
					start();
				} else {
					pause();
				}
			}
		});

		// Buttons auf die Toolbox einlesen
		controlBar.getItems().addAll(cBarButton);

		root.getChildren().add(controlBar);

		patternBar = new ToolBar();
		patternBar.setOrientation(Orientation.HORIZONTAL);
		HBox vbButtons = new HBox();
		vbButtons.setSpacing(5);
		// vbButtons.setPadding(new Insets(0, 20, 10, 20));
		patternBar.getItems().add(vbButtons);
		GoLPattern.fill(vbButtons, stage);
		// // Toolbox in Fenster einhaengen
		root.getChildren().add(patternBar);

		root.getChildren().add(iv);
		// // hintergrund setzen
		// // stage.setBackground(Constants.ALIENDEATHCOLOR);
		// // alienfeld einbinden

		stage.setScene(scene);
//		scene.getStylesheets().add(GuiGameOfLife.class.getResource("/de/sydsoft/sg_gol/gui/style.css").toExternalForm());
		stage.show();
		alienWorld.start();

		// fullscreen = !appSettings.isFullscreen();
		// changeFullscreen();
	}
}
