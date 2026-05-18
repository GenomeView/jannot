package net.sf.jannot.syntenic;

import static org.mockito.Mockito.mock;

import java.util.Arrays;

import org.junit.Test;

import net.sf.jannot.DistributingReporter;
import net.sf.jannot.Global;
import net.sf.jannot.JavaLogInterceptor;
import net.sf.jannot.Location;
import net.sf.jannot.Strand;
import net.sf.nameservice.NameService;

public class SyntenicDataTest {

	private final static DistributingReporter log = mock(
			DistributingReporter.class);
	private final static Global global = new Global(log,
			mock(JavaLogInterceptor.class), mock(NameService.class));

	private final static String id1 = "id1";
	private final static String id2 = "id2";
	private final static String id3 = "id3";
	private final static Location loc1 = new Location(0, 10);
	private final static Location loc2 = new Location(0, 20);
	private final static Location loc3 = new Location(30, 40);

	private final static SyntenicBlock block1 = new SyntenicBlock(id1, id2,
			loc1, loc2, Strand.FORWARD, Strand.FORWARD);
	private final static SyntenicBlock block2 = new SyntenicBlock(id1, id3,
			loc2, loc3, Strand.FORWARD, Strand.REVERSE);

	@Test(expected = NullPointerException.class)
	public void smoke() {
		new SyntenicData(null, null);
	}

	@Test
	public void testConstruct() {
		SyntenicData sd = new SyntenicData(Arrays.asList(block1, block2),
				global);
	}

	@Test
	public void testRange() {
		SyntenicData sd = new SyntenicData(Arrays.asList(block1, block2),
				global);
	}

}