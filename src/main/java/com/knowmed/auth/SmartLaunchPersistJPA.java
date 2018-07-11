package com.knowmed.auth;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class SmartLaunchPersistJPA implements SmartLaunchPersist {

	private EntityManagerFactory emf;
	
	public SmartLaunchPersistJPA() {
		try {
			setUp();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String fetch(String key) {
		EntityManager em = emf.createEntityManager();
		try {
			LaunchContextRecord ctx = em.find(LaunchContextRecord.class, key);
			return ctx == null ? null : ctx.getValue();
		}
		finally {
			em.close();
		}
	}

	/**
	 * Store by possibly multiple keys.
	 */
	public void store(String[] keys, String value, String expiry) {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			for (String key : keys) {
				LaunchContextRecord ctx = new LaunchContextRecord(key, value, expiry);
				em.merge(ctx);
			}
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
	}
	
	protected void setUp() throws Exception {
		// parameterized properties
		// Match parameter names with oidc so we share a schema.
		
		Map<String, String> perprops = new HashMap<String, String>();
		setProperty("OAUTH_JDBC_URL", "javax.persistence.jdbc.url", null, perprops);
		setProperty("OAUTH_USER_NAME", "javax.persistence.jdbc.user", "oauth", perprops);
		setProperty("OAUTH_PASSWORD", "javax.persistence.jdbc.password", "test", perprops);
		System.out.println(perprops);
	    emf = Persistence.createEntityManagerFactory( "launchContext" , perprops);
	}
	
	private void setProperty(String systemPropertyName, String targetPropertyName, String defaultValue, Map<String,String> target) {
		String value = System.getProperty(systemPropertyName);
		if (value == null) {
			if (defaultValue == null) {
				throw new RuntimeException(systemPropertyName + ": missing system property");
			}
			value = defaultValue;
		}
		target.put(targetPropertyName, value);
	}

	public void gc() {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			em.createNamedQuery(LaunchContextRecord.QUERY_GC).executeUpdate();
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
	}

	public void erase(String key) {
		if (key == null) {
			return;
		}
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			LaunchContextRecord ctx = em.find(LaunchContextRecord.class, key);
			if (ctx != null) {
				em.remove(ctx);
			}
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
	}
}