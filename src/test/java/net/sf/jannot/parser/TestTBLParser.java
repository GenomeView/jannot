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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.EntrySet;
import net.sf.jannot.Type;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.Locator;
import support.DataManager;
import tudelft.utilities.logging.ReportToLogger;
import tudelft.utilities.logging.Reporter;

public class TestTBLParser {
	private static Reporter log = new ReportToLogger(
			TestTBLParser.class.toString());

	@Test
	public void testParserMini()
			throws URISyntaxException, IOException, ReadFailedException {
		File f = DataManager.file("sequin.tbl");
		// following is copy of another test.
		// It was expected to fail but apparently works. No idea what it does...
		DataSource ds = DataSourceFactory.create(new Locator(f, log), log);
		EntrySet es = ds.read();
		double score = es.firstEntry().getMemoryAnnotation(Type.get("gene"))
				.get(0).getScore();
		Assert.assertEquals(0, score, 0.0001);

	}

}
