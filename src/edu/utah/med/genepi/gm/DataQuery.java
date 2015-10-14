package edu.utah.med.genepi.gm;

import edu.utah.med.genepi.hapconstructor.analysis.MarkerUnit;
import edu.utah.med.genepi.hapconstructor.analysis.ValueUnit;

public class DataQuery {

	private ValueUnit vu = null;
	private MarkerUnit mu = null;
	// OR, AND, sum, "" (Allele based don't collapse diplotype / genotype)
	private String filterFunction = null;
	
	public DataQuery( ValueUnit ivu, MarkerUnit imu, String ifilter )
	{
		vu = ivu;
		mu = imu;
		filterFunction = ifilter;
	}
	
	//----------------------------------------------------------------------------
	public ValueUnit getValueUnit(){ return vu; }
	
	//----------------------------------------------------------------------------
	public MarkerUnit getMarkerUnit(){ return mu; }
	
	//----------------------------------------------------------------------------
	public int[] filter( int[] matches )
	{
		int[] filtered = matches;
		if ( filterFunction.equals("sum") ) filtered = new int[]{matches[0] + matches[1]}; 
		else if ( filterFunction.equals("OR") ) filtered = new int[]{matches[0] | matches[1]}; 
		else if ( filterFunction.equals("AND") ) filtered = new int[]{matches[0] & matches[1]};
		return filtered;		
	}
	
}
