package com.knowmed.auth;

public interface SmartLaunchPersist {
	String fetch(String key);
	void erase(String key);
	void store(String[] keys, String Json, String expiry);
	/**
	 * garbage collect
	 */
	void gc();
}
