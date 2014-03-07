package vn.com.minhhai3b.flappybird.data;

public class GameConfig {

	private static GameConfig instance;
	
	public static GameConfig getInstance() {
		if (instance == null) {
			instance = new GameConfig();
		}
		return instance;
	}
	
	
	
	private GameConfig() {
	}

}
