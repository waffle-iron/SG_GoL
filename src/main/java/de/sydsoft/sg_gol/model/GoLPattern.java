package de.sydsoft.sg_gol.model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import de.sydsoft.sg_gol.gui.javafx.GuiAddPattern;
import de.sydsoft.sg_gol.gui.javafx.GuiGameOfLife;
import de.sydsoft.sg_gol.lang.Localizer;
import de.sydsoft.sg_gol.world.AlienWorld;

public class GoLPattern {
	private static GoLPatternList	PatternList	= new GoLPatternList();
	private boolean[][]				oldArray;
	private boolean[][]				pattern;
	private String					name;

	public GoLPattern(String name, String[] pattern) {
		this.name = name;
		this.oldArray = this.pattern = parsePattern(pattern);
	}

	public GoLPattern(String name, boolean[][] pattern) {
		this.name = name;
		this.oldArray = this.pattern = pattern;
	}

	private Button createButton() {
		Button b = new Button(getName(),new ImageView(getIcon()));
		b.setContentDisplay(ContentDisplay.TOP);
		b.setMaxHeight(Double.MAX_VALUE);
		b.setAlignment(Pos.BOTTOM_CENTER);
//		b.setVerticalTextPosition(JButton.TOP);
//		b.setHorizontalTextPosition(JButton.CENTER);
//		b.addActionListener(this);
		b.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (event.getSource() instanceof Button) {
					Button eventButton = (Button) event.getSource();
					GoLPattern goLPB = PatternList.get(eventButton.getText());
					System.out.println(eventButton.getText()+" selected.");
					if (goLPB != null) {
						GuiGameOfLife.getInstance().pause();
						AlienWorld al = GuiGameOfLife.getInstance().getAlienWorld();
						if (al.isPatternInUse()) {
							al.resetPatternInUse();
						}
						al.setCurrentPattern(goLPB);
					}
				}
			}
		});
		return b;
	}

	public static boolean[][] parsePattern(String[] pattern) {
		boolean[][] nArray = new boolean[pattern.length][width(pattern)];
		for (int i = 0; i < nArray.length; i++) {
			for (int j = 0; j < nArray[i].length; j++) {
				if (j < pattern[i].length()) {
					nArray[i][j] = (pattern[i].charAt(j) == 'x');
				} else {
					nArray[i][j] = false;
				}
			}
		}
		return rotateCW(nArray);
	}

	private static int width(String[] pattern) {
		int width = -1;
		if (width < 0) for (int i = 0; i < pattern.length; i++)
			if (pattern[i].length() > width) width = pattern[i].length();
		return width;
	}
	
	public Image getIcon() {
		int scale = 2;
		BufferedImage bi = new BufferedImage(scale * pattern.length+2, scale * pattern[0].length+2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		g.scale(scale, scale);
		g.setColor(Constants.toSwingColor(Constants.DEFAULTALIENDEATHCOLOR));
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
			for (int x = 0; x < pattern.length; x++) {
				for (int y = 0; y < pattern[x].length; y++) {
				if (pattern[x][y]) g.setColor(Constants.toSwingColor(Constants.DEFAULTALIENALIVECOLOR));
				else g.setColor(Constants.toSwingColor(Constants.DEFAULTALIENDEATHCOLOR));
				g.fillRect(x+1, y+1, 1, 1);
			}
		}
		return SwingFXUtils.toFXImage(bi, null);
	}
	public String getName() {
		return name;
	}

	public static void addPattern(String name, Pane patternBar, String... pattern) {
		if (PatternList.contains(pattern)) return;
		GoLPattern golp = new GoLPattern(name, pattern);
		PatternList.add(golp);
		patternBar.getChildren().add(golp.createButton());
	}

	public static void addPattern(String name, Pane patternBar, boolean[][] bPattern) {
		if (PatternList.contains(bPattern)) return;
		GoLPattern golp = new GoLPattern(name, bPattern);
		PatternList.add(golp);
		patternBar.getChildren().add(golp.createButton());
	}

	public static void fill(Pane patternBar, Stage stage) {
		addGUI(patternBar, stage);
		addPattern("Glider", patternBar, " x  ", "  x ", "xxx ");
		addPattern("Gun", patternBar, "                        x", "                      x x", "            xx      xx            xx", "           x   x    xx            xx", "xx        x     x   xx", "xx        x   x xx    x x", "          x     x       x", "           x   x", "            xx");
	}

	private static void addGUI(Pane patternBar, final Stage stage) {//TODO:
		Button newB = new Button(Localizer.get("toolBox.new"));
		newB.setMaxHeight(Double.MAX_VALUE);
		newB.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				GuiAddPattern.showDialog(stage);
			}
		});
		patternBar.getChildren().add(newB);
	}

	static boolean[][] rotateCW(boolean[][] mat) {
	    final int M = mat.length;
	    final int N = mat[0].length;
	    boolean[][] ret = new boolean[N][M];
	    for (int r = 0; r < M; r++) {
	        for (int c = 0; c < N; c++) {
	            ret[c][M-1-r] = mat[r][c];
	        }
	    }
	    return ret;
	}

	public boolean[][] clonePattern() {
		boolean[][] clone = new boolean[pattern.length][0];
		for (int i = 0; i < pattern.length; i++) {
			clone[i] = Arrays.copyOf(pattern[i], pattern[i].length);
		}
		return clone;
	}

	public boolean[][] cloneBackupPattern() {
		boolean[][] clone = new boolean[oldArray.length][0];
		for (int i = 0; i < oldArray.length; i++) {
			clone[i] = Arrays.copyOf(oldArray[i], oldArray[i].length);
		}
		return clone;
	}
	
	public boolean[][] getPattern() {
		return pattern;
	}

	public boolean[][] getBackupPattern() {
		return oldArray;
	}

	public void setBackupPattern(boolean[][] oldArray) {
		this.oldArray = oldArray;
	}
}
