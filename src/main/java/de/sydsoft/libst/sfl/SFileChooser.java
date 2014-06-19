/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.sydsoft.libst.sfl;

import java.awt.FileDialog;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Sythelux Rikd
 */
public class SFileChooser extends JFileChooser {

    /**
     * Standardkonstruktor
     */
    public SFileChooser() {
        super(new File(System.getProperty("user.dir")));
    }

    /**
     * Schnellzugriffsmethode erstellt einen öffnen oder speichern dialog, und überprüft die dateien auf den übergebenen Filter.
     * @param mode @FileDialog.LOAD für Laden @FileDialog.SAVE für speichern.
     * @param filter Dateiendungsfilter z.B.: für html @StdFileNExtFilter.htm
     * @return die ausgewählte datei wurde keine ausgewählt kommt null zurück.
     */
    public File showSimpleFileDialog(int mode, FileNameExtensionFilter filter) {
        File file = null;
        int returnVal = 0;
        //filter = new FileNameExtensionFilter("EDiS Datein", "eds");
        this.setFileFilter(filter);
        switch (mode) {
            case FileDialog.LOAD:
                returnVal = this.showOpenDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = this.getSelectedFile();
                }
                break;
            case FileDialog.SAVE:
                returnVal = this.showSaveDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = this.getSelectedFile();
                    if (!filter.accept(file)) {
                        file = new File(file.getAbsolutePath() + "." + filter.getExtensions()[0]);
                    }
                }
                break;
        }
        if (file != null) {
            return file;
        } else {
            return null;
        }
    }

    public File showSimpleFolderDialog(int mode) {
        File file = null;
        int returnVal = 0;
        //filter = new FileNameExtensionFilter("EDiS Datein", "eds");
        //this.setFileFilter(filter);
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        switch (mode) {
            case FileDialog.LOAD:
                returnVal = this.showOpenDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = this.getSelectedFile();
                }
                break;
            case FileDialog.SAVE:
                returnVal = this.showSaveDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = this.getSelectedFile();
//                if (!filter.accept(file)){
//                    file = new File(file.getAbsolutePath() +"."+ filter.getExtensions()[0]);
//                }
                }
                break;
        }
        if (file != null) {
            return file;
        } else {
            return null;
        }
    }

    public static SFileChooser getInstance() {
        return new SFileChooser();
    }
}
