package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.BlankColumn;
import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.analysis.ModelCombo;
import edu.utah.med.genepi.gm.DataQuery;
import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable.Column;

public class IntxORColumnManager extends ColumnManager {
	
	private String model = null;
	private ComboSet comboset = null;
	private int ncols = 0;
	
	public IntxORColumnManager( ComboSet cset, String itype, String imodel, int ncolumns )
	{
		super((Column[]) new BlankColumn[ncolumns],itype);
		model = imodel;
		ncols = ncolumns;
		comboset = cset;
	}
	
	//---------------------------------------------------------------------------
	public int[] getLoci(){ return comboset.getLoci();  }
	
	//---------------------------------------------------------------------------
	public String getAnalysisType(){ return "interaction"; }
	
	//---------------------------------------------------------------------------
	public ComboSet getComboSet(){ return comboset; }
	
	//---------------------------------------------------------------------------
	public boolean checkDistinctRefWt(){ return true; }
	
	//---------------------------------------------------------------------------
	public Column[] getCols()
	{
		BlankColumn[] cols = new BlankColumn[ncols];
		for ( int i=0; i < ncols; i++) cols[i] = new BlankColumn(i); 
		return cols;
	}
	
	//---------------------------------------------------------------------------
	public int getRefColIndex(){ return 0; }
	
	//---------------------------------------------------------------------------
	public GenotypeDataQuery[] getDataQuery()
	{		
		ValueUnit[] vu = comboset.getValueUnits();
		GenotypeDataQuery gdq = new GenotypeDataQuery(vu.length,"append",getType());
		MarkerUnit[] mu = comboset.getMarkerUnits();
		for ( int i=0; i < vu.length; i++){	gdq.setDataQuery(new DataQuery(vu[i],mu[i],"sum"),i); }
		return new GenotypeDataQuery []{gdq};
	}
	
	//---------------------------------------------------------------------------
	public int[] processQueryResult( int[] queryResult, int queryIndex )
	{
		// return the column of tallier to increment based on dq result
		int[] riskAlleles = new int[2];
		int[] nonriskAlleles = new int[2];
		ModelCombo modelcombo = comboset.getModelCombo();
		String[] models = modelcombo.getModels();
		for ( int i=0; i < models.length; i++ )
		{ 
			int nalleles = 1;
			if ( models[i].equals("Rec") ) nalleles = 2;
			riskAlleles[i] = nalleles;
			nonriskAlleles[i] = nalleles-1;
		}
		
		int[][] columnMatchers = new int[4][2];
		columnMatchers[0] = nonriskAlleles;
		columnMatchers[1] = new int[]{riskAlleles[0],nonriskAlleles[0]};
		columnMatchers[2] = new int[]{nonriskAlleles[1],riskAlleles[1]};
		columnMatchers[3] = riskAlleles;
		
		int[] col = new int[]{};
		for ( int i=0; i < columnMatchers.length; i++ )
		{
			boolean match = true;
			for ( int j=0; j < 2; j++ )
			{
				if ( !check_match(i,j,queryResult[j],columnMatchers[i][j]) )
				{
					match = false;
					break;
				}
			}
			if ( match )
			{
				col = new int[]{i};
				break;
			}
		}
		return col;
	}
	
	//---------------------------------------------------------------------------
	private boolean check_match( int i, int j, int queryResult, int value )
	{
		boolean nomatch = true;
		if ( i == 0 )
		{
			if ( queryResult > value ) nomatch = false;
		}
		else if ( i == 3 )
		{
			if ( queryResult < value ) nomatch = false;
		}
		else if ( i == 1 )
		{
			if ( j == 0 )
			{
				if ( queryResult < value ) nomatch = false;
			}
			else if ( j == 1)
			{
				if ( queryResult > value ) nomatch = false;
			}
		}
		else if ( i == 2 )
		{
			if ( j == 0 )
			{
				if ( queryResult > value ) nomatch = false;
			}
			else if ( j == 1)
			{
				if ( queryResult < value ) nomatch = false;
			}
		}
		return nomatch;
	}
}
