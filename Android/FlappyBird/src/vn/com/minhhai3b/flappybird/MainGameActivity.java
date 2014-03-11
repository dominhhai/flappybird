package vn.com.minhhai3b.flappybird;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;

import vn.com.minhhai3b.flappybird.Entity.Bird;
import vn.com.minhhai3b.flappybird.Entity.Bird.STATE;
import vn.com.minhhai3b.flappybird.Entity.Pipe;
import vn.com.minhhai3b.flappybird.data.GameConfig;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;


/**
 * (c) 2014 Hai Do Minh
 * 
 * @author Hai Do Minh
 */

public class MainGameActivity extends SimpleBaseGameActivity {

	public static final int CAMERA_WIDTH = 288;
	public static final int CAMERA_HEIGHT = 512;
	public static int REAL_HEIGHT = 0;
	
	public static final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0);

	private Camera mCamera = null;
	
	private Map<String, int[]> atlasInfo = null;
	private Texture atlas = null;
	
	private Sprite backgroud = null;
	private PhysicsWorld mPhysicsWorld;
	private Random random = new Random();
	private ArrayList<Pipe> activePipe;
	private boolean destroyWorld = false;

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
		return this.createMenuScene();
	}
	
	public PhysicsWorld getPhysicsWorld() {
		return this.mPhysicsWorld;
	}
	
	public ArrayList<Pipe> getActivePipe() {
		return this.activePipe;
	}
	
	private void switchScene(final Scene scene) {
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
	
	public float[] genPipePosition(float pipeH) {
		float lastPosX = 0;
		for (Pipe pipe : this.activePipe) {
			float pipeX = pipe.getTopSprite().getX();
			if (pipeX > 0 && pipeX > lastPosX) {
				lastPosX = pipeX;
			}
		}
		float[] pos = new float[3]; // px, top, range
		// R = [70, 150]
		// ht = [10, Ht]
		// X = 150
		float lastX = lastPosX == 0 ? CAMERA_WIDTH : lastPosX;
		pos[0] = lastPosX = lastX + random.nextInt(50) + 200;
		pos[1] = random.nextFloat() * (pipeH - 60) + 60;
		pos[2] = random.nextFloat() * (120 - 70) + 70;
		if (pos[1] + pos[2] + pipeH < REAL_HEIGHT) {
			pos[2] = REAL_HEIGHT - (pos[1] + pipeH);
		}
		return pos;
	}
	
	/*
	 * Menu Scene
	 * @return
	 */
	private Scene createMenuScene() {
		Scene scene = new Scene();
		
		int[] bgInfo = this.atlasInfo.get("bg_day");
		TextureRegion backgroudRegion = new TextureRegion(this.atlas, bgInfo[2], bgInfo[3], bgInfo[0], bgInfo[1]);
		this.backgroud = new Sprite(0, 0, backgroudRegion, this.getVertexBufferObjectManager());
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
		final int[] charInfo_0 = this.atlasInfo.get("bird0_0");
		TextureRegion titleRegion = new TextureRegion(this.atlas, titleInfo[2], titleInfo[3], titleInfo[0], titleInfo[1]);
		Sprite title = new Sprite((CAMERA_WIDTH - titleInfo[0] - 5 - charInfo_0[0]) >>> 1, (CAMERA_HEIGHT - titleInfo[1]) >>> 1, titleRegion, this.getVertexBufferObjectManager());				
		Bird bird = new Bird(this, scene, false,2, ((CAMERA_WIDTH + titleInfo[0] - 5 - charInfo_0[0]) >>> 1) + 5, (CAMERA_HEIGHT - charInfo_0[1]) >>> 1);
		title.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(
				new MoveYModifier((float)0.5, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) - 20, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) + 5),
				new MoveYModifier((float)0.5, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) + 5, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) - 20))));
		bird.getBird().registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(
				new MoveYModifier((float)0.6, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) - 30, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) + 10),
				new MoveYModifier((float)0.6, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) + 10, ((CAMERA_HEIGHT - titleInfo[1]) >>> 1) - 30))));		
		scene.attachChild(title);
		
		final int[] btnPlayInfo = this.atlasInfo.get("button_play");
		final int[] btnScoreInfo = this.atlasInfo.get("button_score");
		TextureRegion btnPlayRegion = new TextureRegion(this.atlas, btnPlayInfo[2], btnPlayInfo[3], btnPlayInfo[0], btnPlayInfo[1]);
		TextureRegion btnScoreRegion = new TextureRegion(this.atlas, btnScoreInfo[2], btnScoreInfo[3], btnScoreInfo[0], btnScoreInfo[1]);
		int btnY = CAMERA_HEIGHT - footerY - 20 - btnPlayInfo[1];
		ButtonSprite btnPlay = new ButtonSprite((CAMERA_WIDTH / 2 - btnPlayInfo[0]) / 2, btnY, btnPlayRegion, this.getVertexBufferObjectManager(), new ButtonSprite.OnClickListener() {
			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				MainGameActivity.this.switchScene(MainGameActivity.this.createPlayScene());
			}
		});
		ButtonSprite btnScore = new ButtonSprite(CAMERA_WIDTH * 3 / 4 - btnPlayInfo[0] / 2, btnY, btnScoreRegion, this.getVertexBufferObjectManager(), new ButtonSprite.OnClickListener() {
			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
			}
		});		
		scene.attachChild(btnPlay);
		scene.attachChild(btnScore);
		scene.registerTouchArea(btnPlay);
		scene.registerTouchArea(btnScore);
		
		return scene;
	}
	
	/*
	 * Play Scene
	 */
	private Scene createPlayScene() {
		final Scene scene = new Scene();
		final HUD hud = new HUD();
		this.mCamera.setHUD(hud);
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 20), true);
		this.destroyWorld = false;
		
		int[] bgInfo = this.atlasInfo.get((this.random.nextInt(2) == 0) ? "bg_day" : "bg_night");
		TextureRegion backgroudRegion = new TextureRegion(this.atlas, bgInfo[2], bgInfo[3], bgInfo[0], bgInfo[1]);
		this.backgroud = new Sprite(0, 0, backgroudRegion, this.getVertexBufferObjectManager());
		scene.attachChild(this.backgroud);
		
		int[] footerInfo = this.atlasInfo.get("land");
		TextureRegion footerRegion = new TextureRegion(this.atlas, footerInfo[2], footerInfo[3], footerInfo[0], footerInfo[1]);
		REAL_HEIGHT = CAMERA_HEIGHT - footerInfo[1] * 3 / 4;
		final Sprite footer = new Sprite(0, REAL_HEIGHT, footerRegion, this.getVertexBufferObjectManager());
		hud.attachChild(footer);
		float footerMoveDuration = Math.abs(CAMERA_WIDTH - footerInfo[0]) / GameConfig.VELOCITY;

		footer.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(
				new MoveXModifier(footerMoveDuration, 0, CAMERA_WIDTH - footerInfo[0]))));
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, footer.getY(), CAMERA_WIDTH, 2, vertexBufferObjectManager);		
		
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		
		// character
		final Bird bird = new Bird(this, scene, true, this.random.nextInt(3), CAMERA_WIDTH >>> 1, CAMERA_HEIGHT >>> 1);
		
		if (this.activePipe == null) {
			this.activePipe = new ArrayList<Pipe>();
		} else {
			this.activePipe.clear();
		}
		
		for (int i = 0; i < 3; i ++) {
			float pos[] = genPipePosition(320);
			Pipe pipe = new Pipe(MainGameActivity.this, 0, pos[0], pos[1], pos[2]);
			pipe.attachToScene(scene);
			this.activePipe.add(pipe);
		}
		
		// Event Listener
		this.mPhysicsWorld.setContactListener(new ContactListener() {
			
			@Override
			public void preSolve(Contact contact, Manifold oldManifold) {				
			}
			
			@Override
			public void postSolve(Contact contact, ContactImpulse impulse) {
			}
			
			@Override
			public void endContact(Contact contact) {
			}
			
			@Override
			public void beginContact(Contact contact) {
				final Body b1 = contact.getFixtureA().getBody();
	            final Body b2 = contact.getFixtureB().getBody();
	            final String b1Name = (String) b1.getUserData();
	            final String b2Name = (String) b2.getUserData();
	            if ((b1Name !=null && b1Name.equals(Bird.BIRD)) || (b2Name !=null && b2Name.equals(Bird.BIRD))) {
	            	// pause footer
	            	footer.clearEntityModifiers();
	            	// pause Pipe
	            	for (Pipe pipe : MainGameActivity.this.activePipe) {
						pipe.pause();
					}
	            	// bird die
	            	bird.setState(STATE.DIE, (b1Name == null || b2Name == null));
	            	// physicsworld destroy
	            	MainGameActivity.this.destroyWorld = true;
	            	// scene color
	            }
			}
		});
		scene.registerUpdateHandler(this.mPhysicsWorld);
		
		this.mEngine.runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
				if (MainGameActivity.this.destroyWorld) {
					Iterator<Body> localIterator = mPhysicsWorld.getBodies();
					while (true) {
						if (!localIterator.hasNext()) {
							mPhysicsWorld.clearForces();
							mPhysicsWorld.clearPhysicsConnectors();
							mPhysicsWorld.reset();
							mPhysicsWorld.dispose();
							System.gc();
							break;
						}
						try {
							final Body localBody = (Body) localIterator.next();
							mPhysicsWorld.destroyBody(localBody);
						} catch (Exception localException) {
							localException.printStackTrace();
						}
					}
					MainGameActivity.this.destroyWorld = false;			
				}
			}
		});
		
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				bird.jumpUp();
				return false;
			}
		});
		return scene;
	}
	
	/*
	 * Handle keyboard event
	 * 
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if(keyCode == KeyEvent.KEYCODE_BACK) {
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
	
}
