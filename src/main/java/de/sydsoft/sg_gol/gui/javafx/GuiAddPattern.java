package de.sydsoft.sg_gol.gui.javafx;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.AbstractAction;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;

import de.sydsoft.sg_gol.lang.Localizer;
import de.sydsoft.sg_gol.model.Constants;
import de.sydsoft.sg_gol.model.GoLPattern;

public class GuiAddPattern {
	private static int							scale				= 5;
	private static int							X = 5, Y=5;
	private static List<List<Boolean>>					pattern;
	private static String name;
	private static ImageView iv;
	
	public static void repaint() {
		if (iv!=null) {
			iv.setImage(getImage());
		}
	}
	
	private static final Action actionOK = new AbstractAction("OK") {
		 {  
	         ButtonBar.setType(this, ButtonType.OK_DONE); 
	     }
		@Override
		public void handle(ActionEvent ae) {
	        Dialog d = (Dialog) ae.getSource();
	        d.hide();
//	        GoLPattern.addPattern(name, GuiGameOfLife.getInstance().getPatternBar(), toArray());TODO: pane statt Toolbar
		}
	 };
	 
	 private static boolean[][] toArray(){
		 boolean[][] retArray = new boolean[X][Y];
		 for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				retArray[x][y] = pattern.get(x).get(y);
			}
		}
		return retArray;
	 }
	 
	 private static final EventHandler<MouseEvent> eventImageClick = new EventHandler<MouseEvent>() {
	    	public void handle(MouseEvent event) {
				int x = (int) (event.getX() / scale);
				int y = (int) (event.getY() / scale);
				if (x<X) {
					if (y<Y) {
						pattern.get(x).set(y, !pattern.get(x).get(y));
					}
				}
				repaint();
	    	};
	    };
	
	static {
		Collections.fill(pattern, new ArrayList<Boolean>(Y));
		for (int x = 0; x < X; x++) {
			Collections.fill(pattern.get(x), false);
		}
	}
	
	public static void setX(int x) {
		GuiAddPattern.X = x;
		repaint();
	}
	
	public static void setY(int y) {
		GuiAddPattern.X = y;
		repaint();
	}
	
	public static void setScale(int scale) {
		GuiAddPattern.scale = scale;
		repaint();
	}
	
	private static Image getImage() {
		BufferedImage bimg = new BufferedImage(X*scale, X*scale, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bimg.createGraphics();
		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				if (pattern.get(x).get(y)) {
					g2d.setColor(Constants.toSwingColor(Constants.DEFAULTALIENALIVECOLOR));
				}else {
					g2d.setColor(Constants.toSwingColor(Constants.DEFAULTALIENDEATHCOLOR));
				}
				g2d.fillRect(x*scale, y*scale, scale, scale);
			}
		}
		return SwingFXUtils.toFXImage(bimg, null);
	}
	
	public static void showDialog(Stage stage) {
		Dialog dlg = new Dialog(stage, "Login Dialog");

	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(10);
	    grid.setPadding(new Insets(0, 10, 0, 10));

	    TextField tfX = new TextField();
	    tfX.setOnInputMethodTextChanged(new EventHandler<InputMethodEvent>() {
			@Override
			public void handle(InputMethodEvent event) {
				String xVal = event.getCommitted();
				if (xVal.matches("\\d+")) {
					setX(Integer.parseInt(xVal));					
				}
			}
	    });
	    TextField tfY = new TextField();
	    tfY.setOnInputMethodTextChanged(new EventHandler<InputMethodEvent>() {
			@Override
			public void handle(InputMethodEvent event) {
				String yVal = event.getCommitted();
				if (yVal.matches("\\d+")) {
					setY(Integer.parseInt(yVal));					
				}
			}
	    });
	    TextField tfscale = new TextField();
	    tfscale.setOnInputMethodTextChanged(new EventHandler<InputMethodEvent>() {
			@Override
			public void handle(InputMethodEvent event) {
				String scaleVal = event.getCommitted();
				if (scaleVal.matches("\\d+")) {
					setScale(Integer.parseInt(scaleVal));					
				}
			}
	    });
	    final TextField tfName = new TextField();
	    tfName.setOnInputMethodTextChanged(new EventHandler<InputMethodEvent>() {
			@Override
			public void handle(InputMethodEvent event) {
				name = event.getCommitted();
			}
	    });
		final GuiAddPattern addPat = new GuiAddPattern();

	    grid.add(new Label(Localizer.get("dialog.newX")), 0, 0);
	    grid.add(tfX, 1, 0);
	    grid.add(new Label(Localizer.get("dialog.newY")), 2, 0);
	    grid.add(tfY, 3, 0);
	    grid.add(new Label(Localizer.get("Menu.Option.scale")), 4, 0);
	    grid.add(tfscale, 5, 0);
	    grid.add(new Label("name"), 0, 1);
	    grid.add(tfName, 1, 1);
	    ImageView iv = new ImageView(addPat.getImage());
	    iv.setOnMouseClicked(eventImageClick);
	    grid.add(iv, 0, 2);
	}
}
