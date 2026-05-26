package net.sf.jannot.vcf;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import net.sf.jannot.Data;
import net.sf.jannot.DataKey;
import net.sf.jannot.DistributingReporter;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.JavaLogInterceptor;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.Locator;
import net.sf.jannot.source.cache.SourceCache;
import net.sf.jannot.variation.Variation;
import net.sf.nameservice.NameService;
import support.DataManager;
import tudelft.utilities.logging.Reporter;

/**
 * IMPORTANT!!!: This test requires the data in test/resource to be prepared.
 * 
 * See the readme.txt file in test/resource/ to see download instructions.
 * 
 * 
 * @author Thomas Abeel
 * @author David Roldan Martinez
 * 
 */
public class TestVCF {
	private final Global global;

	public TestVCF() throws IOException, ReadFailedException {

		DistributingReporter log = mock(DistributingReporter.class);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log),
				new DataSourceFactory(mock(SourceCache.class), true));
	}

	@Test
	public void test_loadEntries() throws Exception {
		Reporter log = global.getLog();

		int i = 0;

		String dataIdentifier = "CEU.trio.2010_03.genotypes.vcf.gz";
		String indexIdentifier = dataIdentifier + ".tbi";

		EntrySet entries = new EntrySet(global);
		Locator fIndex = new Locator(DataManager.file(indexIdentifier), log);
		Locator fData = new Locator(DataManager.file(dataIdentifier), log);
		DataSource ds = global.getSourceFactory().create(fData, fIndex, global);

		// File fileData = new File(dataFile);
		// File indexData = new File(indexFile);

		System.out.println("------ Sample # " + i + " ---------");
		System.out.println("	> fileData: " + fData.file() + "( "
				+ fData.file().length() + " KB)");
		System.out.println("	> indexData: " + fIndex.file() + "( "
				+ fIndex.file().length() + " KB)");
		i++;
		System.out.println("	> Reading source:" + ds);
		long t1 = System.currentTimeMillis();
		ds.read(entries);
		long t2 = System.currentTimeMillis();
		System.out.println(
				"		> Time consumed loading files: " + Math.abs(t2 - t1));
		System.out.println("		> Number of entries: " + entries.size());
		// assertTrue(entries.size() > 0);
		// We build an ArrayList to access randonmly to a entry
		List<Entry> list = new ArrayList<Entry>();
		for (Entry entry : entries) {
			list.add(entry);
		}
		int selectedIndex = Math.max(0,
				(int) Math.round(Math.random() * list.size()) - 1);
		Entry selectedEntry = list.get(selectedIndex);

		Iterator<DataKey> it = selectedEntry.iterator();
		List<DataKey> list2 = new ArrayList<DataKey>();
		while (it.hasNext()) {
			list2.add(it.next());
		}
		int keyIndex = Math.max(0,
				(int) Math.round(Math.random() * list2.size()) - 1);

		/* retrieve some data from some entry */
		long t3 = System.currentTimeMillis();
		DataKey dataKey = list2.get(keyIndex);
		Data<?> data = selectedEntry.get(dataKey);
		// System.out.println("DD: " + data);
		Iterable<?> resultSet = data.get(500000, 2000000);
		long t4 = System.currentTimeMillis();
		System.out.println(
				"		> Time consumed fetching data: " + Math.abs(t4 - t3));
		for (Object o : resultSet) {
			// System.out.println(o);
			Variation v = (Variation) o;
			// System.out.println(o+"\t"+v.start()+"\t"+v.alleles());
		}
		// }
		// }
	}

}