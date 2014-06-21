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

<<<<<<< Updated upstream
import javax.swing.JPanel;

import de.sydsoft.libst.util.Console;
=======
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireFrustum;
import com.jme3.shadow.BasicShadowRenderer;
>>>>>>> Stashed changes

import de.sydsoft.sg_gol.entities.Alien;
import de.sydsoft.sg_gol.model.Constants;
import de.sydsoft.sg_gol.model.GoLPattern;

/**
 * 
 * @author syd
 */
<<<<<<< Updated upstream
public class AlienWorld extends JPanel implements MouseMotionListener {
	private static final long	serialVersionUID	= -3630357287023316946L;
	private int					anzx, anzy, yoff, xoff;
	private int					w, h, alienSize;
	private Alien[][]			al;
	private AlienTimer			at;
	private boolean				resized				= false;
	private boolean				stored				= false;
	private Graphics			offscreen;
	private Random				rnd					= new Random();
	private GoLPattern			currentGoLP;
=======
public class AlienWorld extends AbstractAppState {
	public static final int		X_OFFSET				= 1;
	public static final int		Z_OFFSET				= 1;
	private static int			anzX;
	private static int			anzZ;
	private Geometry			field;
	private Alien[][]			aliens;
	private boolean				resized					= false;
	private Random				rnd						= new Random();
	private GoLPattern			currentGoLP;
	// public boolean leftC, rightC, middleC;
	public float				mouseX, mouseY;
	private Point				alienGoLPAddressLast	= new Point(-1, -1);
	private float				waitTime;
	private float				speed					= .1f;

	Geometry					frustumMdl;
	WireFrustum					frustum;
	private BasicShadowRenderer	bsr;

	private Node				rootNode;
	private Node				guiNode;
	private Node				stateRootNode			= new Node("AlienworldRootNode");
	private Node				stateGuiNode			= new Node("AlienWorldGuiNode");
	private Node				alienNode				= new Node("AlienRootNode");
	private SpotLight			cursor;
	private PointLight			cursor2;

	Geometry					cube1Geo;

	public AlienWorld(SimpleApplication app, int anzX, int anzZ) {
		this.rootNode = app.getRootNode();
		this.guiNode = app.getGuiNode();
		this.anzX = anzX;
		this.anzZ = anzZ;
		aliens = new Alien[anzX][anzZ];
	}

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		SimpleApplication sapp = (SimpleApplication) app;
		stateRootNode.setShadowMode(ShadowMode.Off);

		// sapp.getCamera().lookAt(cube1Geo.getWorldTranslation(),
		// Vector3f.UNIT_Z.negate());
		// sapp.getCamera().setLocation(cube1Geo.getLocalTranslation().add(0, 0,
		// 10f));

		initializeField();
		fillField();
		initializeLight();
		// initializeShadows(app);

		sapp.getFlyByCamera().setMoveSpeed(100f);
		sapp.getFlyByCamera().setRotationSpeed(10);

		setEnabled(false);
	}

	private void initializeShadows(Application app) {
		bsr = new BasicShadowRenderer(app.getAssetManager(), 512);
		bsr.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
		app.getViewPort().addProcessor(bsr);

		frustum = new WireFrustum(bsr.getPoints());
		frustumMdl = new Geometry("f", frustum);
		frustumMdl.setCullHint(Spatial.CullHint.Never);
		frustumMdl.setShadowMode(ShadowMode.Off);
		frustumMdl.setMaterial(new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md"));
		frustumMdl.getMaterial().getAdditionalRenderState().setWireframe(true);
		frustumMdl.getMaterial().setColor("Color", ColorRGBA.Red);
		stateRootNode.attachChild(frustumMdl);
	}

	private void initializeLight() {
		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(1, -10, -2).normalizeLocal());
		sun.setColor(ColorRGBA.White);
		stateRootNode.addLight(sun);

		// cursor = new SpotLight();
		// cursor.setSpotRange(100f);
		// cursor.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD);
		// cursor.setSpotOuterAngle(25f * FastMath.DEG_TO_RAD);
		// cursor.setColor(ColorRGBA.White.mult(1.3f));
		// cursor.setPosition(SwingGameClient.getApplication().getCamera().getLocation());
		// cursor.setDirection(SwingGameClient.getApplication().getCamera().getDirection());
		// stateRootNode.addLight(cursor);

		// cursor2 = new PointLight();
		// cursor2.setColor(Constants.ALIENALIVECOLOR.mult(1.3f));
		// cursor2.setPosition(new Vector3f(0, 1, 0));
		// cursor2.setRadius(50f);
		// stateRootNode.addLight(cursor2);

	}

	private void initializeField() {
		field = Field.createNew(getAnzX(), getAnzZ());

		Vector3f v1 = new Vector3f(+getAnzX() + AlienWorld.X_OFFSET, 0, -getAnzZ() - AlienWorld.Z_OFFSET);

		SpotLight spot = new SpotLight();
		spot.setSpotRange(100f); // distance
		spot.setSpotInnerAngle(15f * FastMath.DEG_TO_RAD); // inner light cone
															// (central beam)
		spot.setSpotOuterAngle(25f * FastMath.DEG_TO_RAD); // outer light cone
															// (edge of the
															// light)
		spot.setColor(ColorRGBA.White.mult(1.3f)); // light color
		spot.setPosition(v1.add(0, 50, 0)); // shine from camera loc
		spot.setDirection(field.getLocalTranslation()); // shine forward from
														// camera loc
		rootNode.addLight(spot);

		stateRootNode.attachChild(field);
	}

	// private Alien getAlienAt(int x, int z) {
	// Spatial sp = stateRootNode.getChild(Alien.CoordinatesToString(x, z));
	// if (sp instanceof Alien) {
	// return (Alien) sp;
	// } else {
	// return null;
	// }
	// }

	@Override
	public void update(float tpf) {
		super.update(tpf);

		waitTime += tpf;
		if (waitTime < speed) return;
		waitTime -= speed;
		callNextOrbicularStep();
	}

	@Override
	public void render(RenderManager rm) {
		if (resized) {
			// createAlien(true);
			resized = false;
		}
	}
>>>>>>> Stashed changes

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
<<<<<<< Updated upstream
		Alien[][] a = new Alien[anzx][anzy];
		for (int i = 0; i < anzx; i++) {
			for (int j = 0; j < anzy; j++) {
				a[i][j] = new Alien();
			}
		}
=======

		boolean[][] a = new boolean[getAnzX()][getAnzZ()];
		// for (int x = 0; x < getAnzX(); x++) {
		// for (int z = 0; z < getAnzZ(); z++) {
		// a[x][z] = true;//Alien.createAlien(x, z, Constants.ALIENALIVECOLOR);
		// }
		// }
>>>>>>> Stashed changes
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
							al[i][j] = new Alien(rnd.nextBoolean());
						}
					}
					for (int i = al2length; i < anzx; i++) {
						for (int j = 0; j < anzy; j++) {
							al[i][j] = new Alien(rnd.nextBoolean());
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
							al[i][j] = new Alien(rnd.nextBoolean());
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
							al[i][j] = new Alien(rnd.nextBoolean());
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
					al[i][j] = new Alien(rnd.nextBoolean());
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
<<<<<<< Updated upstream
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
=======
>>>>>>> Stashed changes
	 * @param ok
	 */
	public void random() {
		Random rnd = new Random();
		for (int i = 0; i < anzx; i++) {
			for (int j = 0; j < anzy; j++) {
				al[i][j].setAlive(rnd.nextBoolean());
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

<<<<<<< Updated upstream
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
=======
	private interface AlienConsumer {
		void accept(Alien al, boolean oldVal, boolean newVal);
	}

	private void work(Point ref, AlienConsumer alCons) {
>>>>>>> Stashed changes
		boolean[][] newArray = currentGoLP.clonePattern();
		boolean[][] oldArray = currentGoLP.cloneBackupPattern();
		for (int dx = 0; dx < newArray.length; dx++) {
<<<<<<< Updated upstream
			for (int dy = 0; dy < newArray[dx].length; dy++) {
				int xPos = (dx + x < al.length) ? dx + x : newArray.length - 1 - dx;
				int yPos = (dy + y < al[0].length) ? dy + y : newArray[dx].length - 1 - dy;
				al[xPos][yPos].setTemplate(false);
				al[xPos][yPos].setAlive(newArray[dx][dy]);
=======
			for (int dz = 0; dz < newArray[dx].length; dz++) {
				int xPos = dx + ref.x;
				int zPos = dz + ref.y;
				while (xPos >= getAnzx()) xPos -= getAnzx();
				while (zPos >= getAnzZ()) zPos -= getAnzZ();
				// int xPos = (dx + ref.x < getAnzX()) ? dx + ref.x :
				// newArray.length - 1 - dx;
				// int zPos = (dz + ref.y < getAnzZ()) ? dz + ref.y :
				// newArray[dx].length - 1 - dz;
				System.out.printf("oldArray[%d][%d] = %5b|newArray[%d][%d] = %5b...", dx,dz,oldArray[dx][dz],dx,dz,newArray[dx][dz]);
				alCons.accept(aliens[xPos][zPos], oldArray[dx][dz], newArray[dx][dz]);
				System.out.printf("oldArray[%d][%d] = %5b|newArray[%d][%d] = %5b\n", dx,dz,oldArray[dx][dz],dx,dz,newArray[dx][dz]);
>>>>>>> Stashed changes
			}
		}
	}

<<<<<<< Updated upstream
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
=======
	private void placePattern(Point placeCoord) {
		boolean[][] newArray = currentGoLP.clonePattern();
		currentGoLP.setBackupPattern(newArray);
		work(placeCoord, new AlienConsumer() {
			@Override
			public void accept(Alien al, boolean oldVal, boolean newVal) {
				al.setTemplate(false);
				al.setAlive(newVal);
			}
		});
//		System.out.printf("Pattern placed @: %d|%d\n", placeCoord.x, placeCoord.y);
	}

	private void createTemplate(Point placeCoord) {
		boolean[][] oldArray = currentGoLP.cloneBackupPattern();
		work(placeCoord, new AlienConsumer() {
			@Override
			public void accept(Alien al, boolean oldVal, boolean newVal) {
				al.setTemplate(true);
				oldVal = al.isAlive();
				al.setAlive(newVal);
>>>>>>> Stashed changes
			}
		});
		currentGoLP.setBackupPattern(oldArray);
//		System.out.printf("Pattern created @: %d|%d\n", placeCoord.x, placeCoord.y);
	}

	private void clearBackground(int x, int y) {
		System.out.printf("Background cleared @: %d|%d\n", x, y);
		boolean[][] oldArray = currentGoLP.cloneBackupPattern();
<<<<<<< Updated upstream
		for (int dx = 0; dx < oldArray.length; dx++) {
			for (int dy = 0; dy < oldArray[dx].length; dy++) {
				int xPos = (dx + x < al.length) ? dx + x : oldArray.length - 1 - dx;
				int yPos = (dy + y < al[0].length) ? dy + y : oldArray[dx].length - 1 - dy;
				oldArray[dx][dy] = false;
				al[xPos][yPos].setAlive(false);
				al[xPos][yPos].setTemplate(false);
			}
		}
=======
		work(placeCoord, new AlienConsumer() {
			@Override
			public void accept(Alien al, boolean oldVal, boolean newVal) {
				oldVal = false;
				al.setAlive(false);
				al.setTemplate(false);
			}
		});
>>>>>>> Stashed changes
		currentGoLP.setBackupPattern(oldArray);
//		System.out.printf("Background cleared @: %d|%d\n", placeCoord.x, placeCoord.y);
	}

	public void clearTemplate(Point p) {
		clearTemplate(p.x, p.y);
	}
	
	private void clearTemplate(int x, int y) {
		if (currentGoLP == null) return;
<<<<<<< Updated upstream
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
=======
		work(placeCoord, new AlienConsumer() {
			@Override
			public void accept(Alien al, boolean oldVal, boolean newVal) {
				al.setAlive(oldVal);
				al.setTemplate(false);
			}
		});
//		System.out.printf("Pattern cleared @: %d|%d\n", placeCoord.x, placeCoord.y);
	}

	public boolean isPatternInUse() {
		return alienGoLPAddressLast.x + alienGoLPAddressLast.y > -2;
>>>>>>> Stashed changes
	}
	
	public void setLastP(Point lastP) {
		this.lastP = lastP;
	}
	
	public boolean isPatternInUse() {
		return lastP.x+lastP.y>-2;
	}
<<<<<<< Updated upstream
	
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
=======

	public void setCursorPos(Vector2f cursorPos) {
		if (field == null) return;
		CollisionResults results = new CollisionResults();
		Camera cam = SwingGameClient.getApplication().getCamera();
		Vector3f click3d = cam.getWorldCoordinates(new Vector2f(cursorPos.x, cursorPos.y), 0f).clone();
		Vector3f dir = cam.getWorldCoordinates(new Vector2f(cursorPos.x, cursorPos.y), 1f).subtractLocal(click3d).normalizeLocal();

		if (cursor != null) cursor.setDirection(dir);

		Ray ray = new Ray(click3d, dir);
		field.collideWith(ray, results);
		if (results.size() > 0 && cursor2 != null) {
			cursor2.setPosition(results.getClosestCollision().getContactPoint().add(0, 1, 0));
		}
		if (currentGoLP != null) {
			results = new CollisionResults();
			alienNode.collideWith(ray, results);
			if (results.size() > 0) {
				Alien al = (Alien) results.getClosestCollision().getGeometry();
				Point alienGoLPAddress = Alien.StringToCoordinates(al.getName());
				if (!alienGoLPAddress.equals(alienGoLPAddressLast)) {
					if (isPatternInUse()) clearTemplate(alienGoLPAddressLast);
					alienGoLPAddressLast = alienGoLPAddress;
					createTemplate(alienGoLPAddress);
				}
			}
		}
	}

	public void rightClick(Vector2f cursorPos) {
		if (currentGoLP != null) {
			CollisionResults results = new CollisionResults();
			Camera cam = SwingGameClient.getApplication().getCamera();
			Vector3f click3d = cam.getWorldCoordinates(new Vector2f(cursorPos.x, cursorPos.y), 0f).clone();
			Vector3f dir = cam.getWorldCoordinates(new Vector2f(cursorPos.x, cursorPos.y), 1f).subtractLocal(click3d).normalizeLocal();
			Ray ray = new Ray(click3d, dir);
			alienNode.collideWith(ray, results);
			if (results.size() > 0) {
				Alien al = (Alien) results.getClosestCollision().getGeometry();
				clearBackground(Alien.StringToCoordinates(al.getName()));
			}
		}
	}

	public void middleClick(Vector2f cursorPos) {
		if (currentGoLP != null) {
			CollisionResults results = new CollisionResults();
			Camera cam = SwingGameClient.getApplication().getCamera();
			Vector3f click3d = cam.getWorldCoordinates(new Vector2f(cursorPos.x, cursorPos.y), 0f).clone();
			Vector3f dir = cam.getWorldCoordinates(new Vector2f(cursorPos.x, cursorPos.y), 1f).subtractLocal(click3d).normalizeLocal();
			Ray ray = new Ray(click3d, dir);
			alienNode.collideWith(ray, results);
			if (results.size() > 0) {
				Alien al = (Alien) results.getClosestCollision().getGeometry();
				clearTemplate(Alien.StringToCoordinates(al.getName()));
				currentGoLP = null;
>>>>>>> Stashed changes
			}
		}
		System.out.println("after the Test");
		System.out.println("as: " + Arrays.deepToString(as));
		System.out.println("bs: " + Arrays.deepToString(bs));
	}
}
