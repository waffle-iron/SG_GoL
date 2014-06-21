package de.sydsoft.sg_gol.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

import de.sydsoft.sg_gol.model.Constants;

/**
 * 
 * @author syd
 */
public class Alien implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean alive = false;
    private Color clBorder = Color.black;
    private boolean template;

    /**
     * 
     */
    public Alien() {
    }
    
    
    /**
     * 
     * @param alive
     */
    public Alien( boolean alive) {
        this.alive = alive;
    }

    /**
     * 
     * @return
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * 
     * @param alive
     */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }   

<<<<<<< Updated upstream
=======
	private static Texture2D[] drawTexture(int sizeX, int sizeY, ColorRGBA backgroundColor) {
		Texture2D[] ret = new Texture2D[2];
		BufferedImage bimg = new BufferedImage(sizeX, sizeX, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimg.getGraphics();
		g.setColor(new Color(backgroundColor.asIntARGB()));
		g.fillRect(0, 0, bimg.getWidth(), bimg.getHeight());
		g.dispose();
		ret[0] = new Texture2D(new AWTLoader().load(bimg, false));
		bimg = new BufferedImage(sizeX, sizeX, BufferedImage.TYPE_INT_ARGB);
		g = bimg.getGraphics();
		g.setColor(Color.YELLOW);
		g.fillRect(0, 0, bimg.getWidth(), bimg.getHeight());
		g.dispose();
		ret[1] = new Texture2D(new AWTLoader().load(bimg, false));
		return ret;
	}

	public static String CoordinatesToString(int z,int x) {
		String s = ConvertColumnNumberToString(z) + (++x);
		if (s.matches("\\d+")) s = "A" + s;
		return s;
	}

	public static Point StringToCoordinates(String s){
		Point p = new Point(0,0);
		String letters = s.split("\\d+")[0];
		p.y = Integer.parseInt(s.replace(letters, ""))-1;
		p.x = ConvertStringToColumnNumber(letters);
		return p;
	}
	
	private static int ConvertStringToColumnNumber(String s){
		int ret = 0;
		for (int i = 0; i < s.length(); i++) {
			ret *= 26;
			ret += (s.charAt(i)-65);
		}
		return ret;
	}
	
	private static String ConvertColumnNumberToString(int i) {
		if (i == 0) return "";
		int count = i / 26;
		char bar = (char) ((i % 26) + 65);
		StringBuilder sb = new StringBuilder(ConvertColumnNumberToString(count)).append(bar);
		return sb.toString();
	}

//	 public static void main(String[] args) {
//		 System.out.println("Convert Numbers:");
//		 // (0,0)A1(0.0, 49.0)
//		 System.out.println(StringToCoordinates("A1")+":"+CoordinatesToString(0,0));
//		 // (0,1)A2	 (0.0, 50.0)
//		 System.out.println(StringToCoordinates("A2")+":"+CoordinatesToString(1,0));
//		 // (1,1)B2 (1.0, 50.0)
//		 System.out.println(StringToCoordinates("B2")+":"+CoordinatesToString(1,1));
//		 // (54,22)CC23 (50.0, 101.0)
//		 System.out.println(StringToCoordinates("CC23")+":"+CoordinatesToString(22,54));
//	 }

	private Alien() {}

	private Alien(ColorRGBA color, boolean alive, String string, Mesh mesh) {
		super(string, mesh);
		if (alive) setCullHint(CullHint.Never);
		else setCullHint(CullHint.Always);
		this.color = color;
	}

	// /**
	// *
	// * @return
	// */
	public boolean isAlive() {
		return cullHint == CullHint.Never;
	}

	/**
	 * 
	 * @param alive
	 */
	public void setAlive(boolean alive) {
		if (alive) setCullHint(CullHint.Never);
		else setCullHint(CullHint.Always);
	}
>>>>>>> Stashed changes

	public boolean isTemplate() {
		return template;
	}


	public void setTemplate(boolean template) {
		this.template = template;
	}

    /**
     * 
     * @param g
     * @param x
     * @param y
     * @param max
     */
    public void drawAlien(Graphics g, int x, int y, int max) {
        if (alive) {
        	if (template)
        		g.setColor(Constants.ComplementaryAliveColor());
			else
				g.setColor(Constants.ALIENALIVECOLOR);
            g.fillRect(x, y, max, max);
        } else 
        	if (template){
        		g.setColor(Constants.ComplementaryDeathColor());
        		g.fillRect(x, y, max, max);
        	}
        if (Constants.ALIENBORDERACTIVE) {
        	g.setColor(clBorder);
            g.drawRect(x, y, max, max);
        }
    }
}
