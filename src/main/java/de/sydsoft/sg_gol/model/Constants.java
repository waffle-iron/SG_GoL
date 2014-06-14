/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.sydsoft.sg_gol.model;

import java.awt.Color;

/**
 *
 * @author syd
 */
public class Constants {
    /**
     * 
     */
    public static boolean ALIENBORDERACTIVE = true;
    /**
     * 
     */
    public static Color ALIENALIVECOLOR = new Color(0xffdcb1);
    /**
     * 
     */
    public static Color ALIENDEATHCOLOR = new Color(0x008000);//DeathColor = Background
    /**
     * 
     */
    public static final Color DEFAULTALIENALIVECOLOR = new Color(0xffdcb1);
    /**
     * 
     */
    public static final Color DEFAULTALIENDEATHCOLOR = new Color(0x008000);//DeathColor = Background
    /**
     * 
     */
    public static Color ComplementaryAliveColor(){
    	return new Color(255-ALIENALIVECOLOR.getRed(),255-ALIENALIVECOLOR.getGreen(),255-ALIENALIVECOLOR.getBlue());
    }
	public static Color ComplementaryDeathColor() {
    	return new Color(255-ALIENDEATHCOLOR.getRed(),255-ALIENDEATHCOLOR.getGreen(),255-ALIENDEATHCOLOR.getBlue());
	}
}
