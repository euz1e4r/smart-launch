package com.knowmed.auth;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Persistence recond
 */
@Entity
@Table(name="LAUNCHCONTEXT")
public class LaunchContextRecord {
	
	private final static long day = 1L*24*60*60*1000;
	
	/**
	 * Key is access_token or launch_context id
	 */
	@Id
	@Column(length=4000)
	String key;
	
	/**
	 * Value is JSON of key value map.
	 * In JSON
	 */
	@Column(length=4000)
	String value;
	
	@Temporal(TemporalType.TIMESTAMP)
	Date expiry = new Date(System.currentTimeMillis() + day);
	
	public LaunchContextRecord() {
		super();
	}
	public LaunchContextRecord(String key, String value) {
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