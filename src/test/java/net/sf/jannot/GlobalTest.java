package net.sf.jannot;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import tudelft.utilities.logging.Reporter;

public class GlobalTest {
	private final Global global;

	public GlobalTest() throws IOException {
		global = new Global();

	}

	@Test
	public void testLog() {
		Reporter listen = mock(Reporter.class);
		global.getLog().add(listen);

		// WARNING use the Reporter, not java Logger.
		// This is only for testing our log re-routing mechanism
		// check that the archaic logging to java system logger is intercepted
		Logger syslogger = Logger.getLogger(getClass().getSimpleName());
		syslogger.log(Level.WARNING, "test message");

		// Check that the global Reporter received that
		verify(listen, times(1)).log(eq(Level.WARNING), eq("test message"),
				eq(null));

	}
}
