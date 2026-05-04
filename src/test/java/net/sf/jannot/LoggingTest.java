package net.sf.jannot;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.Test;

public class LoggingTest {
	/**
	 * This tests that we can indeed listen to root logger with ""
	 */
	@Test
	public void testIntercept() {

		TestHandler handler = new TestHandler();
		// add handler to ROOT logger, intercepting all events
		Logger.getLogger("").addHandler(handler);

		// report an issue
		Logger logger = Logger.getLogger(getClass().getSimpleName());
		logger.log(Level.INFO, "test issue");
		List<LogRecord> records = handler.getRecords();
		assertEquals(1, records.size());
		assertEquals(Level.INFO, records.get(0).getLevel());
	}

}

class TestHandler extends Handler {

	protected List<LogRecord> records = new ArrayList<>();

	@Override
	public void publish(LogRecord record) {
		records.add(record);
	}

	public List<LogRecord> getRecords() {
		return records;
	}

	@Override
	public void flush() {

	}

	@Override
	public void close() throws SecurityException {

	}

}