package vn.com.minhhai3b.flappybird.scene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.andengine.engine.camera.hud.HUD;
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
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.preferences.SimplePreferences;

import vn.com.minhhai3b.flappybird.MainGameActivity;
import vn.com.minhhai3b.flappybird.Entity.Bird;
import vn.com.minhhai3b.flappybird.Entity.Bird.STATE;
import vn.com.minhhai3b.flappybird.Entity.Pipe;
import vn.com.minhhai3b.flappybird.Entity.ScoreSprite;
import vn.com.minhhai3b.flappybird.data.GameConfig;
import android.view.KeyEvent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;

public class PlayScene extends GScene implements IOnSceneTouchListener, ContactListener {
	
	public static final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0);
	public static int REAL_HEIGHT = 0;
	
	private HUD hud;
	private Sprite backgroud = null;
	private PhysicsWorld mPhysicsWorld;
	private Random random;
	private ArrayList<Pipe> activePipe;
	private int score = 0;
	private ScoreSprite contextScore;
	private Sprite footer;
	private Bird bird;
	private Sprite readyText;
	private Sprite tutorial;
	private TiledSprite pauseResumeBtn;
	private LoopEntityModifier footerModifier;

	public PlayScene(final MainGameActivity pActivity) {
		super(pActivity);
	}

	public PhysicsWorld getPhysicsWorld() {
		return this.mPhysicsWorld;
	}
	
	public ArrayList<Pipe> getActivePipe() {
		return this.activePipe;
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
	
	private void destroyPhysicsWorld() {
		mActivity.getEngine().runOnUpdateThread(new Runnable() {
			
			@Override
			public void run() {
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
				scene.unregisterUpdateHandler(mPhysicsWorld);
			}
		});
	}
	
	private void handleGameOver() {
		final Rectangle dieEffect = new Rectangle(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, vertexBufferObjectManager);
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
				gameOverTextRegion, vertexBufferObjectManager);
		final Sprite scorePanel = new Sprite((CAMERA_WIDTH - scorePanelInfo[0]) / 2, CAMERA_HEIGHT, scorePanelRegion, vertexBufferObjectManager);
		gameOverText.registerEntityModifier(new ScaleModifier(0.5f, 0, 1));
		scorePanel.registerEntityModifier(new MoveYModifier(0.8f, scorePanel.getY(), scorePanelY));
		// score and medal		
		// load highest score
		int highest = SimplePreferences.getInstance(mActivity.getApplicationContext()).getInt("FB_SCORE", 0);		
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
				AnimatedSprite medal = new AnimatedSprite(31, 46, medalRegion, vertexBufferObjectManager);
				medal.animate(150);	
				scorePanel.attachChild(medal);
			}
		}
		if (score > highest) {
			highest = score;
			// save highest score
			SimplePreferences.getEditorInstance(mActivity.getApplicationContext()).putInt("FB_SCORE", highest).commit();
			// show new label
			int[] newInfo = this.atlasInfo.get("new");
			Sprite newSpr = new Sprite(140, 60, new TextureRegion(atlas, newInfo[2], newInfo[3], newInfo[0], newInfo[1]), vertexBufferObjectManager);
			scorePanel.attachChild(newSpr);			
		}		
		float scoreX = scorePanel.getWidth() - 30;
		ScoreSprite userScore = new ScoreSprite(mActivity, scorePanel, scoreX, 36, 1, this.loadScoreTextureRegions(true));
		userScore.animate(score);
		ScoreSprite highestScore = new ScoreSprite(mActivity, scorePanel, scoreX, 80, 1, this.loadScoreTextureRegions(true));
		highestScore.setScore(highest);
		// attach to scene
		scene.attachChild(gameOverText);
		scene.attachChild(scorePanel);
		
		final int[] btnPlayInfo = this.atlasInfo.get("button_play");
		final int[] btnScoreInfo = this.atlasInfo.get("button_score");
		TextureRegion btnPlayRegion = new TextureRegion(this.atlas, btnPlayInfo[2], btnPlayInfo[3], btnPlayInfo[0], btnPlayInfo[1]);
		TextureRegion btnScoreRegion = new TextureRegion(this.atlas, btnScoreInfo[2], btnScoreInfo[3], btnScoreInfo[0], btnScoreInfo[1]);
		float btnY = scorePanelY + scorePanelInfo[1] + 10;
		ButtonSprite btnPlay = new ButtonSprite((CAMERA_WIDTH / 2 - btnPlayInfo[0]) / 2, CAMERA_HEIGHT, btnPlayRegion, vertexBufferObjectManager, new ButtonSprite.OnClickListener() {
			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				mActivity.switchScene((new PlayScene(mActivity)).getScene());
			}
		});
		ButtonSprite btnScore = new ButtonSprite(CAMERA_WIDTH * 3 / 4 - btnPlayInfo[0] / 2, CAMERA_HEIGHT, btnScoreRegion, vertexBufferObjectManager, new ButtonSprite.OnClickListener() {
			
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
		
		this.destroyPhysicsWorld();
	}
	
	@Override
	protected void loadScene() {
		hud = new HUD();
		mActivity.getCamera().setHUD(hud);
		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 20), true);
		this.score = 0;
		this.random = new Random();
		
		VertexBufferObjectManager vbm = this.mActivity.getVertexBufferObjectManager();
		int[] bgInfo = this.atlasInfo.get((this.random.nextInt(2) == 0) ? "bg_day" : "bg_night");
		TextureRegion backgroudRegion = new TextureRegion(this.atlas, bgInfo[2], bgInfo[3], bgInfo[0], bgInfo[1]);
		this.backgroud = new Sprite(0, 0, backgroudRegion, vbm);
		scene.attachChild(this.backgroud);
		
		int[] footerInfo = this.atlasInfo.get("land");
		TextureRegion footerRegion = new TextureRegion(this.atlas, footerInfo[2], footerInfo[3], footerInfo[0], footerInfo[1]);
		REAL_HEIGHT = CAMERA_HEIGHT - footerInfo[1] * 3 / 4;
		this.footer = new Sprite(0, REAL_HEIGHT, footerRegion, vbm);
		hud.attachChild(footer);
		float footerMoveDuration = Math.abs(CAMERA_WIDTH - footerInfo[0]) / GameConfig.VELOCITY;
		footerModifier = new LoopEntityModifier(new SequenceEntityModifier(
				new MoveXModifier(footerMoveDuration, 0, CAMERA_WIDTH - footerInfo[0])));
		footer.registerEntityModifier(footerModifier);
		final Rectangle ground = new Rectangle(0, footer.getY(), CAMERA_WIDTH, 2, vbm);		
		
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		
		// character
		bird = new Bird(this, true, this.random.nextInt(3));
		
		if (this.activePipe == null) {
			this.activePipe = new ArrayList<Pipe>();
		} else {
			this.activePipe.clear();
		}
		
		for (int i = 0; i < 3; i ++) {
			float pos[] = genPipePosition(320);
			Pipe pipe = new Pipe(this, 0, pos[0], pos[1], pos[2]);
			pipe.attachToScene(scene);
			this.activePipe.add(pipe);
		}
		
		// score
		this.contextScore = new ScoreSprite(mActivity, scene, CAMERA_WIDTH / 2, 50, 0, this.loadScoreTextureRegions(true));
		this.contextScore.setScore(0);
		// tutorial
		int[] readyTextInfo = this.atlasInfo.get("text_ready");
		TextureRegion readyTextRegion = new TextureRegion(this.atlas, readyTextInfo[2], readyTextInfo[3], readyTextInfo[0], readyTextInfo[1]);
		int[] tutorialInfo = this.atlasInfo.get("tutorial");
		TextureRegion tutorialRegion = new TextureRegion(this.atlas, tutorialInfo[2], tutorialInfo[3], tutorialInfo[0], tutorialInfo[1]);
		float tutorialY = (CAMERA_HEIGHT - tutorialInfo[1]) / 2;
		float readyTextY = tutorialY - readyTextInfo[1] - 10;
		readyText = new Sprite((CAMERA_WIDTH - readyTextInfo[0]) / 2, readyTextY, readyTextRegion, vertexBufferObjectManager);
		tutorial = new Sprite((CAMERA_WIDTH - tutorialInfo[0]) / 2, tutorialY, tutorialRegion, vertexBufferObjectManager);
		scene.attachChild(readyText);
		scene.attachChild(tutorial);
		// pause/ resume
		final int[] pauseInfo = atlasInfo.get("button_pause");
		final int[] resumeInfo = atlasInfo.get("button_resume");
		TextureRegion pauseRegion = new TextureRegion(atlas, pauseInfo[2], pauseInfo[3], pauseInfo[0], pauseInfo[1]);
		TextureRegion resumeRegion = new TextureRegion(atlas, resumeInfo[2], resumeInfo[3], resumeInfo[0], resumeInfo[1]);
		TiledTextureRegion charRegion = new TiledTextureRegion(atlas, pauseRegion, resumeRegion);
		pauseResumeBtn = new TiledSprite(10, 10, charRegion, vertexBufferObjectManager) {
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_DOWN && bird.getState() != STATE.NOT_MOVE && bird.getState() != STATE.DIE) {
					if (this.getCurrentTileIndex() == 0) { // pause
						for (Pipe pipe : activePipe) {	
							pipe.pause();
						}
						bird.pause();
						footer.unregisterEntityModifier(footerModifier);
						this.setCurrentTileIndex(1);
					} else { // resume
						bird.resume();
						for (Pipe pipe : activePipe) {	
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
		this.mPhysicsWorld.setContactListener(this);
		scene.registerUpdateHandler(this.mPhysicsWorld);

		scene.setOnSceneTouchListener(this);
	}

	@Override
	protected boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent) {
		if (bird.getState() == STATE.NOT_MOVE) {
			readyText.detachSelf();
			tutorial.detachSelf();
			
			bird.setState(STATE.DOWN, false);
			for (Pipe pipe : activePipe) {
				pipe.action();
			}
			
			scene.attachChild(pauseResumeBtn);
			scene.registerTouchArea(pauseResumeBtn);
		}
		bird.jumpUp();
		return false;
	}

	@Override
	public void beginContact(Contact contact) {
		final Body b1 = contact.getFixtureA().getBody();
        final Body b2 = contact.getFixtureB().getBody();
        final String b1Name = (String) b1.getUserData();
        final String b2Name = (String) b2.getUserData();
        if ((b1Name !=null && b1Name.equals(Bird.BIRD)) || (b2Name !=null && b2Name.equals(Bird.BIRD))) {
        	System.out.println("vec: " + bird.getVelocityY());
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
        	for (Pipe pipe : activePipe) {
				pipe.pause();
			}
        	// bird die
        	bird.setState(STATE.DIE, (b1Name == null || b2Name == null));
        	// scene color
        	handleGameOver();
        	scene.detachChild(pauseResumeBtn);
    		scene.unregisterTouchArea(pauseResumeBtn);
        }
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
