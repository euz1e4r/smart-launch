package com.knowmed.auth;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class SmartLaunchBean implements SmartLaunchPersist {

	private EntityManagerFactory emf;
	
	public SmartLaunchBean() {
		try {
			setUp();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String fetch(String key) {
		EntityManager em = emf.createEntityManager();
		try {
			LaunchContext ctx = em.find(LaunchContext.class, key);
			return ctx == null ? null : ctx.getValue();
		}
		finally {
			em.close();
		}
	}

	public void store(String[] keys, String value) {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			for (String key : keys) {
				LaunchContext ctx = new LaunchContext(key, value);
				em.merge(ctx);
			}
			em.getTransaction().commit();
		}
		finally {
			em.close();
		}
	}
	
	protected void setUp() throws Exception {
	    emf = Persistence.createEntityManagerFactory( "launchContext" );
	}
}