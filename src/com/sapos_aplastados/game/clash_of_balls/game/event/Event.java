package com.sapos_aplastados.game.clash_of_balls.game.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.sapos_aplastados.game.clash_of_balls.game.GameBase;

import android.util.Log;

/**
 * all game changes can be stored as an Event
 * these Event's will be collected at the server and then sent to the clients
 *
 */
public abstract class Event {
	private static final String TAG = "Event";
	
	public static final byte type_game_start = 'S';
	public static final byte type_game_end = 'E';
	public static final byte type_game_info = 'G';
	public static final byte type_item_removed = 'R';
	public static final byte type_item_added = 'A';
	public static final byte type_item_update = 'U'; //also used for players
	public static final byte type_impact = 'I';
	
	public final byte type;
	
	public Event(byte type) {
		this.type = type;
	}
	
	//Network conversion
	//returns new Event or null if end of stream
	public static Event read(DataInputStream s, EventPool pool) {
		try {
			if(s.available() <= 0) return null;
			
			byte b=s.readByte();
			return pool.getEventFromStream(s, b);
			
		} catch (IOException e) {
			Log.e(TAG, "Failed to read event ("+e.getMessage()+")");
		}
		
		return null;
	}
	
	public abstract void init(DataInputStream s) throws IOException;
	
	public abstract void write(DataOutputStream s) throws IOException;
	
	//apply this event to the game
	public abstract void apply(GameBase game);
	
	//TODO: add undo method ???
}