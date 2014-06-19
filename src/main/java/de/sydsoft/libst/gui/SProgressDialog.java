package de.sydsoft.libst.gui;

import javax.swing.JPanel;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import java.awt.GridBagLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Scanner;

import javax.swing.JLabel;

/**
 * 
 * @author syd
 */
public class SProgressDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private JPanel jContentPane = null;
    private JProgressBar saveProgress = null;
    private JPanel msgPn = null;
    private JPanel botPn = null;
    private JLabel msgLb = null;

    /**
     * @param owner
     */
    public SProgressDialog(JFrame owner) {
        super(owner);
        initialize();
    }

    /**
     * 
     * @param owner
     * @param title
     * @param msg
     */
    public SProgressDialog(JFrame owner, String title, String msg) {
        super(owner);
        setTitle(title);
        msgLb = new JLabel(msg);
        initialize();
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(300, 200);
        this.setContentPane(getJContentPane());
        this.setLocationRelativeTo(null);
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getSaveProgress(), BorderLayout.CENTER);
            jContentPane.add(getMsgPn(), BorderLayout.NORTH);
            jContentPane.add(getBotPn(), BorderLayout.SOUTH);
        }
        return jContentPane;
    }

    /**
     * This method initializes saveProgress	
     * 	
     * @return javax.swing.JProgressBar	
     */
    public JProgressBar getSaveProgress() {
        if (saveProgress == null) {
            saveProgress = new JProgressBar(0, 100);
        }
        return saveProgress;
    }

    /**
     * This method initializes msgPn	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getMsgPn() {
        if (msgPn == null) {
            if (msgLb == null) {
                msgLb = new JLabel();
            }
            msgPn = new JPanel();
            msgPn.setLayout(new FlowLayout());
            msgPn.setPreferredSize(new Dimension(0, 50));
            msgPn.add(msgLb, null);
        }
        return msgPn;
    }

    /**
     * This method initializes botPn	
     * 	
     * @return javax.swing.JPanel	
     */
    private JPanel getBotPn() {
        if (botPn == null) {
            botPn = new JPanel();
            botPn.setLayout(new GridBagLayout());
            botPn.setPreferredSize(new Dimension(0, 75));
        }
        return botPn;
    }
}
