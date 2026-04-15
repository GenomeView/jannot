/**
 * %HEADER%
 */
package net.sf.jannot.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Level;

import be.abeel.io.LineIterator;
import net.sf.jannot.StringKey;
import net.sf.jannot.parser.software.BlastM8Parser;
import net.sf.jannot.parser.software.BroadSolexa;
import net.sf.jannot.parser.software.FindPeaksParser;
import net.sf.jannot.parser.software.GeneMarkParser;
import net.sf.jannot.parser.software.MapViewParser;
import net.sf.jannot.parser.software.MaqSNPParser;
import net.sf.jannot.parser.software.MauveParser;
import net.sf.jannot.parser.software.SIPHTParser;
import net.sf.jannot.parser.software.TRNAscanParser;
import net.sf.jannot.parser.software.TransTermHPParser;
import tudelft.utilities.logging.Reporter;

/**
 * Factory that produces parser(s) that can handle an inputstream.
 * 
 */
public abstract class ParserFactory {

	/**
	 * 
	 * @param source
	 * @param log
	 * @return
	 */
	public static Parser[] parsers(Object source, Reporter log) {
		return new Parser[] { new GFF3Parser(log),
				new BEDParser(source.toString(), log), new EMBLParser(log),
				new GTFParser(log), new BlastM8Parser(log),
				new FindPeaksParser(log), new GeneMarkParser(log),
				new MaqSNPParser(log), new TransTermHPParser(log),
				new TRNAscanParser(log), new EMBLParser(log),
				new FastaParser(log), new GenbankParser(null, log),
				new PTTParser(log), new TBLParser(log),
				new VCFParser(source.toString(), log), new WiggleParser(log),
				new SyntenicParser(null, log) };
	}

	// this is singleton class
	private ParserFactory() {

	}

	/**
	 * public method for {@link #findParser(InputStream, Object)}
	 * 
	 * @param is     the inputstream of the data to parse
	 * @param source the filename or so representing the original source. Some
	 *               parsers require the object toString function to give a
	 *               valid File path. Others assume the source to be a "datakey"
	 * @param log    the {@link Reporter} where the parser will log issues to
	 * @return an concrete Parser for the input stream, as determined by the
	 *         headers actually in the input stream. Or null if no suitable
	 *         parser is found.
	 * @throws IOException
	 */
	public static Parser create(InputStream is, Object source, Reporter log)
			throws IOException {

		Parser p = findParser(is, source, log);
		log.log(Level.INFO, "Created parser: " + p);
		return p;

	}

	/**
	 * Method to find a parser suited for an input stream. This relies on
	 * detailed knowledge of header contents and rules for comments and empty
	 * lines for the parsers at our disposal. In some cases trial reads are done
	 * to see if errors occur.
	 * 
	 * @param is     the inputstream of the data to parse
	 * @param source the filename or so representing the original source. Some
	 *               parsers require the object toString function to give a
	 *               valid File path. Others assume the source to be a "datakey"
	 * 
	 * @return an concrete Parser for the input stream, as determined by the
	 *         headers actually in the input stream. Or null if no suitable
	 *         parser is found.
	 * @throws IOException
	 */
	private static Parser findParser(InputStream is, Object source,
			Reporter log) throws IOException {
		LineIterator it = new LineIterator(is);
		// it.setSkipComments(true);
		it.setSkipBlanks(true);
		String firstLine = it.next();
		String nonCommentLine = firstLine;

		// Skip comments and UCSC browser information lines
		while (nonCommentLine.startsWith("#")
				|| nonCommentLine.startsWith("browser")) {
			nonCommentLine = it.next();

		}
		if (firstLine.contains("fileformat=VCF"))
			return new VCFParser(source.toString(), log);

		if (firstLine.contains("Mauve1"))
			return new MauveParser(new StringKey(source.toString()), log);

		if (nonCommentLine.equals("id	chrom	start	end	max_coord"))
			return new FindPeaksParser(log);

		// log.log(Level.FINEST, "firstLine: " + firstLine);
		// log.log(Level.FINEST, "nonCommentLine: " + nonCommentLine);
		if (firstLine.startsWith("Guide for interpreting SIPHT output"))
			return new SIPHTParser(new StringKey(source.toString()), log);
		if (firstLine.startsWith("##maf"))
			return new MAFParser(new StringKey(source.toString()), log);
		if (nonCommentLine.startsWith("GeneMark"))
			return new GeneMarkParser(log);
		if (nonCommentLine.startsWith("TransTermHP"))
			return new TransTermHPParser(log);
		if (nonCommentLine.startsWith("gvheader:syntenic")) {
			// old style syntenic files. We don't have these anymore
			// and maybe we should remove this type.
			return new SyntenicParser(new StringKey(source.toString()), log);
		}

		// System.out.println("Detect: " + line);
		if (nonCommentLine.startsWith("track")) {
			if (nonCommentLine.startsWith("track type=wiggle_0")) {
				return new WiggleParser(log);
			} else if (nonCommentLine.startsWith("track type=bedGraph")) {
				return new BedGraphParser(new StringKey(source.toString()),
						log);
			} else {
				nonCommentLine = it.next();
			}

		}

		if (nonCommentLine.startsWith("LOCUS"))
			return new GenbankParser(null, log);

		// ====== NO HEADER. TRY TAB SPLIT . =====
		// and look in contents. This is getting fuzzy.

		String[] nonCommentArr = nonCommentLine.split("\t");

		log.log(Level.FINEST,
				"tab split nonCommentLine: " + nonCommentArr.length);

		if (nonCommentArr.length == 9) {
			if (nonCommentArr[0].contains(".."))
				return new PTTParser(log);
			else {
				boolean no1 = nonCommentArr[1].matches("[0-9]+");
				boolean no2 = nonCommentArr[2].matches("[0-9]+");

				if (no1 && no2) {
					return new BEDParser(source.toString(), log);
				} else if (nonCommentArr[8].contains("="))
					return new GFF3Parser(log);
				else
					return new GTFParser(log);

			}

		}

		if (nonCommentLine.split("[ \t]+").length == 8) {
			String[] head = new String[] { "Sequence", "tRNA", "Bounds", "tRNA",
					"Anti", "Intron", "Bounds", "Cove" };
			if (Arrays.equals(nonCommentLine.split("[ \t]+"), head)) {
				return new TRNAscanParser(log);
			}
		}

		if (nonCommentArr.length >= 12) {
			// could be PAF. Check first because next test is not checking this
			boolean isMap = true;
			// check if it's PAF. But then all extra cols are 'key:val'
			for (int col = 12; col < nonCommentArr.length; col++) {
				isMap = isMap | nonCommentArr[col].contains(":");
			}
			if (isMap && isStrand(nonCommentArr[4].charAt(0))) {
				return new SyntenicParser(new StringKey(source.toString()),
						log);
			}
		}

		/* Can either be BlastM8 or BED */
		if (nonCommentLine.split("\t").length == 12) {
			String[] arr = nonCommentLine.split("\t");

			if (isStrand(arr[4].charAt(0)))
				return new SyntenicParser(new StringKey(source.toString()),
						log);

			try {
				Double.parseDouble(arr[4]);
			} catch (NumberFormatException ne) {
				// #34 won't happen for blast, as arr[4] is a simple number
				return new BlastM8Parser(log);
			}

			if (isStrand(arr[5].charAt(0)))
				return new BEDParser(source.toString(), log);

			try {
				Double.parseDouble(arr[9]);
				Double.parseDouble(arr[11]);
			} catch (NumberFormatException ne) {
				return new MaqSNPParser(log);
			}

			return new BlastM8Parser(log);
		}

		if (nonCommentLine.split("\t").length == 16) {
			return new MapViewParser(new StringKey(source.toString()), log);
		}
		if (nonCommentLine.startsWith("ID") || nonCommentLine.startsWith("FT")
				|| nonCommentLine.startsWith("FH"))
			return new EMBLParser(log);

		if (nonCommentLine.startsWith(">")) {

			if (nonCommentLine.startsWith(">Feature "))
				return new TBLParser(log);
			else
				return specifyFastaType(nonCommentLine, source, log);

		}

		return null;
		// if (nonCommentLine.split("\t").length == 1) {
		// return new ALNParser(new StringKey(source.toString()));
		// }
		//

	}

	/**
	 * 
	 * @param c the strand character
	 * @return true iff c is '+' '-' or '.' which are the strand direction
	 *         chars.
	 */
	private static boolean isStrand(char c) {
		return (c == '+' || c == '-' || c == '.');
	}

	/**
	 * 
	 * @param line   the final string on the header line
	 * @param source the filename or id of the source
	 * @return a {@link BroadSolexa} or {@link FastaParser}
	 */
	private static Parser specifyFastaType(String line, Object source,
			Reporter log) {
		boolean broadShortRead = true;
		try {
			String[] arr = line.split(" ");
			/*
			 * The final thing on the header line should be the word 'mismatches
			 */
			if (!arr[arr.length - 1].equals("mismatches)")) {
				broadShortRead = false;
			}
			/* Try to parse the number of mismatches */
			Integer.parseInt(arr[arr.length - 2].substring(1));
			/* Strand should be either 'fw' or 'rc' */
			if (!arr[2].equals("fw") && !arr[2].equals("rc"))
				broadShortRead = false;

		} catch (NumberFormatException e) {
			broadShortRead = false;
		} catch (IndexOutOfBoundsException e) {
			broadShortRead = false;
		}
		if (broadShortRead)
			return new BroadSolexa(new StringKey(source.toString()), log);
		else
			return new FastaParser(new StringKey(source.toString()), log);
	}

}
