package com.knowmed.auth;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.eclipse.persistence.config.CacheUsage;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

/**
 * Persistence record
 */
@Entity
@Table(name="SMARTLAUNCHCONTEXT")
@NamedQueries(
	value= {
		@NamedQuery (name=LaunchContextRecord.QUERY_FIND,
			query="select r from LaunchContextRecord r where r.key = :key",
			hints = {
				@QueryHint(name=QueryHints.CACHE_USAGE, value=CacheUsage.DoNotCheckCache),
				@QueryHint(name=QueryHints.READ_ONLY, value=HintValues.TRUE)
			}
		),
		@NamedQuery(name=LaunchContextRecord.QUERY_GC, query="delete from LaunchContextRecord where expiry < current_timestamp")
	}
)
public class LaunchContextRecord {
	
	private final static long day = 1L*24*60*60*1000;
	public final static String QUERY_FIND = "LaunchContextRecord.find";
	public final static String QUERY_GC = "LaunchContextRecord.gc";
	
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
	public LaunchContextRecord(String key, String value, String expiry) {
		super();
		this.key = key;
		this.value = value;
		this.expiry = expiry == null ? null : new Date(Long.valueOf(expiry));
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
	public Date getExpiry() {
		return expiry;
	}
	public void setExpiry(Date expiry) {
		this.expiry = expiry;
	}
}