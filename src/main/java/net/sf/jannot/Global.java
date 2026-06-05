package net.sf.jannot;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.cache.SourceCache;
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
 * <p>
 * For testing, typically the logger is mocked and afterwards tested for WARNING
 * or SEVERE, like
 * <p>
 * <code>
 * 		log = mock(DistributingReporter.class);<br>
 *		global = new Global(log, new JavaLogInterceptor(log),<br>
 *				new NameService(log));<br>
 *  </code>
 * <p>
 * and then
 * <p>
 * <code>
 * @After public void after() {<br>
 * verify(log, times(0)).log(eq(Level.WARNING), anyString());<br>
 * verify(log, times(0)).log(eq(Level.SEVERE), anyString());<br>
 * }
</code>
 */
public class Global {
	private static final File DEFAULT_CACHE_DIR = new File(
			System.getProperty("user.home"), "cache");

	private final DistributingReporter log;
	private final NameService ns;
	private final JavaLogInterceptor interceptor;
	private final TypeFactory typeFactory = new TypeFactory();
	private final DataSourceFactory sourceFactory;

	/**
	 * 
	 * @param log
	 * @param interceptor
	 * @param ns
	 * @param cache       the {@link SourceCache} that stores previously fetched
	 *                    URLs
	 */
	public Global(DistributingReporter log, JavaLogInterceptor interceptor,
			NameService ns, DataSourceFactory sourceFactory) {
		this.log = Objects.requireNonNull(log);
		this.interceptor = Objects.requireNonNull(interceptor);
		this.ns = Objects.requireNonNull(ns);
		this.sourceFactory = Objects.requireNonNull(sourceFactory);
	}

	/**
	 * Default constructor. Mostly used for testing and debugging
	 */
	public Global() throws IOException {
		log = new DistributingReporter();
		interceptor = new JavaLogInterceptor(log);
		ns = new NameService(log);
		SourceCache urlCache = new SourceCache(DEFAULT_CACHE_DIR);
		sourceFactory = new DataSourceFactory(urlCache, false);
	}

	/**
	 * 
	 * @return the {@link SourceCache}
	 */
	public DataSourceFactory getSourceFactory() {
		return sourceFactory;
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
