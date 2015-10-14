package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.BlankColumn;
import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.gm.DataQuery;
import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable.Column;

public class HapColumnManager extends ColumnManager{

	private String model = null;
	private ComboSet comboset = null;
	private int ncols = 0;
	
	public HapColumnManager( ComboSet cset, String imodel, String itype, int ncolumns )
	{
		super((Column[]) new BlankColumn[ncolumns],itype);
		model = imodel;
		ncols = ncolumns;
		comboset = cset;
	}
	
	//---------------------------------------------------------------------------
	public int[] getWts()
	{
	    int[] wts = new int[ncols];
	    for (int i = 0; i < ncols; ++i) wts[i] = i;
	    return wts;
	}
	
	//---------------------------------------------------------------------------
	public String getAnalysisType(){ return "haplotype"; }
	
	//---------------------------------------------------------------------------
	public int[] getLoci(){ return comboset.getLoci();  }
	
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
		String filter = "";
		if ( model.contains("Dom") ) filter = "OR";
		else if ( model.contains("Rec") ) filter = "AND";
		else if ( model.contains("Add") ) filter = "sum";
			
		
		ValueUnit[] vu = comboset.getValueUnits();
		GenotypeDataQuery gdq = new GenotypeDataQuery(vu.length,"sum",getType());
		MarkerUnit[] mu = comboset.getMarkerUnits();
		for ( int i=0; i < vu.length; i++){	gdq.setDataQuery(new DataQuery(vu[i],mu[i],filter),i); }
		return new GenotypeDataQuery[]{gdq};
	}
	
	//---------------------------------------------------------------------------
	public int[] processQueryResult( int[] queryResult, int queryIndex ){ return queryResult; }
	
}
