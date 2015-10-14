package edu.utah.med.genepi.gm;

public class GenotypeDataQuery {

	private DataQuery[] dataQueries = null;
	private String filterFunction = null;
	private int[] queryResult = null;
	private String type = null;
	
	public GenotypeDataQuery( int nQueries, String filter, String itype )
	{
		dataQueries = new DataQuery[nQueries];
		filterFunction = filter;
		type = itype;
	}
	
	//----------------------------------------------------------------------------
	public void setDataQuery( DataQuery dq, int index ){ dataQueries[index] = dq; }
	
	//----------------------------------------------------------------------------
	public void setMissing(){ queryResult = null; }
	
	//----------------------------------------------------------------------------
	public DataQuery[] getQueries(){ return dataQueries; }
	
	//----------------------------------------------------------------------------
	public int[] getIncColIndices(){ return queryResult; }
	
	//----------------------------------------------------------------------------
	public int[] getQueryResult(){ return queryResult; }
	
	//----------------------------------------------------------------------------
	public boolean checkFullMatch()
	{
		boolean fullMatch = false;
		if ( filterFunction.equals("OR") )
		{
			if ( type.equals("allele") && queryResult[0] == 1 && queryResult[1] == 1 ) fullMatch = true;
			else if ( type.equals("genotype") && queryResult[0] == 1 ) fullMatch = true;
		}
		else if ( filterFunction.equals("sum") && queryResult[0] == 2 ) fullMatch = true;
		return fullMatch;
	}
	
	//----------------------------------------------------------------------------
	public boolean filter( int[] matches, int index )
	{
		boolean matching = true;
		if ( index == 0 ) queryResult = matches;
		else
		{
			for ( int i=0; i < matches.length; i++ )
			{
				if ( filterFunction.equals("sum") ) queryResult[i] += matches[i]; 
				else if ( filterFunction.equals("OR") ) queryResult[i] |= matches[i]; 
				else if ( filterFunction.equals("AND") )
				{
					queryResult[i] &= matches[i];
					if ( queryResult[i] == 0 ) matching = false;
				}
				else if ( filterFunction.equals("append") )
				{
					int[] new_result = new int[queryResult.length+1];
					for ( int j=0; j < queryResult.length; j++ ) new_result[j] = queryResult[j];
					new_result[queryResult.length] = matches[0];
					queryResult = new_result;
				}
			}
		}
		return matching;
	}
}
