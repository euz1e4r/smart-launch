package com.knowmed.auth;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

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
		
		// Access environment variable LYNXEMR_PROPERTY_FILE
		String LYNXEMR_PROPERTY_FILE = System.getenv("LYNXEMR_PROPERTY_FILE");
		
		if (LYNXEMR_PROPERTY_FILE == null) {
			throw new RuntimeException("LYNXEMR_PROPERTY_FILE: missing environment variable");
		}
		
		// read properties file
		ClassLoader loader = Thread.currentThread().getContextClassLoader();    
		InputStream stream = loader.getResourceAsStream(LYNXEMR_PROPERTY_FILE);
		
		if (stream == null) {
			throw new RuntimeException(LYNXEMR_PROPERTY_FILE + ": file not found");
		}
		
		Properties lynxEmrProperties = new Properties();
		lynxEmrProperties.load(stream);
		
		// Build properties to pass to JPA
		Map<String, String> persistenceProperties = new HashMap<String, String>();
		setProperty(lynxEmrProperties, "OAUTH_JDBC_URL", "javax.persistence.jdbc.url", null, persistenceProperties);
		setProperty(lynxEmrProperties, "OAUTH_USER_NAME", "javax.persistence.jdbc.user", "oauth", persistenceProperties);
		setProperty(lynxEmrProperties, "OAUTH_PASSWORD", "javax.persistence.jdbc.password", "test", persistenceProperties);
		
		System.out.println(persistenceProperties); // display properties
		
		String persistenceUnitName = "launchContext";
	    emf = Persistence.createEntityManagerFactory(persistenceUnitName, persistenceProperties);
	}
	
	private void setProperty(Properties source, String sourcePropertyName, String targetPropertyName, String defaultValue, Map<String,String> target) {
		String value = source.getProperty(sourcePropertyName);
		if (value == null) {
			if (defaultValue == null) {
				throw new RuntimeException(sourcePropertyName + ": missing system property");
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