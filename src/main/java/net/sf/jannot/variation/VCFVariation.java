package net.sf.jannot.variation;

import net.sf.jannot.tabix.TabixLine;

class VCFVariation implements Variation {

	private TabixLine line;

	public VCFVariation(TabixLine line) {
		this.line = line;
	}

	@Override
	public Allele[] alleles() {
		String ref = line.get(3);
		String alt = line.get(4);
		return new Allele[] { new Allele(ref, alt) };
	}

	@Override
	public int start() {
		return line.getBegin();
	}

}