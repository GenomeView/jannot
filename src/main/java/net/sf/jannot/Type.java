/**
 * %HEADER%
 */
package net.sf.jannot;

/**
 * Class 'Type' contains list of group names which features can be classified.
 * e.g. CDS, intergenic, BED.
 * 
 * new Types are added by parsers and the list may grow during use. It is not
 * clear what this is used for.
 * 
 * @author Thomas Abeel
 * 
 */

public class Type implements DataKey {

	private final String name;

	/**
	 * 
	 * @param string a string name for this key. Private as types are created
	 *               with {@link TypeFactory#get(String)}
	 */
	protected Type(String string) {
		this.name = string;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int compareTo(DataKey o) {
		return this.toString().compareTo(o.toString());
// CHECK original order was the wrong way round?
//		return o.toString().compareTo(this.toString());
	}

}
