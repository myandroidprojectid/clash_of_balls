/*
 * Copyright (C) 2012-2013 Hans Hardmeier <hanshardmeier@gmail.com>
 * Copyright (C) 2012-2013 Andrin Jenal
 * Copyright (C) 2012-2013 Beat Küng <beat-kueng@gmx.net>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 */

package com.sapos_aplastados.game.clash_of_balls.game.event;

import java.io.DataInputStream;
import java.io.IOException;

import android.util.Log;

import com.sapos_aplastados.game.clash_of_balls.game.DynamicGameObject;
import com.sapos_aplastados.game.clash_of_balls.game.GameBase;
import com.sapos_aplastados.game.clash_of_balls.game.GameStatistics;
import com.sapos_aplastados.game.clash_of_balls.game.StaticGameObject;
import com.sapos_aplastados.game.clash_of_balls.game.Vector;

public class EventPool {
	
	private final int m_obj_count;
	
	private static class EventArray {
		public EventArray(int array_len) {
			events = new Event[array_len];
		}
		public Event[] events;
		public int count=0;
	}
	
	private EventArray[] m_events=new EventArray[256];
	

	public EventPool(int obj_count) {
		m_obj_count = obj_count;
	}
	
	public Event getEventFromStream(DataInputStream s, byte type) throws IOException {
		Event e = getEvent(type);
		e.init(s);
		return e;
	}
	
	
	public EventGameEnd getEventGameEnd(GameStatistics statistics) {
		EventGameEnd e = (EventGameEnd)getEvent(Event.type_game_end);
		e.init(statistics);
		return e;
	}
	public EventGameInfo getEventGameInfo(GameBase game) {
		EventGameInfo e = (EventGameInfo)getEvent(Event.type_game_info);
		e.init(game);
		return e;
	}
	public EventGameStartNow getEventGameStartNow() {
		EventGameStartNow e = (EventGameStartNow)getEvent(Event.type_game_start);
		return e;
	}
	public EventImpact getEventImpact(short id_a, short id_b, Vector normal) {
		EventImpact e = (EventImpact)getEvent(Event.type_impact);
		e.init(id_a, id_b, normal);
		return e;
	}
	public EventItemAdded getEventItemAdded(GameBase game, StaticGameObject obj) {
		EventItemAdded e = (EventItemAdded)getEvent(Event.type_item_added);
		e.init(game, obj);
		return e;
	}
	public EventItemRemoved getEventItemRemoved(short item_id) {
		EventItemRemoved e = (EventItemRemoved)getEvent(Event.type_item_removed);
		e.init(item_id);
		return e;
	}
	public EventItemUpdate getEventItemUpdate(DynamicGameObject obj) {
		EventItemUpdate e = (EventItemUpdate)getEvent(Event.type_item_update);
		e.init(obj);
		return e;
	}
	
	
	public void recycle(Event e) {
		if(m_events[e.type].count < m_obj_count)
			m_events[e.type].events[m_events[e.type].count++] = e;
	}
	
	private Event getEvent(int type) {
		if(m_events[type]==null) m_events[type]=new EventArray(m_obj_count);
		if(m_events[type].count > 0) 
			return m_events[type].events[--m_events[type].count];
		
		switch(type) {
		case Event.type_game_start: return new EventGameStartNow();
		case Event.type_game_end: return new EventGameEnd();
		case Event.type_game_info: return new EventGameInfo();
		case Event.type_item_removed: return new EventItemRemoved();
		case Event.type_item_added: return new EventItemAdded();
		case Event.type_item_update: return new EventItemUpdate();
		case Event.type_impact: return new EventImpact();
		default: throw new RuntimeException("unknown Event type");
		}
	}
}
