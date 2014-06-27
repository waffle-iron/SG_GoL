package de.sydsoft.sg_gol.entities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.Serializable;

import de.sydsoft.sg_gol.model.Constants;

/**
 * 
 * @author syd
 */
public class Alien implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean alive = false;
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
    public void drawAlien(Graphics2D g, int x, int y, int max) {
        if (alive) {
        	if (template)
        		g.setColor(Constants.toSwingColor(Constants.ComplementaryAliveColor()));
			else
				g.setColor(Constants.toSwingColor(Constants.ALIENALIVECOLOR));
        } else {
        	if (template){
        		g.setColor(Constants.toSwingColor(Constants.ComplementaryDeathColor()));
        	}
        	g.setColor(Constants.toSwingColor(Constants.ALIENDEATHCOLOR));
        }
		g.fillRect(x, y, max, max);
        if (Constants.ALIENBORDERACTIVE) {
        	g.setColor(Color.BLACK);
            g.drawRect(x, y, max, max);
        }
    }
}
