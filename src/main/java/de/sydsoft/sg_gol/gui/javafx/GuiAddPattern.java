package de.sydsoft.sg_gol.gui.javafx;

import de.sydsoft.sg_gol.lang.Localizer;
import de.sydsoft.sg_gol.model.Constants;
import de.sydsoft.sg_gol.model.GoLPattern;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.controlsfx.control.ButtonBar;
import org.controlsfx.control.ButtonBar.ButtonType;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class GuiAddPattern {
    private static int scale = 5;
    private static int X = 5, Y = 5;
    private static List<List<Boolean>> pattern;
    private static String name;
    private static ImageView iv;

    public static void repaint() {
        if (iv != null) {
            iv.setImage(getImage());
        }
    }

    private static final Action actionOK = new Action(new Consumer<ActionEvent>() {
        @Override
        public void accept(ActionEvent actionEvent) {
            Dialog d = (Dialog) actionEvent.getSource();
            d.hide();
            GoLPattern.addPattern(name, GuiGameOfLife.getInstance().getPatternBar(), toArray());
        }
    }) {
        {
            ButtonBar.setType(this, ButtonType.OK_DONE);
        }
    };

    private static boolean[][] toArray() {
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
            if (x < X) {
                if (y < Y) {
                    pattern.get(x).set(y, !pattern.get(x).get(y));
                }
            }
            repaint();
        }

        ;
    };

    static {
        resizePattern();
    }

    private static void resizePattern() {
        if (pattern == null) pattern = new ArrayList<>(X);
        for (int x = 0; x < X; x++) {
            if (pattern.size() <= x) pattern.add(new ArrayList<Boolean>());
            int ySize = pattern.get(x).size();
            for (int y = 0; y < Y - ySize; y++) {
                pattern.get(x).add(false);
            }
        }
    }

    public static void setX(int x) {
        GuiAddPattern.X = x;
        resizePattern();
        repaint();
    }

    public static void setY(int y) {
        GuiAddPattern.Y = y;
        resizePattern();
        repaint();
    }

    public static void setScale(int scale) {
        GuiAddPattern.scale = scale;
        repaint();
    }

    private static Image getImage() {
        BufferedImage bimg = new BufferedImage(X * scale, Y * scale, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bimg.createGraphics();
        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                if (pattern.get(x).get(y)) {
                    g2d.setColor(Constants.toSwingColor(Constants.DEFAULTALIENALIVECOLOR));
                } else {
                    g2d.setColor(Constants.toSwingColor(Constants.DEFAULTALIENDEATHCOLOR));
                }
                g2d.fillRect(x * scale, y * scale, scale, scale);
            }
        }
        return SwingFXUtils.toFXImage(bimg, null);
    }

    public static void showDialog(Stage stage) {
        Dialog dlg = new Dialog(stage, "Pattern Dialog");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 10, 0, 10));


        final Slider tfX = new Slider(1, Byte.MAX_VALUE, X);
        tfX.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setX(newValue.intValue());
            }
        });
        Slider tfY = new Slider(1, Byte.MAX_VALUE, Y);
        tfY.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                setY(newValue.intValue());
            }
        });
        Slider tfscale = new Slider(1, 20, scale);
        tfscale.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                scale = newValue.intValue();
            }
        });
        final TextField tfName = new TextField();
        tfName.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                name = newValue;
            }
        });

        grid.add(new Label(Localizer.get("dialog.newX")), 0, 0);
        grid.add(tfX, 1, 0);
        grid.add(new Label(Localizer.get("dialog.newY")), 2, 0);
        grid.add(tfY, 3, 0);
        grid.add(new Label(Localizer.get("Menu.Option.scale")), 4, 0);
        grid.add(tfscale, 5, 0);
        grid.add(new Label("name"), 0, 1);
        grid.add(tfName, 1, 1);
        iv = new ImageView(GuiAddPattern.getImage());
        iv.setOnMouseClicked(eventImageClick);
        ScrollPane sp = new ScrollPane(iv);
        sp.setMinSize(640, 480);
        BorderPane bp = new BorderPane();
        bp.setTop(grid);
        bp.setCenter(sp);

        dlg.setResizable(false);
        dlg.setIconifiable(false);
        dlg.setContent(bp);
        //dlg.getActions().addAll(actionOK, Actions.CANCEL);

        dlg.show();
    }
}
