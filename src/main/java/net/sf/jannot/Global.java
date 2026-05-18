package net.sf.jannot;

import java.io.IOException;

import net.sf.jannot.exception.ReadFailedException;
import net.sf.nameservice.NameService;
import tudelft.utilities.logging.Reporter;

/**
 * Class for sharing objects that are needed everywhere.
 * <p>
 * This contains the system wide {@link Reporter}, {@link NameService},
 * {@link TypeFactory}. Also reroutes java system logger to the logger using the
 * {@link JavaLogInterceptor}.
 * <p>
 * Grouping them here allows to initialize them properly and to easily add new
 * objects as needed. Avoiding making them static utility classes enables us to
 * debug, test and mock them.
 * <p>
 * Normally only 1 instance is used everywhere.
 */
public class Global {
	private final DistributingReporter log;
	private final NameService ns;
	private final JavaLogInterceptor interceptor;
	private final TypeFactory typeFactory = new TypeFactory();

	public Global(DistributingReporter log, JavaLogInterceptor interceptor,
			NameService ns) {
		this.log = log;
		this.interceptor = interceptor;
		this.ns = ns;
	}

	public Global() throws IOException, ReadFailedException {
		log = new DistributingReporter();
		interceptor = new JavaLogInterceptor(log);
		ns = new NameService(log);
	}

	/**
	 * 
	 * @return the shared instance of {@link DistributingReporter}
	 */
	public DistributingReporter getLog() {
		return log;
	}

	/**
	 * 
	 * @return the shred instance of {@link NameService}
	 */
	public NameService getNameService() {
		return ns;
	}

	/**
	 * 
	 * @return the shared instance of {@link TypeFactory}
	 */
	public TypeFactory typeFactory() {
		return typeFactory;
	}
}
