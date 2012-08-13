package com.github.download13.locality;

import java.util.Date;
import java.util.WeakHashMap;

public class RateLimiter<T1> {
	private WeakHashMap<T1, Long> db;
	
	public RateLimiter() {
		this.db = new WeakHashMap<T1, Long>();
	}
	
	public boolean checkLimited(T1 key, int seconds) {
		if(!db.containsKey(key)) {
			db.put(key, new Long(0));
		}
		
		long now = (new Date()).getTime();
		if(now - ((Long) db.get(key)).longValue() < (seconds * 1000)) {
			return true;
		}
		
		db.put(key, now);
		return false;
	}
}
