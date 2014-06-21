package de.sydsoft.sg_gol.gui.swing;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.Callable;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;

import javax.swing.JFrame;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.FlyByCamera;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;
import com.jme3.util.JmeFormatter;

import de.sydsoft.sg_gol.gui.jme3.JME3GameClient;
import de.sydsoft.sg_gol.lang.Localizer;
import de.sydsoft.sg_gol.world.AlienWorld;

public class SwingGameClient {
	private static JmeCanvasContext		context;
	private static Canvas				canvas;
	private static AppSettings			settings;
	private static SimpleApplication	app;
	private static GuiGameOfLife		frame;
	private static Logger				logger			= Logger.getLogger(SwingGameClient.class.getName());
	private static String				uniqueAppName	= "de/sydsoft/sg_gol";
	public static int		anzX			= 18;
	public static int		anzZ			= 18;


	public static SimpleApplication getApplication() {
		return app;
	}

	public static GuiGameOfLife getGuiGameOfLive() {
		return frame;
	}

	public static void main(String[] args) {
		JmeFormatter formatter = new JmeFormatter();

		Handler consoleHandler = new ConsoleHandler();
		consoleHandler.setFormatter(formatter);

		Logger.getLogger("").removeHandler(Logger.getLogger("").getHandlers()[0]);
		Logger.getLogger("").addHandler(consoleHandler);

		createCanvas();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JPopupMenu.setDefaultLightWeightPopupEnabled(false);

				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception e) {
					logger.log(Level.SEVERE, "a Problem with the Look and feel occured", e.getCause());
				}

				createFrame();

				frame.add(canvas, BorderLayout.CENTER);
				frame.pack();
				startApp();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	private static void startApp() {
		app.startCanvas();
		app.enqueue(new Callable<Void>() {
			public Void call() {
				if (app instanceof SimpleApplication) {
					SimpleApplication simpleApp = (SimpleApplication) app;
					FlyByCamera flyCam = simpleApp.getFlyByCamera();
					flyCam.setDragToRotate(true);
					flyCam.setMoveSpeed(0f);
					flyCam.setRotationSpeed(0);
					float pos = (anzX>anzZ)?2.5f*anzX+5:2.5f*anzZ+5;
					simpleApp.getCamera().setLocation(new Vector3f(0,pos,0));

				}
				return null;
			}
		});
	}

	private static void createFrame() {
		frame = new GuiGameOfLife(app);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				app.stop();
			}
		});
		frame.setTitle(settings.getTitle());
		// frame.setExtendedState(appSettings.getExtendedState());
		// Constants.ALIENBORDERACTIVE = appSettings.getAlienBorderActive();
	}

	private static void createCanvas() {
		settings = new AppSettings(true);
		settings.setWidth(640);
		settings.setHeight(480);
		settings.setTitle(Localizer.get("dialog.header") + " " + Localizer.get("version") + " (© Astrid Fiedler, Sythelux Rikd)");

		app = JME3GameClient.getInstance();

//		try {
//			settings.load(uniqueAppName);
//		} catch (BackingStoreException ex) {
//			logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex.fillInStackTrace());
//		}

		app.setPauseOnLostFocus(true);
		app.setSettings(settings);
		app.getStateManager().attach(new AlienWorld(app, anzX, anzZ));
		app.createCanvas();
        app.startCanvas();

		context = (JmeCanvasContext) app.getContext();
		canvas = context.getCanvas();
		canvas.setSize(settings.getWidth(), settings.getHeight());
	}
}
