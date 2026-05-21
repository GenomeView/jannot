/**
 *    This file is part of JAnnot.
 *
 *    JAnnot is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    JAnnot is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with JAnnot.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jannot.parser;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import be.abeel.io.LineIterator;
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
import net.sf.nameservice.NameService;
import support.DataManager;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestBEDParser {

	private final Global global;
	private final DistributingReporter log;

	public TestBEDParser() throws IOException, ReadFailedException {
		log = mock(DistributingReporter.class);
		global = new Global(log, new JavaLogInterceptor(log),
				new NameService(log));
	}

	@After
	public void after() {
		// check log/2 and log/3 separately
		verify(log, times(0)).log(eq(Level.WARNING), anyString());
		verify(log, times(0)).log(eq(Level.WARNING), anyString(), any());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString(), any());
	}

	@Test
	public void testParserMini() throws Exception {
		File f = DataManager.file("minibed.bed");
		DataSource ds = DataSourceFactory.create(new Locator(f, log), global);
		EntrySet es = ds.read(new EntrySet(global));
		// System.out.println(es.firstEntry());
		Assert.assertEquals("chr7", es.firstEntry().getID());
		int count = 0;
		for (Entry e : es) {
			count++;
		}
		Assert.assertEquals(1, count);
		Data d = es.firstEntry().get(global.typeFactory().get("ItemRGBDemo"));
		for (DataKey dk : es.firstEntry()) {
			System.out.println("Datakey=" + dk);
		}
		Assert.assertNotNull(d);

	}

	@Test
	public void testParserBare() throws Exception {
		File f = DataManager.file("barebed.bed");
		DataSource ds = DataSourceFactory.create(new Locator(f, log), global);
		EntrySet es = ds.read(new EntrySet(global));
		// System.out.println(es.firstEntry());
		Assert.assertEquals("chr7", es.firstEntry().getID());
		int count = 0;
		for (Entry e : es) {
			count++;
		}
		Assert.assertEquals(1, count);
		Data d = es.firstEntry().get(global.typeFactory().get("barebed.bed"));
		for (DataKey dk : es.firstEntry()) {
			System.out.println("Datakey=" + dk);
		}
		Assert.assertNotNull(d);

	}

	@Test
	public void testSave() throws Exception {
		File f = DataManager.file("barebed.bed");
		DataSource ds = DataSourceFactory.create(new Locator(f, log), global);
		EntrySet es = ds.read(new EntrySet(global));
		BEDParser output = new BEDParser("save.bed", global);
		FileOutputStream fos = new FileOutputStream("save.bed");
		for (Entry e : es) {
			output.write(fos, e);
		}
		fos.close();
		LineIterator it = new LineIterator(new File("save.bed"));
		Assert.assertEquals("track name=\"barebed.bed\"", it.next());
		LineIterator expected = new LineIterator(f);
		for (String line : it) {
			Assert.assertEquals(expected.next(), line.replaceAll("0\\.0", "0"));
		}
		it.close();
		expected.close();
	}

	@Test
	public void test_loadEntries() throws Exception {
		log.log(Level.INFO, "BEGIN test_loadEntries");
		File fileData = DataManager.file("ItemRGBDemo.txt");
		InputStream is = new FileInputStream(fileData);

		EntrySet entries = new EntrySet(global);
		BEDParser parser = new BEDParser(fileData.getName(), global);
		log.log(Level.INFO, "	> fileData: " + fileData + "( "
				+ fileData.length() + " KB)");
		// We parse the sample file
		entries = parser.parse(is, entries);
		log.log(Level.INFO, "		> Number of entries: " + entries.size());
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
		System.out.println("DD: " + data);

		long t4 = System.currentTimeMillis();
		log.log(Level.INFO,
				"		> Time consumed fetching data: " + Math.abs(t4 - t3));

		log.log(Level.INFO, "END test_loadEntries");
	}

}
