/**
 * %HEADER%
 */
package net.sf.jannot;

import org.junit.Test;

import junit.framework.Assert;

/**
 * 
 * @author Thomas Abeel
 *
 */
public class AAMappingTest {

	@Test
	public void testAAmapping() {
		AminoAcidMapping aa = AminoAcidMapping.STANDARDCODE;
		AminoAcidMapping ab = AminoAcidMapping.YEASTMITOCHONDRIAL;
		AminoAcidMapping ac = AminoAcidMapping.INVERTEBRATEMITOCHONDRIAL;
		Assert.assertTrue(true);
	}
}
