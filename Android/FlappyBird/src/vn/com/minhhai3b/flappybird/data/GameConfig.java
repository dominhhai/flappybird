package vn.com.minhhai3b.flappybird.data;

import java.util.Map;

import org.andengine.opengl.texture.Texture;

public class GameConfig {

	private static GameConfig instance;
	
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
