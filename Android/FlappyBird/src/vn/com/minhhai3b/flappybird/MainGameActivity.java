package vn.com.minhhai3b.flappybird;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import vn.com.minhhai3b.flappybird.data.GameConfig;
import vn.com.minhhai3b.flappybird.scene.MainMenuScene;
import android.view.KeyEvent;


/**
 * (c) 2014 Hai Do Minh
 * 
 * @author Hai Do Minh
 */

public class MainGameActivity extends SimpleBaseGameActivity {

	public static final int CAMERA_WIDTH = 288;
	public static final int CAMERA_HEIGHT = 512;	

	private Camera mCamera = null;
	
	private Map<String, int[]> atlasInfo = null;
	private Texture atlas = null;
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions mEngineOptions =  new EngineOptions(true, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), this.mCamera);
		mEngineOptions.getRenderOptions().setMultiSampling(true);
		mEngineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		return mEngineOptions;
	}

	@Override
	protected void onCreateResources() {
		this.atlasInfo = this.decodeAtlasInfo();
		this.atlas = this.decodeAtlasTexture();
		this.atlas.load();
		GameConfig.getInstance().setAtlas(this.atlas, this.atlasInfo);
	}

	@Override
	protected Scene onCreateScene() {
		return (new MainMenuScene(this)).getScene();
	}
	
	public Camera getCamera() {
		return this.mCamera;
	}	
	
	public void switchScene(final Scene scene) {
		if(this.mEngine.getScene() != scene) {			
			this.mEngine.getScene().postRunnable(new Runnable(){
				
				@Override
				public void run() {
					MainGameActivity.this.clearCurrentScene();
					MainGameActivity.this.mEngine.setScene(scene);
				}
				
			});
		}
	}
	
	private void clearCurrentScene() {
		Scene scene =this.mEngine.getScene(); 
		scene.clearUpdateHandlers();
		scene.clearTouchAreas();
		scene.clearEntityModifiers();
		scene.clearChildScene();
		scene.detachChildren();
		scene.dispose();
	}
	
	private Texture decodeAtlasTexture() {
		BitmapTexture bmTexture = null;
		// texture load, unload
		try {
			bmTexture = new BitmapTexture(this.getTextureManager(), new IInputStreamOpener() {				
				@Override
				public InputStream open() throws IOException {					
					return MainGameActivity.this.getAssets().open("gfx/atlas.png");
				}
			}, TextureOptions.NEAREST_PREMULTIPLYALPHA);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmTexture;
	}
	
	private Map<String, int[]> decodeAtlasInfo() {
		/*
		 *atlatInfo:
		 *	+ key: String
		 *	+ value: int[w, h, x, y] 
		 */
		Map<String, int[]> atlasInfo = new HashMap<String, int[]>();
		
		InputStream ips = null;
		InputStreamReader ipsReader = null;
		BufferedReader buffReader = null;
		try {
			ips = this.getAssets().open("gfx/atlas.txt");
			ipsReader = new InputStreamReader(ips);
			buffReader = new BufferedReader(ipsReader);
			String txtLine = buffReader.readLine();
			while (txtLine != null) {
				// handle text line
				String[] lineInfo = txtLine.split(" ");
				atlasInfo.put(lineInfo[0], new int[] {Integer.valueOf(lineInfo[1]), Integer.valueOf(lineInfo[2]), Integer.valueOf(lineInfo[3]), Integer.valueOf(lineInfo[4])});
				// continue read buffer
				txtLine = buffReader.readLine();				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally { // release buffer
			try {
				if (ips != null) {
					ips.close();
				}
				if (ipsReader != null) {
					ipsReader.close();
				}
				if (buffReader != null) {
					buffReader.close();
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		
		return atlasInfo;
	}
	
	public Map<String, int[]> getAtlasInfo() {
		return this.atlasInfo;
	}
	
	public Texture getAtlas() {
		return this.atlas;
	}

	
	/*
	 * Handle keyboard event
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
//			String curScene = (String) mEngine.getScene().getUserData();
//			if (curScene.equals(SCENE_MENU)) {
//				return true;
//			} else if (curScene.equals(SCENE_PLAY)) {
//				
//			} else {
//			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
