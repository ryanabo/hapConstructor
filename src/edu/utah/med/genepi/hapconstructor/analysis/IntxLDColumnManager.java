package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.BlankColumn;
import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.gm.DataQuery;
import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable.Column;

public class IntxLDColumnManager extends ColumnManager {
	
	private String model = null;
	private ComboSet comboset = null;
	private int ncols = 0;
	
	public IntxLDColumnManager( ComboSet cset, String itype, String imodel, int ncolumns )
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
		GenotypeDataQuery[] queries = new GenotypeDataQuery[vu.length];
		MarkerUnit[] mu = comboset.getMarkerUnits();
		for ( int i=0; i < vu.length; i++)
		{	
			GenotypeDataQuery gdq = new GenotypeDataQuery(1,"",getType());
			gdq.setDataQuery(new DataQuery(vu[i],mu[i],"sum"),0);
			queries[i] = gdq;
		}
		return queries;
	}
	
	//---------------------------------------------------------------------------
	public int[] processQueryResult( GenotypeDataQuery dq )
	{
		int[] col = null;
		return col;
	}
}
