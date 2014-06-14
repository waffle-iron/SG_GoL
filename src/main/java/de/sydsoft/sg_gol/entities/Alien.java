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
