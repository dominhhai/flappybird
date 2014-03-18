package vn.com.minhhai3b.flappybird.scene;

import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.modifier.SequenceEntityModifier;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;

import vn.com.minhhai3b.flappybird.MainGameActivity;
import vn.com.minhhai3b.flappybird.entity.Bird;
import android.view.KeyEvent;

/**
 * (c) 2014 Hai Do Minh
 * 
 * @author Hai Do Minh
 */

public class MainMenuScene extends GScene {
	
	public MainMenuScene(final MainGameActivity pActivity) {
		super(pActivity);
	}

	@Override
	protected void loadScene() {
		int[] bgInfo = this.atlasInfo.get("bg_day");
		TextureRegion backgroudRegion = new TextureRegion(this.atlas, bgInfo[2], bgInfo[3], bgInfo[0], bgInfo[1]);
		Sprite backgroud = new Sprite(0, 0, backgroudRegion, vertexBufferObjectManager);
		scene.attachChild(backgroud);
		
		int[] footerInfo = this.atlasInfo.get("land");
		TextureRegion footerRegion = new TextureRegion(this.atlas, footerInfo[2], footerInfo[3], footerInfo[0], footerInfo[1]);
		int footerY = footerInfo[1] * 3 / 4; 
		Sprite footer = new Sprite(0, CAMERA_HEIGHT - footerY, footerRegion, vertexBufferObjectManager);
		scene.attachChild(footer);
		int[] copyInfo = this.atlasInfo.get("brand_copyright");
		TextureRegion copyRegion = new TextureRegion(this.atlas, copyInfo[2], copyInfo[3], copyInfo[0], copyInfo[1]);
		Sprite copy = new Sprite((CAMERA_WIDTH - copyInfo[0]) >>> 1, CAMERA_HEIGHT - (footerY >>> 1), copyRegion, vertexBufferObjectManager);
		scene.attachChild(copy);

		final int[] titleInfo = this.atlasInfo.get("title");
		final int[] charInfo_0 = this.atlasInfo.get("bird0_0");
		TextureRegion titleRegion = new TextureRegion(this.atlas, titleInfo[2], titleInfo[3], titleInfo[0], titleInfo[1]);
		float titleY = (CAMERA_HEIGHT - titleInfo[1]) / 2;
		Sprite title = new Sprite((CAMERA_WIDTH - titleInfo[0] - 5 - charInfo_0[0]) / 2, titleY, titleRegion, vertexBufferObjectManager);						
		title.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new MoveYModifier(0.5f, titleY - 20, titleY + 5), new MoveYModifier(0.5f, titleY + 5, titleY - 20))));
		scene.attachChild(title);
		new Bird(this, false,2, ((CAMERA_WIDTH + titleInfo[0] - 5 - charInfo_0[0]) >>> 1) + 5, (CAMERA_HEIGHT - charInfo_0[1]) >>> 1);
		
		final int[] btnPlayInfo = this.atlasInfo.get("button_play");
		final int[] btnScoreInfo = this.atlasInfo.get("button_score");
		TextureRegion btnPlayRegion = new TextureRegion(this.atlas, btnPlayInfo[2], btnPlayInfo[3], btnPlayInfo[0], btnPlayInfo[1]);
		TextureRegion btnScoreRegion = new TextureRegion(this.atlas, btnScoreInfo[2], btnScoreInfo[3], btnScoreInfo[0], btnScoreInfo[1]);
		int btnY = CAMERA_HEIGHT - footerY - 20 - btnPlayInfo[1];
		ButtonSprite btnPlay = new ButtonSprite((CAMERA_WIDTH / 2 - btnPlayInfo[0]) / 2, btnY, btnPlayRegion, vertexBufferObjectManager, new ButtonSprite.OnClickListener() {
			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
				mActivity.switchScene((new PlayScene(mActivity)).getScene());
			}
		});
		ButtonSprite btnScore = new ButtonSprite(CAMERA_WIDTH * 3 / 4 - btnPlayInfo[0] / 2, btnY, btnScoreRegion, vertexBufferObjectManager, new ButtonSprite.OnClickListener() {
			
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX,
					float pTouchAreaLocalY) {
			}
		});		
		scene.attachChild(btnPlay);
		scene.attachChild(btnScore);
		scene.registerTouchArea(btnPlay);
		scene.registerTouchArea(btnScore);
	}

	@Override
	protected boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

}
