package net.sf.jannot;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import tudelft.utilities.logging.Reporter;

/**
 * Intercepts {@link Logger} events and re-routes them to a {@link Reporter}
 */
public class JavaLogInterceptor extends Handler {

	private final Reporter reporter;

	public JavaLogInterceptor(Reporter reporter) {
		this.reporter = reporter;
		Logger.getLogger("").addHandler(this);
	}

	@Override
	public void publish(LogRecord record) {
		reporter.log(record.getLevel(), record.getMessage(),
				record.getThrown());
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() throws SecurityException {
		Logger.getLogger("").removeHandler(this);
	}
}