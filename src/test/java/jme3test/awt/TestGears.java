package jme3test.awt;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Sample 6 - how to give an object's surface a material and texture. How to
 * make objects transparent. How to make bumpy and shiny surfaces.
 */
public class TestGears extends SimpleApplication {
	Geometry g1;
	Geometry g2;
	Geometry g3;
	Geometry g4;
	Geometry g5;
	
	
	public static void main(String[] args) {
		TestGears app = new TestGears();
		app.start();
	}

	@Override
	public void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
		g1.rotate(0, 0, tpf);
		g2.rotate(0, 0, -tpf);
		g3.rotate(0, 0, tpf*2);
	}

	@Override
	public void simpleInitApp() {
		Box cube2Mesh = new Box(50f,50f, 1);
		g1 = new Geometry("window frame", cube2Mesh);
		g1.setLocalTranslation(50f, 50f, 0);
		Material cube2Mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		cube2Mat.setTexture("ColorMap", assetManager.loadTexture("G6n.png"));
		cube2Mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
		g1.setMaterial(cube2Mat);
		guiNode.attachChild(g1);

		g2 = new Geometry("window frame", cube2Mesh);
		g2.setLocalTranslation(148f, 50f, 0);
		g2.setMaterial(cube2Mat);
		guiNode.attachChild(g2);

		Box cube3Mesh = new Box(25f,25f, 1);
		g3 = new Geometry("window frame", cube3Mesh);
		g3.setLocalTranslation(222f, 50f, 0);
		g3.setMaterial(cube2Mat);
		guiNode.attachChild(g3);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		DirectionalLight sun = new DirectionalLight();
//		sun.setDirection(new Vector3f(1, 0, -2).normalizeLocal());
//		sun.setColor(ColorRGBA.White);
//		guiNode.addLight(sun);

	}
}