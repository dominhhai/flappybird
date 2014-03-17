package vn.com.minhhai3b.flappybird.scene;

import java.util.Map;

import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.view.KeyEvent;
import vn.com.minhhai3b.flappybird.MainGameActivity;

public abstract class GScene {

	protected MainGameActivity mActivity;
	protected Map<String, int[]> atlasInfo;
	protected Texture atlas;
	
	protected Scene scene;
	protected int CAMERA_WIDTH;
	protected int CAMERA_HEIGHT;
	protected VertexBufferObjectManager vertexBufferObjectManager;
	
	protected Object userData;
	
	public GScene(final MainGameActivity pActivity) {
		this.mActivity = pActivity;
		this.atlasInfo = pActivity.getAtlasInfo();
		this.atlas = pActivity.getAtlas();
		
		this.scene = new Scene();
		this.CAMERA_WIDTH = MainGameActivity.CAMERA_WIDTH;
		this.CAMERA_HEIGHT = MainGameActivity.CAMERA_HEIGHT;
		vertexBufferObjectManager = this.mActivity.getVertexBufferObjectManager();
		this.loadScene();
	}
	
	public Scene getScene() {
		return this.scene;
	}
	
	public VertexBufferObjectManager getVertexBufferObjectManager() {
		return this.vertexBufferObjectManager;
	}
	
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	public Object getUserData() {
		return this.userData;
	}
	
	protected abstract void loadScene();
	
	protected abstract boolean onKeyDown(int keyCode, KeyEvent event);

}
