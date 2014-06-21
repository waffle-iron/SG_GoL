package de.sydsoft.sg_gol.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.util.TangentBinormalGenerator;

import de.sydsoft.sg_gol.gui.jme3.JME3GameClient;
import de.sydsoft.sg_gol.gui.swing.SwingGameClient;
import de.sydsoft.sg_gol.model.Constants;
import de.sydsoft.sg_gol.model.NormalMapBuilder;
import de.sydsoft.sg_gol.world.AlienWorld;

public class Field extends Geometry {
	public static Geometry createNew(int sizeX, int sizeZ) {
		return createNew(sizeX, sizeZ, Constants.ALIENDEATHCOLOR);
	}

	public static Geometry createNew(int sizeX, int sizeZ, ColorRGBA backgroundcolor) {
//		Box mesh = new Box(sizeX+2*AlienWorld.X_OFFSET, 1f, sizeZ+2*AlienWorld.Z_OFFSET);
		Vector3f v1 = new Vector3f(-sizeX - AlienWorld.X_OFFSET, 0, -sizeZ - AlienWorld.Z_OFFSET);
		
		Box mesh = new Box(v1, new Vector3f(sizeX + AlienWorld.X_OFFSET, 1, sizeZ + AlienWorld.Z_OFFSET));
		Field f = new Field("Field", mesh);
        Texture2D[] texs = drawTexture(sizeX, sizeZ, backgroundcolor);

        TangentBinormalGenerator.generate(f);
        f.setMaterial(createLightingMaterial(texs[0], texs[1],backgroundcolor));

        f.setLocalTranslation(0,-1.25f,0);
        f.setShadowMode(ShadowMode.Receive);
        return f;
	}

	private static Material createLightingMaterial(Texture2D DiffuseMap, Texture2D NormalMap,ColorRGBA backgroundcolor) {
		Material mat = new Material(JME3GameClient.getInstance().getAssetManager(), "Common/MatDefs/Light/Lighting.j3md");

		mat.setTexture("DiffuseMap", DiffuseMap);
		mat.setTexture("NormalMap", NormalMap);

		mat.setBoolean("UseMaterialColors", true);
		mat.setColor("Ambient", ColorRGBA.White);
		mat.setColor("Diffuse", ColorRGBA.White.mult(1.3f));
		mat.setColor("Specular", ColorRGBA.White);
		mat.setFloat("Shininess", 0f); // [0,128]

		return mat;
	}

	private static Material createUnshadedMaterial(Texture2D ColorMap) {
		Material mat = new Material(JME3GameClient.getInstance().getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");

		mat.setTexture("ColorMap", ColorMap);

		return mat;
	}

	private static Texture2D[] drawTexture(int width, int height, ColorRGBA backgroundColor) {
		BufferedImage bimg = new BufferedImage((AlienWorld.X_OFFSET * 20) + width * width, (AlienWorld.Z_OFFSET * 20) + height * height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bimg.getGraphics();
		g.setColor(new Color(backgroundColor.asIntARGB()));
		g.fillRect(0, 0, bimg.getWidth(), bimg.getHeight());

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				g.setColor(Color.BLACK);
				g.drawRect(AlienWorld.X_OFFSET * 10 + (width * x), AlienWorld.Z_OFFSET * 10 + (height * y), width, height);
			}
		}

		BufferedImage bimg2 = new BufferedImage((AlienWorld.Z_OFFSET * 20) + height * height, (AlienWorld.X_OFFSET * 20) + width * width, BufferedImage.TYPE_INT_ARGB);

		AffineTransform at = new AffineTransform();
		at.translate(bimg2.getWidth() / 2, bimg2.getHeight() / 2);
		at.rotate(Math.PI / 2);
		at.translate(-bimg.getWidth() / 2, -bimg.getHeight() / 2);

		((Graphics2D) bimg2.getGraphics()).drawImage(bimg, at, null);
		
		return new Texture2D[] { new Texture2D(new AWTLoader().load(bimg2, false)), new Texture2D(new AWTLoader().load(NormalMapBuilder.build(bimg2), false)) };
	}

	private Field() {
		super();
	}

	private Field(String name) {
		super(name);
	}

	private Field(String name, Mesh mesh) {
		super(name, mesh);
	}
}
