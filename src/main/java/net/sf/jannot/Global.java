package net.sf.jannot;

import java.io.IOException;

import net.sf.jannot.exception.ReadFailedException;
import net.sf.nameservice.NameService;

/**
 * Class for sharing objects that are needed everywhere. Grouping them here
 * allows to initialize them properly. Avoiding making them static utility stuff
 * enables us to debug them.
 */
public class Global {
	private final DistributingReporter log;
	private final NameService ns;
	private final JavaLogInterceptor interceptor;

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

	public DistributingReporter getLog() {
		return log;
	}

	public NameService getNameService() {
		return ns;
	}
}
