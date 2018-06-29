package com.knowmed.auth;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.log4j.Logger;

import ca.uhn.fhir.rest.api.Constants;

/**
 * Stand alone smart on fhir writable launch context servlet. <br>
 * Communicates with LaunchContextResolver SmartLaunchContextResolver <br>
 * configured by web.xml
 */
public class SmartLaunchContextServlet extends HttpServlet {

	private static final long serialVersionUID = 1;
	private final static Logger logger = Logger.getLogger(SmartLaunchContextServlet.class);
	private final SmartLaunchPersist persist = new SmartLaunchPersistJPA();
	private final static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setContentType(Constants.CT_JSON);
			resp.setStatus(HttpStatus.SC_OK);
			String uri = req.getRequestURI();
			String[] pieces = uri.split("/");
			String launchContextId = pieces[pieces.length-1];
			String json = persist.fetch(launchContextId);
			if (json != null) {
				resp.getWriter().write(json);
			}
		}
		catch (Exception e) {
			logger.warn(e.getMessage(), e);
			resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String[] keys = req.getParameterValues("key");
			String value = req.getParameter("value");
			if (keys == null || keys.length == 0|| value == null) {
				throw new RuntimeException("missing key or value");
			}
			persist.store(keys, value);
			resp.setStatus(HttpStatus.SC_OK);
		}
		catch (Exception e) {
			logger.warn(e.getMessage(), e);
			resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
		executor.scheduleWithFixedDelay(new GarbageCollector(), 0, 1, TimeUnit.HOURS);
	}
	
	public final class GarbageCollector implements Runnable {
		public GarbageCollector(){
		}
		public void run() {
			persist.gc();
		}
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			resp.setContentType(Constants.CT_JSON);
			resp.setStatus(HttpStatus.SC_OK);
			String uri = req.getRequestURI();
			String[] pieces = uri.split("/");
			String id = pieces[pieces.length-1];
			persist.erase(id);
		}
		catch (Exception e) {
			logger.warn(e.getMessage(), e);
			resp.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
	}
}