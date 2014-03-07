package vn.com.minhhai3b.flappybird.Entity;

import java.util.Map;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import vn.com.minhhai3b.flappybird.MainGameActivity;
import vn.com.minhhai3b.flappybird.data.GameConfig;

public class Bird {
	
	public static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(15, 0, 0.5f);
	
	public static enum TYPE {
		RED,
		BLUE,
		YELLOW
	}
	
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
	private TYPE type = TYPE.BLUE;
	private STATE state = STATE.NOT_MOVE;
	
	public Bird (MainGameActivity activity, Scene scene, boolean physics,TYPE type, float x, float y) {
		this.type = type;
		this.activity = activity;
		this.scene = scene;
		String resource;
		if (this.type == TYPE.RED) {
			resource = "bird2";
		} else if (this.type == TYPE.BLUE) {
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
			this.birdBody = PhysicsFactory.createCircleBody(this.activity.getPhysicsWorld(), this.bird, BodyType.DynamicBody, Bird.FIXTURE_DEF);
			this.activity.getPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(this.bird, this.birdBody, true, true));
			this.state = STATE.NOT_MOVE;
		}
	}
	
	public void jumpUp() {
		if (this.state == STATE.DOWN || this.state == STATE.NOT_MOVE) {
			// jump up
			final Vector2 velocity = Vector2Pool.obtain(0, -4);
			this.birdBody.setLinearVelocity(velocity);
			Vector2Pool.recycle(velocity);
			
		}
	}
	
	public AnimatedSprite getBird() {
		return this.bird;
	}
	
}
