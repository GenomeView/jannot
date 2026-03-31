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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.sf.jannot.parser.software.BlastM8Parser;
import support.DataManager;

/**
 * 
 * @author Thomas Abeel
 * 
 */
public class TestParserDetection {
	private static final String PAF = "YJM1447_vs_R64.paf";

	@Test
	public void testBED() throws FileNotFoundException, IOException {
		File f = DataManager.file("minibed.bed");
		Parser p = ParserFactory.detectParser(new FileInputStream(f), "file");
		assertNotNull(p);
		Assert.assertTrue("Wrong parser: " + p.getClass(),
				p instanceof BEDParser);

	}

	@Test
	public void testVCF() throws FileNotFoundException, IOException {
		File f = DataManager.file("tiny.vcf");
		Parser p = ParserFactory.detectParser(new FileInputStream(f), "file");
		assertNotNull(p);
		Assert.assertTrue("Wrong parser: " + p.getClass(),
				p instanceof VCFParser);

	}

	@Test
	public void testBlast() throws FileNotFoundException, IOException {
		File f = DataManager.file("testblast.m8");
		Parser p = ParserFactory.detectParser(new FileInputStream(f), "file");
		assertNotNull(p);
		Assert.assertTrue("Wrong parser: " + p.getClass(),
				p instanceof BlastM8Parser);

	}

	@Test
	public void testPAF() throws FileNotFoundException, IOException {
		File f = DataManager.file("minibed.bed");
		Parser p = ParserFactory.detectParser(new FileInputStream(f), "file");
		assertNotNull(p);
		Assert.assertTrue("Wrong parser: " + p.getClass(),
				p instanceof BEDParser);

	}

	@Test
	public void testSyntenic() throws FileNotFoundException, IOException {
		File f = DataManager.file(PAF);
		Parser p = ParserFactory.detectParser(new FileInputStream(f), "file");
		assertNotNull(p);
		Assert.assertTrue("Wrong parser: " + p.getClass(),
				p instanceof SyntenicParser);
	}

}
