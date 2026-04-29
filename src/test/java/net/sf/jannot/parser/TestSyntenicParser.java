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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import net.sf.jannot.Data;
import net.sf.jannot.EntrySet;
import net.sf.jannot.Global;
import net.sf.jannot.SyntenicData;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.Locator;
import support.DataManager;
import tudelft.utilities.logging.Reporter;

public class TestSyntenicParser {
	private static final String SYN_FILE = "test.syn";
	private final Reporter log;
	private final Global global;

	public TestSyntenicParser() throws IOException, ReadFailedException {
		global = new Global();
		log = global.getLog();
	}

	@Test
	public void testParserMini() throws Exception {
		File f = DataManager.file(SYN_FILE);
		DataSource ds = DataSourceFactory.create(new Locator(f, log), global);
		EntrySet es = ds.read(new EntrySet(global));

		Data<?> data = es.getOrCreateEntry("anthracis")
				.get(SyntenicParser.SYNTENIC_KEY);
		assertTrue(data instanceof SyntenicData);
		assertEquals(6, data.get().spliterator().getExactSizeIfKnown());
		assertEquals(3, ((SyntenicData) data).getReferences().size());

		data = es.getOrCreateEntry("info1").get(SyntenicParser.SYNTENIC_KEY);
		assertTrue(data instanceof SyntenicData);
		assertEquals(4, data.get().spliterator().getExactSizeIfKnown());

		data = es.getOrCreateEntry("info2").get(SyntenicParser.SYNTENIC_KEY);
		assertTrue(data instanceof SyntenicData);
		assertEquals(2, data.get().spliterator().getExactSizeIfKnown());
	}

}
