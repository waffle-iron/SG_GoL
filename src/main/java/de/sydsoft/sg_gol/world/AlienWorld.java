package de.sydsoft.sg_gol.world;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JPanel;

import de.sydsoft.sg_gol.entities.Alien;
import de.sydsoft.sg_gol.model.AlienTimer;
import de.sydsoft.sg_gol.model.Constants;
import de.sydsoft.sg_gol.model.GoLPattern;
import de.sydsoft.sg_gol.model.PerlinNoise;

/**
 * 
 * @author syd
 */
public class AlienWorld extends JPanel implements MouseMotionListener {
	private static final long	serialVersionUID	= -3630357287023316946L;
	private int					anzx, anzy, yoff, xoff;
	private int					w, h, alienSize;
	private Alien[][]			al;
	private AlienTimer			at;
	private boolean				resized				= false;
	private boolean				stored				= false;
	private Graphics			offscreen;
	private PerlinNoise				pn					= new PerlinNoise();
	private GoLPattern			currentGoLP;

	/**
	 * 
	 * @param pixel
	 */
	public AlienWorld(int alienSize) {
		this.alienSize = alienSize;
		this.setBackground(Constants.ALIENDEATHCOLOR);
		initialize();
	}

	// Version 1 ohne umlaufendes Spielfeld
	/**
     * 
     */
	// public void callNextFlatStep() {
	// Alien[][] a = new Alien[anzx][anzy];
	// for (int i = 0; i < anzx; i++) {
	// for (int j = 0; j < anzy; j++) {
	// // a[i][j] = new Alien(clFill, bordact);
	// }
	// }
	// int count = 0;
	// for (int i = 0; i < anzx; i++) {
	// for (int j = 0; j < anzy; j++) {
	// count = countNeighborsFlat(i, j);
	// // if (al[i][j].isLive() == true) {
	// // if (count == 2 || count == 3) {
	// // al[i][j].setLive(true);
	// // }
	// // }
	// // if (al[i][j].isLive() == false && count == 3) {
	// // al[i][j].setLive(true);
	// // }
	// }
	// }
	// for (int i = 0; i < anzx; i++) {
	// for (int j = 0; j < anzy; j++) {
	// al[i][j] = al[i][j];
	// }
	// }
	// repaint();
	// }

	// Version 2 mit umlaufendem Spielfeld
	/**
     * 
     */
	public void callNextOrbicularStep() {
		Alien[][] a = new Alien[anzx][anzy];
		for (int i = 0; i < anzx; i++) {
			for (int j = 0; j < anzy; j++) {
				a[i][j] = new Alien();
			}
		}
		int count = 0;
		for (int i = 0; i < anzx; i++) {
			for (int j = 0; j < anzy; j++) {
				count = countNeighborsOrbicular(i, j);
				if (al[i][j].isAlive() == true) {
					if (count == 2 || count == 3) {
						a[i][j].setAlive(true);
					}
				}
				if (al[i][j].isAlive() == false && count == 3) {
					a[i][j].setAlive(true);
				}
			}
		}
		for (int i = 0; i < anzx; i++) {
			for (int j = 0; j < anzy; j++) {
				al[i][j] = a[i][j];
			}
		}
		repaint();
	}

	// Version 1 mit nicht umlaufenden Spielfeld

	/**
	 * 
	 * @param l
	 * @param b
	 * @return
	 */
	public int countNeighborsFlat(int l, int b) {
		int sum = 0;
		if (l > 0) {
			sum += living(al, l - 1, b);
			if (b > 0) {
				sum += living(al, l - 1, b - 1);
			}
			if (b + 1 != anzy) {
				sum += living(al, l - 1, b + 1);
			}
		}
		if (l < anzx) {
			if (b > 0) {
				sum += living(al, l, b - 1);
			}
			if (b + 1 != anzy) {
				sum += living(al, l, b + 1);
			}
		}

		if (l + 1 != anzx) {
			sum += living(al, l + 1, b);
			if (b > 0) {
				sum += living(al, l + 1, b - 1);
			}
			if (b + 1 != anzy) {
				sum += living(al, l + 1, b + 1);
			}
		}
		return sum;
	}

	// Version mit umlaufendem Spielfeld
	/**
	 * 
	 * @param l
	 * @param b
	 * @return
	 */
	public int countNeighborsOrbicular(int l, int b) {
		// anzx ->l
		// anzy ->b
		int sum = 0;

		// linke obere Ecke
		if (l == 0 && b == 0) {
			sum = living(al, 0, 1) + living(al, 0, anzy - 1) + living(al, 1, 0) + living(al, 1, 1) + living(al, anzx - 1, anzy - 1) + living(al, 1, anzy - 1) + living(al, anzx - 1, 1) + living(al, anzx - 1, 0);
		}
		// linke untere Ecke
		if (l == 0 && b == anzy - 1) {
			sum = living(al, 0, 0) + living(al, 1, 0) + living(al, anzx - 1, 0) + living(al, 0, b - 1) + living(al, 1, b - 1) + living(al, 1, b) + living(al, anzx - 1, b - 1) + living(al, anzx - 1, b);
		}
		// rechte obere Ecke
		if (l == anzx - 1 && b == 0) {
			sum = living(al, 0, 0) + living(al, 0, 1) + living(al, 0, anzy - 1) + living(al, l, anzy - 1) + living(al, l - 1, anzy - 1) + living(al, l - 1, 0) + living(al, l - 1, 1) + living(al, l, 1);
		}
		// rechte untere Ecke
		if (l == anzx - 1 && b == anzy - 1) {
			sum = living(al, l - 1, b) + living(al, l - 1, b - 1) + living(al, l, b - 1) + living(al, 0, b) + living(al, 0, b - 1) + living(al, 0, 0) + living(al, l - 1, 0) + living(al, l, 0);
		}
		// Mitte ohne 0.+n.Zeile, 0.+n.Spalte
		if (l > 0 && l < anzx - 1 && b > 0 && b < anzy - 1) {
			sum = living(al, l - 1, b) + living(al, l + 1, b) + living(al, l - 1, b + 1) + living(al, l - 1, b - 1) + living(al, l + 1, b + 1) + living(al, l + 1, b - 1) + living(al, l, b + 1) + living(al, l, b - 1);
		}

		// 0.Zeile, nicht Spalte 0,n
		if (b == 0 && l > 0 && l < anzx - 1) {
			sum = living(al, l - 1, b) + living(al, l + 1, b) + living(al, l - 1, b + 1) + living(al, l + 1, b + 1) + living(al, l, b + 1) + living(al, l - 1, anzy - 1) + living(al, l, anzy - 1) + living(al, l + 1, anzy - 1);
		}

		// n.Zeile, nicht Spalte 0,n
		if (b == anzy - 1 && l > 0 && l < anzx - 1) {
			sum = living(al, l - 1, b) + living(al, l + 1, b) + living(al, l - 1, b - 1) + living(al, l, b - 1) + living(al, l + 1, b - 1) + living(al, l - 1, 0) + living(al, l, 0) + living(al, l + 1, 0);
		}

		// 0.Spalte, nicht Zeile 0,n
		if (l == 0 && b > 0 && b < anzy - 1) {
			sum = living(al, l, b - 1) + living(al, l, b + 1) + living(al, l + 1, b - 1) + living(al, l + 1, b + 1) + living(al, l + 1, b) + living(al, anzx - 1, b - 1) + living(al, anzx - 1, b) + living(al, anzx - 1, b + 1);
		}

		// n.Spalte, nicht Zeile 0,n
		if (l == anzx - 1 && b > 0 && b < anzy - 1) {
			sum = living(al, l, b - 1) + living(al, l, b + 1) + living(al, l - 1, b) + living(al, l - 1, b - 1) + living(al, l - 1, b + 1) + living(al, 0, b) + living(al, 0, b - 1) + living(al, 0, b + 1);
		}
		return sum;

	}

	// // zum Schluss fertiges Bild auf den Bildschirm kopieren
	// g.drawImage(buffer, 0, 0, this);
	// }
	/**
	 * 
	 * @param ok
	 */
	public void createAlien(boolean ok) {
		if (ok) {
			h = getHeight();
			w = getWidth();
			anzy = ((h - alienSize) / alienSize);
			anzx = ((w - alienSize) / alienSize);
		}
		xoff = ((w - anzx * alienSize) / 2);
		yoff = ((h - anzy * alienSize) / 2);
		Alien[][] al2 = null;
		if (al != null) {
			al2 = al.clone();
		}
		al = new Alien[anzx][anzy];
		if (al2 != null) {
			int al2length = al2.length;
			int al2length2 = al2[0].length;
			if (al2length < anzx) {
				if (al2length2 < anzy) {
					for (int i = 0; i < al2length; i++) {
						for (int j = 0; j < al2length2; j++) {
							al[i][j] = al2[i][j];
						}
						for (int j = al2length2; j < anzy; j++) {
							al[i][j] = new Alien(pn.getb(i*10, j*10));
						}
					}
					for (int i = al2length; i < anzx; i++) {
						for (int j = 0; j < anzy; j++) {
							al[i][j] = new Alien(pn.getb(i*10, j*10));
						}
					}
				} else {
					for (int i = 0; i < al2length; i++) {
						for (int j = 0; j < al2length2; j++) {
							al[i][j] = al2[i][j];
						}
					}
					for (int i = al2length; i < anzx; i++) {
						for (int j = 0; j < anzy; j++) {
							al[i][j] = new Alien(pn.getb(i*10, j*10));
						}
					}
				}
			} else {
				if (al2length2 < anzy) {
					for (int i = 0; i < al2length; i++) {
						for (int j = 0; j < al2length2; j++) {
							al[i][j] = al2[i][j];
						}
						for (int j = al2length2; j < anzy; j++) {
							al[i][j] = new Alien(pn.getb(i*10, j*10));
						}
					}
				} else {
					for (int i = 0; i < anzx; i++) {
						for (int j = 0; j < anzy; j++) {
							al[i][j] = al2[i][j];
						}
					}
				}
			}
		} else {
			for (int i = 0; i < anzx; i++) {
				for (int j = 0; j < anzy; j++) {
					al[i][j] = new Alien(pn.getb(i*10, j*10));
				}
			}
		}
	}

	/**
     * 
     */
	public void drawRaster() {
		for (int i = 0; i < anzx; i++) {
			for (int j = 0; j < anzy; j++) {
				al[i][j].drawAlien(offscreen, xoff + alienSize * i, yoff + alienSize * j, alienSize);
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	public Alien[][] getAl() {
		return al;
	}

	/**
	 * 
	 * @return
	 */
	public int getAnzx() {
		return anzx;
	}

	/**
	 * 
	 * @return
	 */
	public int getAnzy() {
		return anzy;
	}

	/**
	 * 
	 * @return
	 */
	public int getH() {
		return h;
	}

	/**
	 * 
	 * @return
	 */
	public int getAlienSize() {
		return alienSize;
	}

	/**
	 * 
	 * @return
	 */
	public AlienTimer getTimer() {
		return at;
	}

	/**
	 * 
	 * @return
	 */
	public int getW() {
		return w;
	}

	private void initialize() {
		// buffer = createImage(getWidth(), getHeight());
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent arg0) {
				resized = true;
			}
		});
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.printf("Mouse clicke @: %d|%d", e.getX(), e.getY());
				int xcl = (e.getX() - xoff) / alienSize;
				int ycl = (e.getY() - yoff) / alienSize;
				if ((e.getButton() == MouseEvent.BUTTON1)) {
					if (currentGoLP == null) {
						al[xcl][ycl].setAlive(!al[xcl][ycl].isAlive());
					} else {
						placePattern(xcl, ycl);
					}
				}
				if ((e.getButton() == MouseEvent.BUTTON2)) {
					if (currentGoLP != null) clearTemplate(xcl, ycl);
					currentGoLP = null;
				}
				if ((e.getButton() == MouseEvent.BUTTON3)) {
					if (currentGoLP != null) clearBackground(xcl, ycl);
				}
				repaint();
			}
		});
		addMouseMotionListener(this);
		requestFocus();
	}

	/**
	 * 
	 * @return
	 */
	public boolean isResized() {
		return resized;
	}

	private int living(Alien[][] a, int l, int b) {
		if (a[l][b].isAlive()) { return 1; }
		return 0;
	}

	// Zeichenmethode auf Graphics
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// g.setColor(Color.YELLOW);
		offscreen = g;

		if (resized) {
			createAlien(true);
			// setRandom(true);

			drawRaster();
			resized = false;
		} else {
			drawRaster();
		}
		if (stored) {
			stored = false;
		}
	}

	/**
     * 
     */
	public void clearAll() {
		for (int i = 0; i < anzx; i++) {
			for (int j = 0; j < anzy; j++) {
				al[i][j].setAlive(false);
			}
		}
		repaint();
	}

	/**
	 * 
	 * @param al
	 */
	public void setAl(Alien[][] al) {
		this.al = al;
	}

	/**
	 * 
	 * @param anzx
	 */
	public void setAnzx(int anzx) {
		this.anzx = anzx;
	}

	/**
	 * 
	 * @param anzy
	 */
	public void setAnzy(int anzy) {
		this.anzy = anzy;
	}

	/**
	 * 
	 * @param clBack
	 */
	public void setBackgroundColor(Color clBack) {
		Constants.ALIENDEATHCOLOR = clBack;
		this.setBackground(Constants.ALIENDEATHCOLOR);
		repaint();
	}

	/**
	 * 
	 * @param clFill
	 */
	public void setAlienColor(Color clFill) {
		Constants.ALIENALIVECOLOR = clFill;
	}

	/**
	 * 
	 * @param h
	 */
	public void setH(int h) {
		this.h = h;
	}

	/**
	 * 
	 * @param pixel
	 */
	public void setAlienSize(int alienSize) {
		this.alienSize = alienSize;
	}

	/**
	 * 
	 * @param ok
	 */
	public void random() {
		PerlinNoise pn = new PerlinNoise(8);
		for (int i = 0; i < anzx; i++) {
			for (int j = 0; j < anzy; j++) {
				al[i][j].setAlive(pn.getb(i*10, j*10));
			}
		}
		repaint();
	}

	/**
	 * 
	 * @param resized
	 */
	public void setResized(boolean resized) {
		this.resized = resized;
	}

	/**
	 * 
	 * @param stored
	 */
	public void setStored(boolean stored) {
		this.stored = stored;
	}

	/**
	 * 
	 * @param sleept
	 */
	public void setTimer(int sleept) {
		at = new AlienTimer(this, sleept);
	}

	/**
	 * 
	 * @param w
	 */
	public void setW(int w) {
		this.w = w;
	}

	/**
	 * 
	 * @return
	 */
	public boolean sizeIsChanged() {
		return resized;
	}

	/**
     * 
     */
	public void stopTimer() {
		at.stopThread();
	}

	@Override
	public void update(Graphics g) {
		// g.setColor(Color.YELLOW);
		// offscreen = g;
		// h = getHeight();
		// w = getWidth();
		// anzy = (h - pixel) / pixel;
		// anzx = (w - pixel) / pixel;
		// xoff = (w - anzx * pixel) / 2;
		// yoff = (h - anzy * pixel) / 2;
		//
		// if(start==0){
		// ev = new GameBoard(anzx, anzy, xoff, yoff, pixel);
		// start++;
		// }
		// al = new Alien[anzx][anzy];
		//
		// for (int i = 0; i < anzx; i++) {
		// for (int j = 0; j < anzy; j++) {
		// al[i][j] = new Alien();
		// }
		// }
		//
		// for (int i = 0; i < anzx; i++) {
		// for (int j = 0; j < anzy; j++) {
		// al[i][j].drawAlien(offscreen, xoff + pixel * i, yoff
		// + pixel * j, pixel);
		// }
		// }
	}

	public void setCurrentPattern(GoLPattern golP) {
		this.currentGoLP = golP;
	}

	@Override
	public void mouseDragged(MouseEvent e) {}

	private Point	lastP	= new Point(-1, -1);

	@Override
	public void mouseMoved(MouseEvent e) {
		if (currentGoLP == null) return;
		Point newP = new Point((e.getX() - xoff) / alienSize, (e.getY() - yoff) / alienSize);
		if (newP.equals(lastP)) return;
		if (isPatternInUse()) clearTemplate(lastP.x, lastP.y);
		lastP = newP;
		createTemplate(newP.x, newP.y);
		repaint();
	}

	private void placePattern(int x, int y) {
		System.out.printf("Pattern placed @: %d|%d\n", x, y);
		boolean[][] newArray = currentGoLP.clonePattern();
		currentGoLP.setBackupPattern(newArray);
		for (int dx = 0; dx < newArray.length; dx++) {
			for (int dy = 0; dy < newArray[dx].length; dy++) {
				int xPos = (dx + x < al.length) ? dx + x : newArray.length - 1 - dx;
				int yPos = (dy + y < al[0].length) ? dy + y : newArray[dx].length - 1 - dy;
				al[xPos][yPos].setTemplate(false);
				al[xPos][yPos].setAlive(newArray[dx][dy]);
			}
		}
	}

	private void createTemplate(int x, int y) {
		System.out.printf("Pattern created @: %d|%d\n", x, y);
		boolean[][] oldArray = currentGoLP.cloneBackupPattern();
		boolean[][] newArray = currentGoLP.clonePattern();
		for (int dx = 0; dx < newArray.length; dx++) {
			for (int dy = 0; dy < newArray[dx].length; dy++) {
				int xPos = (dx + x < al.length) ? dx + x : newArray.length - 1 - dx;
				int yPos = (dy + y < al[0].length) ? dy + y : newArray[dx].length - 1 - dy;
				al[xPos][yPos].setTemplate(true);
				oldArray[dx][dy] = al[xPos][yPos].isAlive();
				al[xPos][yPos].setAlive(newArray[dx][dy]);
			}
		}
		currentGoLP.setBackupPattern(oldArray);
	}

	private void clearBackground(int x, int y) {
		System.out.printf("Background cleared @: %d|%d\n", x, y);
		boolean[][] oldArray = currentGoLP.cloneBackupPattern();
		for (int dx = 0; dx < oldArray.length; dx++) {
			for (int dy = 0; dy < oldArray[dx].length; dy++) {
				int xPos = (dx + x < al.length) ? dx + x : oldArray.length - 1 - dx;
				int yPos = (dy + y < al[0].length) ? dy + y : oldArray[dx].length - 1 - dy;
				oldArray[dx][dy] = false;
				al[xPos][yPos].setAlive(false);
				al[xPos][yPos].setTemplate(false);
			}
		}
		currentGoLP.setBackupPattern(oldArray);
	}

	public void clearTemplate(Point p) {
		clearTemplate(p.x, p.y);
	}
	
	private void clearTemplate(int x, int y) {
		if (currentGoLP == null) return;
		boolean[][] oldArray = currentGoLP.cloneBackupPattern();
		for (int dx = 0; dx < oldArray.length; dx++) {
			for (int dy = 0; dy < oldArray[dx].length; dy++) {
				int xPos = (dx + x < al.length) ? dx + x : oldArray.length - 1 - dx;
				int yPos = (dy + y < al[0].length) ? dy + y : oldArray[dx].length - 1 - dy;
				al[xPos][yPos].setAlive(oldArray[dx][dy]);
				al[xPos][yPos].setTemplate(false);
			}
		}
		System.out.printf("Pattern cleared @: %d|%d\n", x, y);
	}

	public Point getLastP() {
		return lastP;
	}
	
	public void setLastP(Point lastP) {
		this.lastP = lastP;
	}
	
	public boolean isPatternInUse() {
		return lastP.x+lastP.y>-2;
	}
	
	public void resetPatternInUse(){
		clearTemplate(lastP);
		lastP = new Point(-1, -1);
		currentGoLP = null;
	}

	public static void main(String[] args) {
		boolean[][] as = new boolean[][] { { true, true, false }, { false, false, true }, { true, true, true } };
		boolean[][] bs = new boolean[as.length][0];
		for (int i = 0; i < as.length; i++) {
			bs[i] = Arrays.copyOf(as[i], as[i].length);
		}
		System.out.println("bevore the Test");
		System.out.println("as: " + Arrays.deepToString(as));
		System.out.println("bs: " + Arrays.deepToString(bs));
		Random r = new Random();
		for (int i = 0; i < bs.length; i++) {
			for (int j = 0; j < bs[i].length; j++) {
				as[i][j] = r.nextBoolean();
			}
		}
		System.out.println("after the Test");
		System.out.println("as: " + Arrays.deepToString(as));
		System.out.println("bs: " + Arrays.deepToString(bs));
	}
}
