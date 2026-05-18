/**
 * %HEADER%
 */
package net.sf.jannot.bigwig;

import java.util.ArrayList;
import java.util.logging.Level;

import org.broad.igv.bbfile.BBFileReader;
import org.broad.igv.bbfile.BBZoomLevelHeader;
import org.broad.igv.bbfile.BigWigIterator;
import org.broad.igv.bbfile.RPChromosomeRegion;
import org.broad.igv.bbfile.WigItem;
import org.broad.igv.bbfile.ZoomDataRecord;
import org.broad.igv.bbfile.ZoomLevelIterator;

import net.sf.jannot.Data;
import net.sf.jannot.Global;
import net.sf.jannot.pileup.Pile;
import net.sf.jannot.pileup.PileNormalization;
import net.sf.jannot.pileup.PileTools;

/**
 * @author Thomas Abeel
 * 
 */
public class BigWigData implements Data<Pile>, PileNormalization {

	private String chr;

	private int size = -1;

	private BBFileReader tr;

	private int[] zoomReductionLevels = null;

	private final Global log;

	/**
	 * @param chr
	 * @param tr
	 * @param global the {@link Global} vars
	 */
	public BigWigData(String chr, BBFileReader tr, Global global) {
		this.log = global;
		this.chr = chr;
		this.tr = tr;

		zoomReductionLevels = new int[tr.getZoomLevelCount() + 1];
		zoomReductionLevels[0] = 5;
		for (BBZoomLevelHeader header : tr.getZoomLevels()
				.getZoomLevelHeaders()) {
			global.getLog().log(Level.INFO, "$$" + header.getZoomLevel() + "\t"
					+ header.getReductionLevel());
			zoomReductionLevels[header.getZoomLevel()] = header
					.getReductionLevel();
		}
		RPChromosomeRegion b = tr.getChromosomeBounds(tr.getChromosomeID(chr),
				tr.getChromosomeID(chr));
		size = b.getEndBase();
		global.getLog().log(Level.INFO,
				chr + "  " + b.getStartBase() + "  " + b.getEndBase());

	}

	@Override
	public Global global() {
		return log;
	}

	@Override
	public String label() {
		String out = tr.getLocator().toString().replace('\\', '/');
		return out.substring(out.lastIndexOf('/') + 1);

	}

	@Override
	public Iterable<Pile> get(int start, int end) {
		int idx = 0;
		while ((end - start + 1) / 400 > zoomReductionLevels[idx]) {
			idx++;
		}
		if (idx > 0) {
			idx--;
		}

		ArrayList<Pile> out = new ArrayList<Pile>();

		if (idx > 0) {
			fillZoom(out, start, end, idx);
		} else {
			fillWig(out, start, end);
		}

		return out;

	}

	private void fillZoom(ArrayList<Pile> out, int start, int end, int zoom) {
		ZoomLevelIterator zlIter = tr.getZoomLevelIterator(zoom, chr, start,
				chr, end, false);
		while (zlIter.hasNext()) {
			ZoomDataRecord rec = zlIter.next();
			int n = rec.getBasesCovered();
			if (n > 0) {
				Pile tmp = PileTools.create(rec.getChromStart(),
						rec.getMeanVal());
				tmp.setLength(rec.getChromEnd() - rec.getChromStart());
				out.add(tmp);
			}

		}

	}

	private void fillWig(ArrayList<Pile> out, int start, int end) {
		BigWigIterator zlIter = tr.getBigWigIterator(chr, start, chr, end,
				false);
		while (zlIter.hasNext()) {
			WigItem rec = zlIter.next();
			int n = rec.getEndBase() - rec.getStartBase();// .getBasesCovered();
			if (n > 0) {
				Pile tmp = PileTools.create(rec.getStartBase(),
						rec.getWigValue());
				tmp.setLength(n);
				out.add(tmp);
			}

		}

	}

	@Override
	public Iterable<Pile> get() {
		return get(1, size);
	}

	@Override
	public boolean canSave() {
		return false;
	}

	@Override
	public boolean supportsNormalization() {
		// FIXME implementing PileNormalization? Yes. Actually supporting it?
		// No.
		return false;
	}

}
