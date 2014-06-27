/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.sydsoft.sg_gol.model;

import javafx.scene.paint.Color;

/**
 *	http://serennu.com/colour/colourcalculator.php
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
    public static final Color DEFAULTALIENALIVECOLOR = Color.web("0xFFDCB1");
    /**
     * 
     */
    public static final Color DEFAULTALIENDEATHCOLOR = Color.web("0x008000");//DeathColor = Background
    /**
     * 
     */
    public static Color ALIENALIVECOLOR = DEFAULTALIENALIVECOLOR;
    /**
     * 
     */
    public static Color ALIENDEATHCOLOR = DEFAULTALIENDEATHCOLOR;//DeathColor = Background
    /**
     * 
     */
    public static Color ComplementaryAliveColor(){
    	return ALIENALIVECOLOR.invert();
    }
	public static Color ComplementaryDeathColor() {
    	return ALIENDEATHCOLOR.invert();
	}

	public static final java.awt.Color toSwingColor(Color javafxColor){
		int r = (int) (javafxColor.getRed() * 255);
		int g = (int) (javafxColor.getGreen() * 255);
		int b = (int) (javafxColor.getBlue() * 255);
		 
		//This is the combined sRGB value which can be passed to a Swing Color object.
		int rgb = (r << 16) + (g << 8) + b;
		return new java.awt.Color(rgb);
	}
}
