/*
 * Copyright (c) 2007-2010 by The Broad Institute, Inc. and the Massachusetts Institute of Technology.
 * All Rights Reserved.
 *
 * This software is licensed under the terms of the GNU Lesser General private License (LGPL), Version 2.1 which
 * is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 *
 * THE SOFTWARE IS PROVIDED "AS IS." THE BROAD AND MIT MAKE NO REPRESENTATIONS OR WARRANTIES OF
 * ANY KIND CONCERNING THE SOFTWARE, EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, NONINFRINGEMENT, OR THE ABSENCE OF LATENT
 * OR OTHER DEFECTS, WHETHER OR NOT DISCOVERABLE.  IN NO EVENT SHALL THE BROAD OR MIT, OR THEIR
 * RESPECTIVE TRUSTEES, DIRECTORS, OFFICERS, EMPLOYEES, AND AFFILIATES BE LIABLE FOR ANY DAMAGES OF
 * ANY KIND, INCLUDING, WITHOUT LIMITATION, INCIDENTAL OR CONSEQUENTIAL DAMAGES, ECONOMIC
 * DAMAGES OR INJURY TO PROPERTY AND LOST PROFITS, REGARDLESS OF WHETHER THE BROAD OR MIT SHALL
 * BE ADVISED, SHALL HAVE OTHER REASON TO KNOW, OR IN FACT SHALL KNOW OF THE POSSIBILITY OF THE
 * FOREGOING.
 */

package org.broad.tools;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.logging.Level;

import org.broad.igv.track.WindowFunction;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.samtools.SamReader;
import htsjdk.samtools.SamReaderFactory;
import tudelft.utilities.logging.Reporter;

/**
 * Class to create TDF files from BAM files.
 * 
 * @author jrobinso
 * @author Thomas Abeel
 */
public class TDFTools {

	private final Reporter log;

	public TDFTools(Reporter log) {
		this.log = log;
	}

	/**
	 * Compute coverage or density of an alignment or feature file.
	 * 
	 * @param ifile           BAM Alignment
	 * @param ofile           Output file
	 * @param windowFunctions
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void doCount(String ifile, String ofile,
			Collection<WindowFunction> windowFunctions)
			throws IOException, URISyntaxException {
		log.log(Level.INFO, "Computing coverage.  File = " + ifile);
		SamReader sfr = SamReaderFactory.makeDefault().open(new File(ifile));
		SAMSequenceDictionary dict = sfr.getFileHeader()
				.getSequenceDictionary();
		long max = 0;
		for (SAMSequenceRecord ssr : dict.getSequences()) {
			if (ssr.getSequenceLength() > max)
				max = ssr.getSequenceLength();
		}

		int zoom = 0;
		while (max / 2 > 50000) {
			max /= 2;
			zoom++;
		}
		log.log(Level.INFO, "Zoom levels needed: " + zoom);

		int maxZoomValue = zoom;

		Preprocessor p = new Preprocessor(new File(ifile).getName(),
				new File(ofile), dict, windowFunctions, 1, log);
		p.count(ifile, maxZoomValue);
		p.finish();

	}

}
