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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.DistributingReporter;
import net.sf.jannot.Global;
import net.sf.jannot.JavaLogInterceptor;
import net.sf.jannot.exception.ReadFailedException;
import net.sf.jannot.parser.software.BlastM8Parser;
import net.sf.nameservice.NameService;
import support.DataManager;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestParserDetection {
	private static final String PAF = "YJM1447_vs_R64.paf";
	private final Global global;
	private final DistributingReporter log;

	public TestParserDetection() throws ReadFailedException, IOException {
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
	public void testBED() throws Exception {
		File f = DataManager.file("minibed.bed");
		Parser p = ParserFactory.create(new FileInputStream(f), "file", global);
		assertNotNull(p);
		Assert.assertTrue("Wrong parser: " + p.getClass(),
				p instanceof BEDParser);

	}

	@Test
	public void testVCF() throws Exception {
		File f = DataManager.file("tiny.vcf");
		Parser p = ParserFactory.create(new FileInputStream(f), "file", global);
		assertNotNull(p);
		Assert.assertTrue("Wrong parser: " + p.getClass(),
				p instanceof VCFParser);

	}

	@Test
	public void testBlast() throws Exception {
		File f = DataManager.file("testblast.m8");
		Parser p = ParserFactory.create(new FileInputStream(f), "file", global);
		assertNotNull(p);
		Assert.assertTrue("Wrong parser: " + p.getClass(),
				p instanceof BlastM8Parser);

	}

	@Test
	public void testPAF() throws Exception {
		File f = DataManager.file("minibed.bed");
		Parser p = ParserFactory.create(new FileInputStream(f), "file", global);
		assertNotNull(p);
		Assert.assertTrue("Wrong parser: " + p.getClass(),
				p instanceof BEDParser);

	}

	@Test
	public void testSyntenic() throws Exception {
		File f = DataManager.file(PAF);
		Parser p = ParserFactory.create(new FileInputStream(f), "file", global);
		assertNotNull(p);
		Assert.assertTrue("Wrong parser: " + p.getClass(),
				p instanceof SyntenicParser);
	}

}
