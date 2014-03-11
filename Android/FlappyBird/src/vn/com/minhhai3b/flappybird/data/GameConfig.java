package vn.com.minhhai3b.flappybird.data;

import java.util.Map;

import org.andengine.opengl.texture.Texture;


/**
 * (c) 2014 Hai Do Minh
 * 
 * @author Hai Do Minh
 */

public class GameConfig {
	
	private static GameConfig instance;
	
	public static final float VELOCITY = (float) 80.0;
	
	public static GameConfig getInstance() {
		if (instance == null) {
			instance = new GameConfig();
		}
		return instance;
	}
	
	private Map<String, int[]> atlasInfo = null;
	private Texture atlas = null;	
	
	private GameConfig() {
	}
	
	public void setAtlas(Texture atlas, Map<String, int[]> atlatInfo) {
		this.atlas = atlas;
		this.atlasInfo = atlatInfo;
	}
	
	public Texture getAtlas() {
		return this.atlas;
	}
	
	public Map<String, int[]> getAtlasInfo() {
		return this.atlasInfo;
	}

}
