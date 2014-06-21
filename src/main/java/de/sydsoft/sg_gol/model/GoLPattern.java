package de.sydsoft.sg_gol.model;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import de.sydsoft.sg_gol.gui.swing.GuiAddPattern;
import de.sydsoft.sg_gol.gui.swing.GuiGameOfLife;
import de.sydsoft.sg_gol.lang.Localizer;
import de.sydsoft.sg_gol.world.AlienWorld;

public class GoLPattern implements ActionListener {
	private static GoLPatternList	PatternList	= new GoLPatternList();
	private boolean[][]				oldArray;
	private boolean[][]				pattern;
	private String					name;

	public GoLPattern(String name, String[] pattern, JToolBar patternBar) {
		this.name = name;
		this.oldArray = this.pattern = parsePattern(pattern);
		patternBar.add(createButton());
	}

	public GoLPattern(String name, boolean[][] pattern, JToolBar patternBar) {
		this.name = name;
		this.oldArray = this.pattern = pattern;
		patternBar.add(createButton());
	}

	private JButton createButton() {
		JButton b = new JButton();
		b.setIcon(this.getIcon());
		b.setText(this.getName());
		b.setVerticalTextPosition(JButton.TOP);
		b.setHorizontalTextPosition(JButton.CENTER);
		b.addActionListener(this);
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
	
	public Icon getIcon() {
		int scale = 2;
		BufferedImage bi = new BufferedImage(scale * pattern.length+2, scale * pattern[0].length+2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();
		g.scale(scale, scale);
		g.setColor(Constants.DEFAULTALIENDEATHCOLOR);
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
			for (int x = 0; x < pattern.length; x++) {
				for (int y = 0; y < pattern[x].length; y++) {
				if (pattern[x][y]) g.setColor(Constants.DEFAULTALIENALIVECOLOR);
				else g.setColor(Constants.DEFAULTALIENDEATHCOLOR);
				g.fillRect(x+1, y+1, 1, 1);
			}
		}
		return new ImageIcon(bi);
	}
	public String getName() {
		return name;
	}

	public static void addPattern(String name, JToolBar patternBar, String... pattern) {
		if (PatternList.contains(pattern)) return;
		PatternList.add(new GoLPattern(name, pattern, patternBar));
	}

	public static void addPattern(String name, JToolBar patternBar, boolean[][] bPattern) {
		if (PatternList.contains(bPattern)) return;
		PatternList.add(new GoLPattern(name, bPattern, patternBar));
	}

	public static void fill(JToolBar patternBar) {
		addGUI(patternBar);
		addPattern("Glider", patternBar, " x  ", "  x ", "xxx ");
		addPattern("Gun", patternBar, "                        x", "                      x x", "            xx      xx            xx", "           x   x    xx            xx", "xx        x     x   xx", "xx        x   x xx    x x", "          x     x       x", "           x   x", "            xx");
	}

	private static void addGUI(JToolBar patternBar) {
		JButton newB = new JButton();
		newB.setText(Localizer.get("toolBox.new"));
		newB.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				GuiAddPattern.showDialog();
			}
		});
		patternBar.add(newB);
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JButton) {
			JButton eventButton = (JButton) e.getSource();
			GoLPattern goLPB = PatternList.get(eventButton.getText());
			System.out.println(eventButton.getText()+" selected.");
			if (goLPB != null) {
				GuiGameOfLife.getInstance().stop();
				AlienWorld al = GuiGameOfLife.getInstance().getAlienWorld();
				if (al.isPatternInUse()) {
					al.resetPatternInUse();
				}
				al.setCurrentPattern(goLPB);
			}
		}
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
