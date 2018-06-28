package com.knowmed.auth;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class LaunchContext {
	
	/**
	 * Key is access_token or launch_context id
	 */
	@Id
	@Column(length=4000)
	String key;
	
	/**
	 * Value is JSON of key value map.
	 */
	@Column(length=4000)
	String value;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="AVAILABLEDTM")
	Date available = new Date();
	
	public LaunchContext() {
		super();
	}
	public LaunchContext(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}