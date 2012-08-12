package com.github.download13.locality;

import java.util.Date;
import java.util.WeakHashMap;

public class RateLimiter<T1> {
	private WeakHashMap<T1, Long> db;
	private int msPer;
	
	public RateLimiter(int secondsPer) {
		this.db = new WeakHashMap<T1, Long>();
		this.msPer = secondsPer * 1000;
	}
	
	public boolean checkLimited(T1 key) {
		if(!db.containsKey(key)) {
			db.put(key, new Long(0));
		}
		
		long now = (new Date()).getTime();
		if(now - ((Long) db.get(key)).longValue() < msPer) {
			return true;
		}
		
		db.put(key, now);
		return false;
	}
}
