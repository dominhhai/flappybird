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
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;

public class MainGameActivity extends SimpleBaseGameActivity {

	private static final int CAMERA_WIDTH = 288;
	private static final int CAMERA_HEIGHT = 512;

	private Camera mCamera = null;
	
	private Map<String, int[]> atlasInfo = null;
	private Texture atlas = null;
	
	private TextureRegion backgroudRegion = null;
	
	
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
		
		int[] bgInfo = this.atlasInfo.get("bg_day");
		this.backgroudRegion = new TextureRegion(this.atlas, bgInfo[2], bgInfo[3], bgInfo[0], bgInfo[1]);		
	}

	@Override
	protected Scene onCreateScene() {
		Scene scene = new Scene();
		scene.setBackground(new Background(Color.BLACK));
		Sprite backgroud = new Sprite(0, 0, this.backgroudRegion, this.getVertexBufferObjectManager());
		scene.attachChild(backgroud);
		
		int[] footerInfo = this.atlasInfo.get("land");
		TextureRegion footerRegion = new TextureRegion(this.atlas, footerInfo[2], footerInfo[3], footerInfo[0], footerInfo[1]);
		int footerY = footerInfo[1] * 3 / 4; 
		Sprite footer = new Sprite(0, CAMERA_HEIGHT - footerY, footerRegion, this.getVertexBufferObjectManager());
		scene.attachChild(footer);
		int[] copyInfo = this.atlasInfo.get("brand_copyright");
		TextureRegion copyRegion = new TextureRegion(this.atlas, copyInfo[2], copyInfo[3], copyInfo[0], copyInfo[1]);
		Sprite copy = new Sprite((CAMERA_WIDTH - copyInfo[0]) >>> 1, CAMERA_HEIGHT - (footerY >>> 1), copyRegion, this.getVertexBufferObjectManager());
		scene.attachChild(copy);
		
		final int[] titleInfo = this.atlasInfo.get("title");
		final int[] charInfo = this.atlasInfo.get("bird0_0");
		TextureRegion titleRegion = new TextureRegion(this.atlas, titleInfo[2], titleInfo[3], titleInfo[0], titleInfo[1]);
		Sprite title = new Sprite((CAMERA_WIDTH - titleInfo[0] - 5 - charInfo[0]) >>> 1, (CAMERA_HEIGHT - titleInfo[1]) >>> 1, titleRegion, this.getVertexBufferObjectManager());				
		TextureRegion charRegion = new TextureRegion(this.atlas, charInfo[2], charInfo[3], charInfo[0], charInfo[1]);
		Sprite character = new Sprite(((CAMERA_WIDTH + titleInfo[0] - 5 - charInfo[0]) >>> 1) + 5, (CAMERA_HEIGHT - charInfo[1]) >>> 1, charRegion, this.getVertexBufferObjectManager());
		title.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(
				new MoveYModifier((float)0.6, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) - 30, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) + 10),
				new MoveYModifier((float)0.6, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) + 10, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) - 30))));
		character.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(
				new MoveYModifier((float)0.6, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) - 30, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) + 10),
				new MoveYModifier((float)0.6, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) + 10, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) - 30))));
		scene.attachChild(title);
		scene.attachChild(character);
		
		return scene;
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

}
