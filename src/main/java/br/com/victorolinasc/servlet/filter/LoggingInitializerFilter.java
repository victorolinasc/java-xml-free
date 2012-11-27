package br.com.victorolinasc.servlet.filter;

import java.util.logging.Handler;
import java.util.logging.LogManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Servlet Listener for switching the default log handler in JUL (Java utils
 * logging) to SLF4J
 * 
 * @see SLF4JBridgeHandler
 * @author victor
 */
@WebListener
public class LoggingInitializerFilter implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {

		// Jersey uses java.util.logging - bridge to slf4
		java.util.logging.Logger rootLogger = LogManager.getLogManager()
				.getLogger("");
		final Handler[] handlers = rootLogger.getHandlers();

		for (int i = 0; i < handlers.length; i++)
			rootLogger.removeHandler(handlers[i]);

		SLF4JBridgeHandler.install();
	}
}