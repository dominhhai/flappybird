package vn.com.minhhai3b.flappybird.Entity;

import java.util.Map;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.Texture;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.util.modifier.IModifier;

import vn.com.minhhai3b.flappybird.MainGameActivity;
import vn.com.minhhai3b.flappybird.data.GameConfig;

public class Pipe implements IEntityModifierListener{
	
	public final static int[] TYPE = new int[]{0, 1};
	public final static float MAX_TOP = MainGameActivity.CAMERA_HEIGHT / 4;
	public final static float MAX_RANGE = MainGameActivity.CAMERA_HEIGHT / 2;
	public final static float MAX_BOTTOM = MAX_TOP + MAX_RANGE;
	
	private Sprite sprTop = null;
	private Sprite sprBottom = null;
	
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
	}
	
	public Sprite getTopSprite() {
		return this.sprTop;
	}
	
	public Sprite getBottomSprite() {
		return this.sprBottom;
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
		scene.attachChild(this.sprTop);
		scene.attachChild(this.sprBottom);
		float pipeMoveDuration = (MainGameActivity.CAMERA_WIDTH + this.sprTop.getWidth()) / GameConfig.VELOCITY;
		this.sprTop.registerEntityModifier(new MoveXModifier(pipeMoveDuration, MainGameActivity.CAMERA_WIDTH, -this.sprTop.getWidth()));
		this.sprBottom.registerEntityModifier(new MoveXModifier(pipeMoveDuration, MainGameActivity.CAMERA_WIDTH, -this.sprTop.getWidth(), this));
	}
	
	public void detachFromScene() {
		Pipe.this.sprTop.detachSelf();
		Pipe.this.sprBottom.detachSelf();
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
