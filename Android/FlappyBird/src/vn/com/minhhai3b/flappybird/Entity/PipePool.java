package vn.com.minhhai3b.flappybird.Entity;

import java.util.ArrayList;
import java.util.HashMap;

import vn.com.minhhai3b.flappybird.MainGameActivity;

public class PipePool {

	private static PipePool instance;
	
	public static PipePool getInstance() {
		if (instance == null) {
			instance = new PipePool();
		}
		
		return instance;
	}
	
	private HashMap<String, ArrayList<Pipe>> pool;
	
	private PipePool() {
		pool = new HashMap<String, ArrayList<Pipe>>();
	}
	
	public Pipe getPipe(MainGameActivity activity, int type, float px, float top, float range) {
		ArrayList<Pipe> typePool = this.pool.get(String.valueOf(type));
		if (typePool == null || typePool.size() == 0) {
			return new Pipe(activity, type, px, top, range);
		} else {
			Pipe pipe = typePool.remove(0);
			pipe.setPosition(px, top, range);
			return pipe;
		}
	}
	
	public void releasePipe(Pipe pipe) {
		pipe.detachFromScene();
		String key = String.valueOf(pipe.getType());
		ArrayList<Pipe> typePool = this.pool.get(key);
		if (typePool == null) {
			typePool = new ArrayList<Pipe>();
		}
		typePool.add(pipe);
		this.pool.put(key, typePool);
	}
	
	public void releasePool() {
	}

}
