package vn.com.minhhai3b.flappybird.Entity;

import java.util.Map;

import org.andengine.entity.modifier.IEntityModifier;
import org.andengine.entity.modifier.RotationModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

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

public class Bird {
	
	public static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(200, 0, 0);
	public static final String BIRD = "BIRD";
	
	public final static int[] TYPE = new int[] {0 /*RED*/, 1 /*BLUE*/, 2 /*YELLOW*/};
	
	public static enum STATE {
		JUMP,
		DOWN,
		NOT_MOVE,
		DIE
	}
	
	private MainGameActivity activity;
	private Scene scene;
	
	private AnimatedSprite bird;
	private Body birdBody;
	private int type = TYPE[0];
	private STATE state = STATE.NOT_MOVE;
	private IEntityModifier pEntityModifier;
	
	public Bird (MainGameActivity activity, Scene scene, boolean physics, int type, float x, float y) {
		this.type = type;
		this.activity = activity;
		this.scene = scene;
		String resource;
		if (this.type == 0) {
			resource = "bird2";
		} else if (this.type == 1) {
			resource = "bird1";
		} else {
			resource = "bird0";
		}
		
		Texture atlas = GameConfig.getInstance().getAtlas();
		Map<String, int[]> atlasInfo = GameConfig.getInstance().getAtlasInfo();
		
		final int[] charInfo_0 = atlasInfo.get(resource + "_0");
		final int[] charInfo_1 = atlasInfo.get(resource + "_1");
		final int[] charInfo_2 = atlasInfo.get(resource + "_2");
		TextureRegion charRegion_0 = new TextureRegion(atlas, charInfo_0[2], charInfo_0[3], charInfo_0[0], charInfo_0[1]);
		TextureRegion charRegion_1 = new TextureRegion(atlas, charInfo_1[2], charInfo_1[3], charInfo_1[0], charInfo_1[1]);
		TextureRegion charRegion_2 = new TextureRegion(atlas, charInfo_2[2], charInfo_2[3], charInfo_2[0], charInfo_2[1]);
		TiledTextureRegion charRegion = new TiledTextureRegion(atlas, charRegion_0, charRegion_1, charRegion_2);
		this.bird = new AnimatedSprite(x, y, charRegion, this.activity.getVertexBufferObjectManager());
		this.scene.attachChild(this.bird);
		if (physics) {
			bird.animate(new long[]{100, 100, 100});
			PhysicsWorld physicsWorld = this.activity.getPhysicsWorld();
			final Rectangle birdRec = new Rectangle(x + 9, y + 9, 30, 30, this.activity.getVertexBufferObjectManager());
			this.birdBody = PhysicsFactory.createCircleBody(physicsWorld, birdRec, BodyType.DynamicBody, Bird.FIXTURE_DEF);
			this.birdBody.setUserData(Bird.BIRD);
			physicsWorld.registerPhysicsConnector(new PhysicsConnector(this.bird, this.birdBody, true, false));
			this.state = STATE.NOT_MOVE;
			this.birdBody.setActive(false);
		} else {
			bird.animate(new long[]{150, 150, 150});
		}
				
	}
	
	public void jumpUp() {
		if (this.state == STATE.DOWN || this.state == STATE.NOT_MOVE) {
			// jump up
			float curVecY = this.birdBody.getLinearVelocity().y;
			curVecY = curVecY >= -3 ? -4 : curVecY - 0.75f;
			
			if (this.bird.getY() >= - curVecY) {
				Vector2 velocity = Vector2Pool.obtain(0, curVecY);
				this.birdBody.setLinearVelocity(velocity);
				Vector2Pool.recycle(velocity);
			}
			if (this.pEntityModifier != null) {
				this.bird.unregisterEntityModifier(this.pEntityModifier);
			}
			// should use DelayModifier?
			this.pEntityModifier = new SequenceEntityModifier(new RotationModifier(0.3f, this.bird.getRotation(), -35), 
																new RotationModifier(1, -35, 80));
			this.bird.registerEntityModifier(this.pEntityModifier);
		}
	}
	
	public void setState(STATE state, boolean collisionGround) {
		this.state = state;
		if (state == STATE.DIE) {
			this.bird.clearEntityModifiers();
			if (collisionGround) {
				this.pause();
			} else {			
				this.bird.registerEntityModifier(new RotationModifier(1, this.bird.getRotation(), 80));
			}
		} else if (state == STATE.DOWN) {
			this.birdBody.setActive(true);
		}
	}
	
	public void pause() {
		this.bird.stopAnimation();
		this.birdBody.setActive(false);	
	}
	
	public STATE getState() {
		return this.state;
	}
	
	public AnimatedSprite getBird() {
		return this.bird;
	}
	
}
