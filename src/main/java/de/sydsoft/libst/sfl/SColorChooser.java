package de.sydsoft.libst.sfl;

import javax.swing.*;
import java.awt.*;

public class SColorChooser extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public SColorChooser() {
        super("ColorChooser");
        JColorChooser cc = new JColorChooser(Color.yellow);

        JPanel panel = new JPanel();
        panel.add(cc);
        setContentPane(panel);
    }

    public static void main(String[] args) {
        SColorChooser mcc = new SColorChooser();
        mcc.setSize(500, 400);
        mcc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mcc.setVisible(true);
    }
}
