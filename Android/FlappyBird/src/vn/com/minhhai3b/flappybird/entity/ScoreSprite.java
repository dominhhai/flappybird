package vn.com.minhhai3b.flappybird.entity;

import java.util.ArrayList;
import java.util.List;

import org.andengine.entity.Entity;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;

import vn.com.minhhai3b.flappybird.MainGameActivity;

public class ScoreSprite {
	
	private MainGameActivity activity;
	private Entity scene;
	private ArrayList<AnimatedSprite> scoreSpriteList;
	private ITiledTextureRegion tiledTextureRegion;
	private float width;
	private float x;
	private float y;
	private int margin;/*0: center, -1: left: 1: right*/
	
	public ScoreSprite(MainGameActivity activity, Entity scene, float x, float y, int margin,ITiledTextureRegion pTiledTextureRegion) {
		this.activity = activity;
		this.scene = scene;
		this.scoreSpriteList = new ArrayList<AnimatedSprite>();
		this.tiledTextureRegion = pTiledTextureRegion;
		this.width = pTiledTextureRegion.getWidth(0);
		this.margin = margin;
		this.x = (margin == 0) ? (x-this.width / 2) : x;
		this.y = y;
	}
	
	public void setScore(int score) {
		this.createDigitsSprites(score);
	}
	
	public void animate(int score) {
		this.createDigitsSprites(score);
		for (AnimatedSprite sprite : this.scoreSpriteList) {
			int finalIndex = sprite.getCurrentTileIndex();
			if (finalIndex > 0) {
				sprite.animate(getDurations(finalIndex + 1), 0, finalIndex, 1);
			}
		}
	}
	
	private void createDigitsSprites(int score) {
		List<Integer> digits = this.digits(score);
		int totalLength = digits.size();
		int diffEle = totalLength - this.scoreSpriteList.size();
		int index = 0;
		for (AnimatedSprite sprite : this.scoreSpriteList) {
			sprite.setCurrentTileIndex(digits.get(index));
			if (diffEle > 0) {
				sprite.setX(this.getSpritePosition(index, totalLength));
			}
			index ++;
		}
		if (diffEle > 0) {			
			for (int i = 0; i < diffEle; i ++) {
				this.addSprite(this.getSpritePosition(index, totalLength), this.y, digits.get(index));
				index ++;
			}
		}
		
	}
	
	private List<Integer> digits(int score) {
	    List<Integer> digits = new ArrayList<Integer>();
	    do {
	        digits.add(score % 10);
	        score /= 10;
	    } while(score > 0);
	    return digits; 
	}
	
	private void addSprite(float px, float py, int tiledIndex) {
		AnimatedSprite sprite = new AnimatedSprite(px, py, this.tiledTextureRegion.deepCopy(), this.activity.getVertexBufferObjectManager());
		sprite.setCurrentTileIndex(tiledIndex);
		this.scene.attachChild(sprite);
		this.scoreSpriteList.add(sprite);
	}
	
	private float getSpritePosition(int index, int length) {
		float pX = 0;
		if (this.margin == 0) { 
			pX = this.x + (length / 2 - index) * width;
		} else { 
			pX = this.x - this.margin * index * width;
			if (this.margin == 1) {
				pX -= width;
			}
		}
		return pX;
	}
	
	private long[] getDurations(int length) {
		long[] durations = new long[length];
		for (int i = 0; i < length; i ++) {
			durations[i] = 100;
		}
		return durations;
	}

}
