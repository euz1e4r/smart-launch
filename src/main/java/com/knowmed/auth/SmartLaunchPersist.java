package com.knowmed.auth;

public interface SmartLaunchPersist {
	String fetch(String key);
	void store(String[] keys, String Json);
}
