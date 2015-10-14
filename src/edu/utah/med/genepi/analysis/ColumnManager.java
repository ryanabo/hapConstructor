package edu.utah.med.genepi.analysis;

import edu.utah.med.genepi.gm.DataQuery;
import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.gm.GtypeMatcher;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable.Column;
import edu.utah.med.genepi.hapconstructor.analysis.ComboSet;
import edu.utah.med.genepi.hapconstructor.analysis.MarkerUnit;
import edu.utah.med.genepi.hapconstructor.analysis.ValueUnit;
import edu.utah.med.genepi.util.Ut;

public class ColumnManager {

	private Column[] cols = null;
	private String type = null;
	
	public ColumnManager( Column[] columns, String itype )
	{
		cols = columns;
		type = itype;
	}
	
	//---------------------------------------------------------------------------
	public String getAnalysisType(){ return "singlemarker"; }
	
	//---------------------------------------------------------------------------
	public int[] getLoci(){ return cols[0].getGtypeMatcher()[0].getLoci();  }
	
	//---------------------------------------------------------------------------
	public int[] getWts()
	{
	    int[] wts = new int[cols.length];
	    for (int i = 0; i < cols.length; ++i) wts[i] = cols[i].getWeight();
	    return wts;
	}
	
	//---------------------------------------------------------------------------
	public Column[] getCols(){ return cols; }
	
	//---------------------------------------------------------------------------
	public int getRefColIndex(){ return Ut.indexOfMin(getWts()); }
	
	//---------------------------------------------------------------------------
	public boolean checkDistinctRefWt()
	{
		int flag = 0;
		int[] wts = getWts();
		for ( int i = 0; i < wts.length; i++)
		{
			if ( wts[i] == wts[Ut.indexOfMin(getWts())] ) flag++;
			if ( flag > 1 ) return false;
		}
		return true;
	}
	
	//---------------------------------------------------------------------------	
	public int getNCols(){ return cols.length; }
	
	//---------------------------------------------------------------------------	
	public void updateRepeat( int repeatIndex, int[][] repeat )
	{
		int nRepeat = repeat.length;
		
		if ( nRepeat > 1 ) 
		{
			for ( int c = 0; c < cols.length; c++ ) 
			{
				GtypeMatcher[] gtm = cols[c].getGtypeMatcher();
				for ( int g = 0; g < gtm.length; g++ ) gtm[g].updateSelectedLocus(repeat[repeatIndex]);
			}
		}	
	}

	//---------------------------------------------------------------------------	
	public GenotypeDataQuery[] getDataQuery()
	{ 
		// Transform column definitions into dataQuery objects.
		GenotypeDataQuery[] dqs = new GenotypeDataQuery[cols.length];
		for ( int i=0; i < cols.length; i++ )
		{
			Column col = cols[i];
			GtypeMatcher[] gtMatchers = col.getGtypeMatcher();
			dqs[i] = new GenotypeDataQuery(gtMatchers.length,"OR",type);
			if ( type.equals("allele") ) dqs[i] = new GenotypeDataQuery(gtMatchers.length,"sum",type);
			for ( int j=0; j < gtMatchers.length; j++ )
			{
				MarkerUnit mu = new MarkerUnit(gtMatchers[j].iLoci);
				ValueUnit vu = new ValueUnit(gtMatchers[j].getPatterns(),"rgen");
				DataQuery dq = new DataQuery(vu,mu,"AND");
				if ( type.equals("allele") ) dq = new DataQuery(vu,mu,"");
				dqs[i].setDataQuery(dq,j);
			}
		}
		return dqs;
	}

	//---------------------------------------------------------------------------	
	public int[] processQueryResult( int[] queryResult, int queryIndex )
	{ 
		int nhits = 0;
		for ( int i=0; i < queryResult.length; i++ )
		{
			if ( queryResult[i] != 0 ) nhits++;
		}
		int[] colIndices = new int[nhits]; 
		for ( int i=0; i < colIndices.length; i++ ) colIndices[i] = queryIndex;
		return colIndices;	
	}
	
	//---------------------------------------------------------------------------	
	public String getType(){ return type; }

	//---------------------------------------------------------------------------	
	public ComboSet getComboSet() { return null; }

}
