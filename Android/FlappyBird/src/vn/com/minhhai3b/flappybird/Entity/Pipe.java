package vn.com.minhhai3b.flappybird.Entity;

import java.util.Map;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.IModifier;

import vn.com.minhhai3b.flappybird.MainGameActivity;
import vn.com.minhhai3b.flappybird.data.GameConfig;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.MassData;

/**
 * (c) 2014 Hai Do Minh
 * 
 * @author Hai Do Minh
 */

public class Pipe implements IEntityModifierListener{
	
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
		this.sprTopBody = PhysicsFactory.createBoxBody(physicsWorld, this.sprTop, BodyType.DynamicBody, MainGameActivity.wallFixtureDef);
		this.sprBottomBody = PhysicsFactory.createBoxBody(physicsWorld, this.sprBottom, BodyType.DynamicBody, MainGameActivity.wallFixtureDef);

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
		float pipeMoveDuration = (MainGameActivity.CAMERA_WIDTH + this.sprTop.getWidth()) / GameConfig.VELOCITY;
		this.sprTop.registerEntityModifier(new MoveXModifier(pipeMoveDuration, MainGameActivity.CAMERA_WIDTH, -this.sprTop.getWidth()));
		this.sprBottom.registerEntityModifier(new MoveXModifier(pipeMoveDuration, MainGameActivity.CAMERA_WIDTH, -this.sprTop.getWidth(), this));
	}
	
	public void detachFromScene() {
		Pipe.this.sprTop.detachSelf();
		Pipe.this.sprBottom.detachSelf();
		Pipe.this.sprTop.setVisible(false);
		Pipe.this.sprBottom.setVisible(false);
		Pipe.this.sprTopBody.setActive(false);
		Pipe.this.sprBottomBody.setActive(false);
	}

	@Override
	public void onModifierStarted(IModifier<IEntity> pModifier, IEntity pItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModifierFinished(IModifier<IEntity> pModifier, IEntity pItem) {
		// detach
		PipePool.getInstance().releasePipe(Pipe.this);	
	}

}
