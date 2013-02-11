package com.sapos_aplastados.game.clash_of_balls;

import java.util.ArrayList;

import com.sapos_aplastados.game.clash_of_balls.R;

import android.content.Context;
import android.util.Log;


public class LevelManager {
	private static final String LOG_TAG = "LevelManager";
	
	private Context m_context;
	
	private ArrayList<GameLevel> m_levels;
	
	public GameLevel level(int idx) { return m_levels.get(idx); }
	public int levelCount() { return m_levels.size(); }

	public LevelManager(Context context) {
		m_context = context;
		m_levels = new ArrayList<GameLevel>();
	}
	
	public void loadLevels() {
		loadLevel(R.raw.level_1);
		loadLevel(R.raw.level_2);
		loadLevel(R.raw.level_3);
		loadLevel(R.raw.level_test1);
		loadLevel(R.raw.level_obstacles);
		loadLevel(R.raw.level_walls);
		loadLevel(R.raw.level_walls2);
		//TODO: other levels, also from file system?
		
	}
	
	private void loadLevel(int raw_res_id) {
		try {
			GameLevel l = new GameLevel(m_context);
			l.loadLevel(raw_res_id);
			m_levels.add(l);
		} catch (Exception e) {
			Log.w(LOG_TAG, "Failed to load level with raw res id="+raw_res_id
					+" ("+e.getMessage()+")");
			
		}
		
	}
}