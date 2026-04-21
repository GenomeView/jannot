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

import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.EntrySet;
import net.sf.jannot.Type;
import net.sf.jannot.source.DataSource;
import net.sf.jannot.source.DataSourceFactory;
import net.sf.jannot.source.Locator;
import net.sf.nameservice.NameService;
import support.DataManager;
import tudelft.utilities.logging.ReportToLogger;
import tudelft.utilities.logging.Reporter;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestGTFParser {

	@Test
	public void testParserMini() throws Exception {

		Reporter log = new ReportToLogger(TestGTFParser.class.toString());
		NameService.init(log);

		File f = DataManager.file("doubleScore.gtf");
		DataSource ds = DataSourceFactory.create(new Locator(f, log), log);
		EntrySet es = ds.read();
		double score = es.firstEntry().getMemoryAnnotation(Type.get("gene"))
				.get(0).getScore();
		Assert.assertEquals(0, score, 0.0001);

	}

}
