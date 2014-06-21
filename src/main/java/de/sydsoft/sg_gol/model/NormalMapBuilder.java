package de.sydsoft.sg_gol.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jme3.math.Vector3f;

public class NormalMapBuilder {
	public static BufferedImage build(BufferedImage biInput) {
		BufferedImage biOutput = new BufferedImage(biInput.getWidth(), biInput.getHeight(), biInput.getType());
		BufferedImage heightmapImage = new BufferedImage(biInput.getWidth(), biInput.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		Graphics gr = heightmapImage.getGraphics();
		gr.drawImage(biInput, 0, 0, null);
		gr.dispose();
		for (int y = 1; y < heightmapImage.getHeight() - 1; y++) {
			for (int x = 1; x < heightmapImage.getWidth() - 1; x++) {
				Vector3f vx = new Vector3f(0, 1, heightmapImage.getRGB(x + 1, y));
				Vector3f vy = new Vector3f(1, 0, heightmapImage.getRGB(x, y + 1));
				Vector3f normal = vx.cross(vy).normalize();
				int r = (int) ((normal.x + 1f) * 255f);
				int g = (int) ((normal.y + 1f) * 255f);
				int b = (int) ((normal.z + 1f) * 255f);
//				System.out.printf("Color:%d,%d,%d\n",r,g,b);
				Color c = new Color(r, g, b);
				biOutput.setRGB(x, y, c.getRGB());
			}
		}
		gr = biOutput.getGraphics();
		gr.setColor(new Color(biOutput.getRGB(1, 1)));
		gr.drawRect(0, 0, biOutput.getWidth(), biOutput.getHeight());
		gr.dispose();
//		try {
//			ImageIO.write(biOutput, "png", new File("E:\\sythelux\\Bilder\\of me\\normalmap.png"));
//			ImageIO.write(heightmapImage, "png", new File("E:\\sythelux\\Bilder\\of me\\heightmap.png"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return biOutput;
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("specify an input picture.");
			return;
		} else if (!new File(args[0]).exists()) {
			System.out.println("File: " + args[0] + " could not be found.");
			return;
		}
		BufferedImage biInput = null;
		String extension = "";
		try {
			extension = args[0].substring(args[0].lastIndexOf('.') + 1);
			biInput = ImageIO.read(new File(args[0]));
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ImageIO.write(build(biInput), extension, new File(args[0].replace("." + extension, "_n." + extension)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
