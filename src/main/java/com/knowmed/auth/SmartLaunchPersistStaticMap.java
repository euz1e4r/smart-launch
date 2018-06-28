package com.knowmed.auth;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SmartLaunchPersistStaticMap implements SmartLaunchPersist {

	private final static Map<String, String> smartLaunchContextLocalStore = new ConcurrentHashMap<String, String>();
	
	public String fetch(String key) {
		return smartLaunchContextLocalStore.get(key);
	}

	public void store(String[] keys, String json) {
		for (String key : keys) {
			smartLaunchContextLocalStore.put(key, json);
		}
	}
}