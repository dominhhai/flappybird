package vn.com.minhhai3b.flappybird.Entity;

import java.util.Map;
import java.util.Random;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.region.TextureRegion;

import vn.com.minhhai3b.flappybird.MainGameActivity;
import vn.com.minhhai3b.flappybird.data.GameConfig;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

/**
 * (c) 2014 Hai Do Minh
 * 
 * @author Hai Do Minh
 */

public class Pipe {
	
	public static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0);
	public static final String PIPE = "PIPE";
	
	public final static int[] TYPE = new int[]{0, 1};
	public final static float MAX_TOP = MainGameActivity.CAMERA_HEIGHT / 4;
	public final static float MAX_RANGE = MainGameActivity.CAMERA_HEIGHT / 2;
	public final static float MAX_BOTTOM = MAX_TOP + MAX_RANGE;
	
	private Sprite sprTop = null;
	private Sprite sprBottom = null;
	private Body sprTopBody = null;
	private Body sprBottomBody = null;
	
	private MainGameActivity activity;
	private int type;
	
	public Pipe(MainGameActivity activity, int type, float px, float top, float range) {
		this.activity = activity;
		this.type = type;
		Texture atlas = GameConfig.getInstance().getAtlas();
		Map<String, int[]> atlasInfo = GameConfig.getInstance().getAtlasInfo();
		String resource = (type == 0) ? "pipe" : "pipe2";
		
		final int[] upInfo = atlasInfo.get(resource + "_down");
		final int[] downInfo = atlasInfo.get(resource + "_up");
		TextureRegion upRegion = new TextureRegion(atlas, upInfo[2], upInfo[3], upInfo[0], upInfo[1]);
		TextureRegion downRegion = new TextureRegion(atlas, downInfo[2], downInfo[3], downInfo[0], downInfo[1]);
		
		float ptop = top - upInfo[1];
		float pbottom = top + range;		
		this.sprTop = new Sprite(px, ptop, upRegion, activity.getVertexBufferObjectManager());
		this.sprBottom = new Sprite(px, pbottom, downRegion, activity.getVertexBufferObjectManager());
		PhysicsWorld physicsWorld = this.activity.getPhysicsWorld();
		this.sprTopBody = PhysicsFactory.createBoxBody(physicsWorld, this.sprTop, BodyType.KinematicBody, FIXTURE_DEF);
		this.sprBottomBody = PhysicsFactory.createBoxBody(physicsWorld, this.sprBottom, BodyType.KinematicBody, FIXTURE_DEF);
		this.sprTopBody.setUserData(Pipe.PIPE);
		this.sprBottomBody.setUserData(Pipe.PIPE);

		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this.sprTop, this.sprTopBody, true, false));
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(this.sprBottom, this.sprBottomBody, true, false));
	}
	
	public Sprite getTopSprite() {
		return this.sprTop;
	}
	
	public Sprite getBottomSprite() {
		return this.sprBottom;
	}
	
	public Body getTopSpriteBody() {
		return this.sprTopBody;
	}
	
	public Body getBottomSpriteBody() {
		return this.sprBottomBody;
	}
	
	public int getType() {
		return this.type;
	}
	
	public void setPosition(float px, float top, float range) {
		float ptop = top - this.sprTop.getHeight();
		float pbottom = top + range;
		this.sprTop.setPosition(px, ptop);
		this.sprBottom.setPosition(px, pbottom);
	}
	
	public void attachToScene(Scene scene) {
		this.sprTop.setVisible(true);
		this.sprBottom.setVisible(true);
		this.sprTopBody.setActive(true);
		this.sprBottomBody.setActive(true);
		scene.attachChild(this.sprTop);
		scene.attachChild(this.sprBottom);
		final float pipeVec = - GameConfig.VELOCITY / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
		Vector2 velocity = Vector2Pool.obtain(pipeVec, 0);
		this.sprBottomBody.setLinearVelocity(velocity);
		this.sprTopBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);

		this.sprBottom.registerUpdateHandler(new IUpdateHandler() {
			
			@Override
			public void reset() {
			}
			
			@Override
			public void onUpdate(float pSecondsElapsed) {
				if (Pipe.this.sprBottom.getX() < - Pipe.this.sprBottom.getWidth()) {
					float[] pos = activity.genPipePosition(Pipe.this.sprTop.getHeight());
					float x = pos[0] / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT; 
					float y1 = (pos[1] - Pipe.this.sprTop.getHeight()) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
					float y2 = (pos[1] + pos[2]) / PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT;
					Pipe.this.sprTopBody.setTransform(x, y1, Pipe.this.sprTopBody.getAngle());
					Pipe.this.sprBottomBody.setTransform(x, y2, Pipe.this.sprBottomBody.getAngle());
					Vector2 velocity = Vector2Pool.obtain(pipeVec, 0);
					Pipe.this.sprBottomBody.setLinearVelocity(velocity);
					Pipe.this.sprTopBody.setLinearVelocity(velocity);
					Vector2Pool.recycle(velocity);
				}
			}
		});
	}
	
	public void detachFromScene() {
		this.sprTop.detachSelf();
		this.sprBottom.detachSelf();
		this.sprTop.setVisible(false);
		this.sprBottom.setVisible(false);
		this.pause();
	}
	
	public void pause() {
//		this.sprTopBody.setActive(false);
//		this.sprBottomBody.setActive(false);
		Vector2 velocity = Vector2Pool.obtain(0, 0);
		Pipe.this.sprBottomBody.setLinearVelocity(velocity);
		Pipe.this.sprTopBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
	}
}
