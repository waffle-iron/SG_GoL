package de.sydsoft.sg_gol.gui.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.sydsoft.sg_gol.lang.Localizer;
import de.sydsoft.sg_gol.model.Constants;
import de.sydsoft.sg_gol.model.GoLPattern;

public class GuiAddPattern extends JDialog {
	private static final long	serialVersionUID	= 3178485244215222768L;
	int							scale				= 5;
	int							x, y;
	boolean[][]					pattern;
	String						name;
	boolean ok;

	public GuiAddPattern(int x, int y, int ppA) {
		this.x = x;
		this.y = y;
		pattern = new boolean[x][y];
		scale = ppA;
		setLayout(new BorderLayout());
		final JTextField nameField = new JTextField("<name>");
		JButton okBt = new JButton("ok");
		okBt.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {
				name = nameField.getText();
				ok = true;
				dispose();
			}
		});
		@SuppressWarnings("serial")
		JPanel patternPanel = new JPanel() {
			protected void paintComponent(java.awt.Graphics g) {
				for (int x = 0; x < pattern.length; x++) {
					for (int y = 0; y < pattern[x].length; y++) {
						if (pattern[x][y]) {
							g.setColor(Constants.DEFAULTALIENALIVECOLOR);
						}else {
							g.setColor(Constants.DEFAULTALIENDEATHCOLOR);
						}
						g.fillRect(x*scale, y*scale, scale, scale);
					}
				}
			}
		};
		patternPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int x = e.getX() / scale;
				int y = e.getY() / scale;
				if (x<pattern.length) {
					if (y<pattern[x].length) {
						pattern[x][y] = !pattern[x][y];
						repaint();
					}
				}
			}
		});
		patternPanel.setSize(x*scale, y*scale);
		add(nameField, BorderLayout.NORTH);
		add(patternPanel, BorderLayout.CENTER);
		add(okBt, BorderLayout.SOUTH);
		setSize(patternPanel.getWidth()+nameField.getWidth()+okBt.getWidth(), patternPanel.getHeight()+nameField.getHeight()+okBt.getHeight());
	}

	public static void showDialog() {
		JPanel dimJP = new JPanel();
		dimJP.setLayout(new GridLayout(3, 2));
		JTextField xDimJTF = new JTextField();
		JTextField yDimJTF = new JTextField();
		JTextField ppAJTF = new JTextField("5");
		dimJP.add(new JLabel(Localizer.get("dialog.newX")));
		dimJP.add(xDimJTF);
		dimJP.add(new JLabel(Localizer.get("dialog.newY")));
		dimJP.add(yDimJTF);
		dimJP.add(new JLabel(Localizer.get("Menu.Option.PpA")));
		dimJP.add(ppAJTF);
		int result = JOptionPane.showConfirmDialog(GuiGameOfLife.getInstance(), dimJP, Localizer.get("dialog.dim"), JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.CANCEL_OPTION) return;
		if (!(xDimJTF.getText().matches("[0-9]*") && xDimJTF.getText().matches("[0-9]*") && ppAJTF.getText().matches("[0-9]*"))) return;

		int x = Integer.parseInt(xDimJTF.getText());
		int y = Integer.parseInt(xDimJTF.getText());
		int ppA = Integer.parseInt(ppAJTF.getText());

		GuiAddPattern addPat = new GuiAddPattern(x, y, ppA);
		addPat.setModal(true);
		addPat.setVisible(true);
		
		if(addPat.ok)
			GoLPattern.addPattern(addPat.name, GuiGameOfLife.getInstance().getPatternBar(), addPat.pattern);
	}

}
