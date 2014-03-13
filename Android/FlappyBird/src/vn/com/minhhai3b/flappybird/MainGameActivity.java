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
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.modifier.AlphaModifier;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.ScaleModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.preferences.SimplePreferences;

import vn.com.minhhai3b.flappybird.Entity.Bird;
import vn.com.minhhai3b.flappybird.Entity.Bird.STATE;
import vn.com.minhhai3b.flappybird.Entity.Pipe;
import vn.com.minhhai3b.flappybird.Entity.ScoreSprite;
import vn.com.minhhai3b.flappybird.data.GameConfig;
import android.content.SharedPreferences;
import android.view.KeyEvent;

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
	
	public static final String SCENE_MENU = "SCENE_MENU";
	public static final String SCENE_PLAY = "SCENE_PLAY";
	public static final String SCENE_SCORE = "SCENE_SCORE";
	
	public static final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0);

	private Camera mCamera = null;
	
	private Map<String, int[]> atlasInfo = null;
	private Texture atlas = null;
	
	private Sprite backgroud = null;
	private PhysicsWorld mPhysicsWorld;
	private Random random = new Random();
	private ArrayList<Pipe> activePipe;
	private boolean destroyWorld = false;
	private int score = 0;
	private ScoreSprite contextScore;

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
	
	/*
	 * Menu Scene
	 * @return
	 */
	private Scene createMenuScene() {
		Scene scene = new Scene();		
		scene.setUserData(SCENE_MENU);
		
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
		float titleY = (CAMERA_HEIGHT - titleInfo[1]) / 2;
		Sprite title = new Sprite((CAMERA_WIDTH - titleInfo[0] - 5 - charInfo_0[0]) / 2, titleY, titleRegion, this.getVertexBufferObjectManager());						
		title.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new MoveYModifier(0.5f, titleY - 20, titleY + 5), new MoveYModifier(0.5f, titleY + 5, titleY - 20))));
		scene.attachChild(title);
		new Bird(this, scene, false,2, ((CAMERA_WIDTH + titleInfo[0] - 5 - charInfo_0[0]) >>> 1) + 5, (CAMERA_HEIGHT - charInfo_0[1]) >>> 1);
		
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
		if (lastPosX == 0) {
			lastPosX = CAMERA_WIDTH;
		}
		pos[0] = lastPosX + random.nextInt(50) + 200;
		pos[1] = random.nextFloat() * (pipeH - 60) + 60;
		pos[2] = random.nextFloat() * (120 - 55) + 55;
		if (pos[1] + pos[2] + pipeH < REAL_HEIGHT) {
			pos[2] = REAL_HEIGHT - (pos[1] + pipeH);
		}
		return pos;
	}
	
	private TiledTextureRegion loadScoreTextureRegions(boolean isContext) {
		String resource = !isContext ? "number_context_0" : "number_score_0";
		TextureRegion[] contextScoreRegions = new TextureRegion[10];
		for (int i = 0; i < 10; i ++) {
			int[] textureInfo = atlasInfo.get(resource + i);
			contextScoreRegions[i] = new TextureRegion(atlas, textureInfo[2], textureInfo[3], textureInfo[0], textureInfo[1]);
		}
		TiledTextureRegion tiledTextureRegion = new TiledTextureRegion(atlas, contextScoreRegions);
		return tiledTextureRegion;
	}
	
	public void increateScore() {
		this.score ++;
		this.contextScore.setScore(score);
	}
	 
	private void gameOverEffect(final Scene scene) {
		final Rectangle dieEffect = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, this.getVertexBufferObjectManager());
    	dieEffect.setAlpha(0);
		dieEffect.registerEntityModifier(new SequenceEntityModifier(
				new AlphaModifier(0.25f, dieEffect.getAlpha(), 1)
				, new AlphaModifier(0.2f, dieEffect.getAlpha(), 0)
				));
		scene.attachChild(dieEffect);
		int[] scorePanelInfo = this.atlasInfo.get("score_panel");
		int[] gameOverTextInfo = this.atlasInfo.get("text_game_over");
		float scorePanelY = CAMERA_HEIGHT / 2 - scorePanelInfo[1] / 4;
		float gameOverTextY = scorePanelY - gameOverTextInfo[1] - 30;
		TextureRegion gameOverTextRegion = new TextureRegion(this.atlas, gameOverTextInfo[2], gameOverTextInfo[3], gameOverTextInfo[0], gameOverTextInfo[1]);
		TextureRegion scorePanelRegion = new TextureRegion(this.atlas, scorePanelInfo[2], scorePanelInfo[3], scorePanelInfo[0], scorePanelInfo[1]);

		final Sprite gameOverText = new Sprite(
				(CAMERA_WIDTH - gameOverTextInfo[0]) / 2, gameOverTextY,
				gameOverTextRegion, this.getVertexBufferObjectManager());
		final Sprite scorePanel = new Sprite((CAMERA_WIDTH - scorePanelInfo[0]) / 2, CAMERA_HEIGHT, scorePanelRegion, this.getVertexBufferObjectManager());
		gameOverText.registerEntityModifier(new ScaleModifier(0.5f, 0, 1));
		scorePanel.registerEntityModifier(new MoveYModifier(0.8f, scorePanel.getY(), scorePanelY));
		// score and medal		
		// load highest score
		int highest = SimplePreferences.getInstance(getApplicationContext()).getInt("FB_SCORE", 0);		
		if (score > 10) {
			int medalIndex = -1;
			if (score > highest) {
				medalIndex = 1;
			} else if (score + 10 >= highest) {
				medalIndex = 0;
			}			
			if (medalIndex > -1) {
				int[] medalInfo_0  = this.atlasInfo.get("medals_" + medalIndex);
				int[] medalInfo_1 = this.atlasInfo.get("medals_" + (medalIndex + 2));
				TiledTextureRegion medalRegion = new TiledTextureRegion(atlas, 
						new TextureRegion(atlas, medalInfo_0[2], medalInfo_0[3], medalInfo_0[0], medalInfo_0[1]), 
						new TextureRegion(atlas, medalInfo_1[2], medalInfo_1[3], medalInfo_1[0], medalInfo_1[1]));
				AnimatedSprite medal = new AnimatedSprite(31, 46, medalRegion, this.getVertexBufferObjectManager());
				medal.animate(150);	
				scorePanel.attachChild(medal);
			}
		}
		if (score > highest) {
			highest = score;
			// save highest score
			SimplePreferences.getEditorInstance(getApplicationContext()).putInt("FB_SCORE", highest).commit();
			// show new label
			int[] newInfo = this.atlasInfo.get("new");
			Sprite newSpr = new Sprite(140, 60, new TextureRegion(atlas, newInfo[2], newInfo[3], newInfo[0], newInfo[1]), this.getVertexBufferObjectManager());
			scorePanel.attachChild(newSpr);			
		}		
		float scoreX = scorePanel.getWidth() - 30;
		ScoreSprite userScore = new ScoreSprite(this, scorePanel, scoreX, 36, 1, this.loadScoreTextureRegions(true));
		userScore.animate(score);
		ScoreSprite highestScore = new ScoreSprite(this, scorePanel, scoreX, 80, 1, this.loadScoreTextureRegions(true));
		highestScore.setScore(highest);
		// attach to scene
		scene.attachChild(gameOverText);
		scene.attachChild(scorePanel);
		
		final int[] btnPlayInfo = this.atlasInfo.get("button_play");
		final int[] btnScoreInfo = this.atlasInfo.get("button_score");
		TextureRegion btnPlayRegion = new TextureRegion(this.atlas, btnPlayInfo[2], btnPlayInfo[3], btnPlayInfo[0], btnPlayInfo[1]);
		TextureRegion btnScoreRegion = new TextureRegion(this.atlas, btnScoreInfo[2], btnScoreInfo[3], btnScoreInfo[0], btnScoreInfo[1]);
		float btnY = scorePanelY + scorePanelInfo[1] + 10;
		ButtonSprite btnPlay = new ButtonSprite((CAMERA_WIDTH / 2 - btnPlayInfo[0]) / 2, CAMERA_HEIGHT, btnPlayRegion, this.getVertexBufferObjectManager(), new ButtonSprite.OnClickListener() {
			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				MainGameActivity.this.switchScene(MainGameActivity.this.createPlayScene());
			}
		});
		ButtonSprite btnScore = new ButtonSprite(CAMERA_WIDTH * 3 / 4 - btnPlayInfo[0] / 2, CAMERA_HEIGHT, btnScoreRegion, this.getVertexBufferObjectManager(), new ButtonSprite.OnClickListener() {
			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
			}
		});
		btnPlay.registerEntityModifier(new MoveYModifier(0.9f, btnPlay.getY(), btnY));
		btnScore.registerEntityModifier(new MoveYModifier(0.9f, btnPlay.getY(), btnY));
		scene.attachChild(btnPlay);
		scene.attachChild(btnScore);
		scene.registerTouchArea(btnPlay);
	}
	
	/*
	 * Play Scene
	 */
	private Scene createPlayScene() {
		final Scene scene = new Scene();
		scene.setUserData(SCENE_PLAY);
		final HUD hud = new HUD();
		this.mCamera.setHUD(hud);
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 20), true);
		this.destroyWorld = false;
		this.score = 0;
		
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
		final LoopEntityModifier footerModifier = new LoopEntityModifier(new SequenceEntityModifier(
				new MoveXModifier(footerMoveDuration, 0, CAMERA_WIDTH - footerInfo[0])));
		footer.registerEntityModifier(footerModifier);
		
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, footer.getY(), CAMERA_WIDTH, 2, vertexBufferObjectManager);		
		
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		
		// character
		final Bird bird = new Bird(this, scene, true, this.random.nextInt(3));		
		
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
		
		// score
		this.contextScore = new ScoreSprite(this, scene, CAMERA_WIDTH / 2, 50, 0, this.loadScoreTextureRegions(true));
		this.contextScore.setScore(0);
		// tutorial
		int[] readyTextInfo = this.atlasInfo.get("text_ready");
		TextureRegion readyTextRegion = new TextureRegion(this.atlas, readyTextInfo[2], readyTextInfo[3], readyTextInfo[0], readyTextInfo[1]);
		int[] tutorialInfo = this.atlasInfo.get("tutorial");
		TextureRegion tutorialRegion = new TextureRegion(this.atlas, tutorialInfo[2], tutorialInfo[3], tutorialInfo[0], tutorialInfo[1]);
		float tutorialY = (CAMERA_HEIGHT - tutorialInfo[1]) / 2;
		float readyTextY = tutorialY - readyTextInfo[1] - 10;
		final Sprite readyText = new Sprite((CAMERA_WIDTH - readyTextInfo[0]) / 2, readyTextY, readyTextRegion, this.getVertexBufferObjectManager());
		final Sprite tutorial = new Sprite((CAMERA_WIDTH - tutorialInfo[0]) / 2, tutorialY, tutorialRegion, this.getVertexBufferObjectManager());
		scene.attachChild(readyText);
		scene.attachChild(tutorial);
		// pause/ resume
		final int[] pauseInfo = atlasInfo.get("button_pause");
		final int[] resumeInfo = atlasInfo.get("button_resume");
		TextureRegion pauseRegion = new TextureRegion(atlas, pauseInfo[2], pauseInfo[3], pauseInfo[0], pauseInfo[1]);
		TextureRegion resumeRegion = new TextureRegion(atlas, resumeInfo[2], resumeInfo[3], resumeInfo[0], resumeInfo[1]);
		TiledTextureRegion charRegion = new TiledTextureRegion(atlas, pauseRegion, resumeRegion);
		final TiledSprite pauseResumeBtn = new TiledSprite(10, 10, charRegion, this.getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN && bird.getState() != STATE.NOT_MOVE && bird.getState() != STATE.DIE) {
					if (this.getCurrentTileIndex() == 0) { // pause
						for (Pipe pipe : MainGameActivity.this.activePipe) {	
							pipe.pause();
						}
						bird.pause();
						footer.unregisterEntityModifier(footerModifier);
						this.setCurrentTileIndex(1);
					} else { // resume
						bird.resume();
						for (Pipe pipe : MainGameActivity.this.activePipe) {	
							pipe.resume();
						}
						footer.registerEntityModifier(footerModifier);
						this.setCurrentTileIndex(0);
					}
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}
		};		
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
	            	boolean groundCollision = b1Name == null || b2Name == null;
	            	if (bird.getState() == STATE.DIE) {
	            		if (groundCollision) {
		            		bird.pause();
		            	}
		            	return;
	            	}
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
	            	MainGameActivity.this.gameOverEffect(scene);
	            	scene.detachChild(pauseResumeBtn);
	        		scene.unregisterTouchArea(pauseResumeBtn);
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
					mEngine.getScene().unregisterUpdateHandler(mPhysicsWorld);
					MainGameActivity.this.destroyWorld = false;
				}
			}
		});
		
		scene.setOnSceneTouchListener(new IOnSceneTouchListener() {
			
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
				if (bird.getState() == STATE.NOT_MOVE) {
					readyText.detachSelf();
					tutorial.detachSelf();
					
					bird.setState(STATE.DOWN, false);
					for (Pipe pipe : MainGameActivity.this.activePipe) {
						pipe.action();
					}
					
					scene.attachChild(pauseResumeBtn);
					scene.registerTouchArea(pauseResumeBtn);
				}
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
