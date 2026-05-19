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

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.Data;
import net.sf.jannot.DataKey;
import net.sf.jannot.DistributingReporter;
import net.sf.jannot.Entry;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Feature;
import net.sf.jannot.Global;
import net.sf.jannot.MemoryFeatureAnnotation;
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
public class TestVCFParser {

	private final DistributingReporter log;
	private final Global global;

	public TestVCFParser() throws ReadFailedException, IOException {
		log = mock(DistributingReporter.class);
		global = new Global(log, null, new NameService(log));
	}

	@After
	public void after() {
		verify(log, times(0)).log(eq(Level.WARNING), anyString(), any());
		verify(log, times(0)).log(eq(Level.SEVERE), anyString(), any());
	}

	@Test
	public void testTinySize() throws Exception {

		File f = DataManager.file("tiny.vcf");
		DataSource ds = DataSourceFactory.create(new Locator(f, log), global);
		EntrySet es = ds.read(new EntrySet(global));
		// System.out.println(es.firstEntry());
		Assert.assertEquals("20", es.firstEntry().getID());
		int count = 0;
		for (Entry e : es) {
			count++;
		}
		Assert.assertEquals(1, count);
		Data d = es.firstEntry().get(global.typeFactory().get("tiny.vcf"));
		for (DataKey dk : es.firstEntry()) {
			assertNotNull(dk);

		}
		Assert.assertTrue(d instanceof MemoryFeatureAnnotation);
		MemoryFeatureAnnotation mfa = (MemoryFeatureAnnotation) d;

		for (Feature feat : mfa.get()) {
			assertNotNull(feat);
		}

		assertNotNull(d);

	}

	@Test
	public void testRegularSize() throws Exception {

		File f = DataManager.file("regular.vcf");
		DataSource ds = DataSourceFactory.create(new Locator(f, log), global);
		EntrySet es = ds.read(new EntrySet(global));
		Assert.assertEquals("gi|395136682|gb|CP003248.1|",
				es.firstEntry().getID());
		int count = 0;
		for (Entry e : es) {
			count++;
		}
		Assert.assertEquals(1, count);
		Data d = es.firstEntry().get(global.typeFactory().get("regular.vcf"));
		for (DataKey dk : es.firstEntry()) {
			assertNotNull(dk);
		}
		Assert.assertTrue(d instanceof MemoryFeatureAnnotation);
		MemoryFeatureAnnotation mfa = (MemoryFeatureAnnotation) d;

		for (Feature feat : mfa.get()) {
			assertNotNull(feat.type());
		}

		Assert.assertNotNull(d);

	}
}
