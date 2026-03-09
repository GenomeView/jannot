/**
 * %HEADER%
 */
package net.sf.jannot;

/**
 * @author Thomas Abeel
 *
 */
public interface DensityEstimate {

	/**
	 * 
	 * @param l the Location for the estimated count
	 * @return the estimated number of objects(features) in the location range
	 */
	public int getEstimateCount(Location l);

	/**
	 * 
	 * @return the maximum end point of contained data
	 */
	public int getMaximumCoordinate();
}
