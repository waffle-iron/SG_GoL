/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.sydsoft.libst.sfl;

import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Sythelux Rikd
 */
public interface StdFileNExtFilter {
    /** Dateinamenserweiterung HTM nach Wikipedia */
    public static FileNameExtensionFilter HTM = new FileNameExtensionFilter("Hypertext Markup Language", "html", "htm");
    /** Dateinamenserweiterung CSV nach Wikipedia */
    public static FileNameExtensionFilter CSV = new FileNameExtensionFilter("Comma separated values", "csv");
    /** Dateinamenserweiterung XLS nach Wikipedia */
    public static FileNameExtensionFilter XLS = new FileNameExtensionFilter("MS Excel Sheet", "xls");
    /** Dateinamenserweiterung JPG nach Wikipedia */
    public static FileNameExtensionFilter JPG = new FileNameExtensionFilter("Joint Photographic Experts Group", "jpg", "jpeg", "jpes");
    /** Dateinamenserweiterung PNG nach Wikipedia */
    public static FileNameExtensionFilter PNG = new FileNameExtensionFilter("Portable Network Graphics", "png");
    /** Dateinamenserweiterung MDB nach Wikipedia */
    public static FileNameExtensionFilter MDB = new FileNameExtensionFilter("Microsoft Data Base", "mdb");
    /** Dateinamenserweiterung GOL nach Sydsoft */
    public static FileNameExtensionFilter GOL = new FileNameExtensionFilter("Game of Life Datein", "gol");
    /** Dateinamenserweiterung SGU nach Sydsoft */    
    public static FileNameExtensionFilter SGU = new FileNameExtensionFilter("SGUniversum Datein", "sgu");
}
