package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.BlankColumn;
import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.gm.DataQuery;
import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable.Column;

public class CTColumnManager extends ColumnManager {

	private String model = null;
	private ComboSet comboset = null;
	private int ncols = 0;
	
	public CTColumnManager(ComboSet cset, String itype, String imodel, int ncolumns) 
	{
		super((Column[]) new BlankColumn[ncolumns],itype);
		model = imodel;
		ncols = ncolumns;
		comboset = cset;
	}
	
	//---------------------------------------------------------------------------
	public String getAnalysisType(){ return "compositetrend"; }
	
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
		ValueUnit[] vu = comboset.getValueUnits();
		GenotypeDataQuery gdq = new GenotypeDataQuery(vu.length,"sum",getType());
		MarkerUnit[] mu = comboset.getMarkerUnits();
		for ( int i=0; i < vu.length; i++){	gdq.setDataQuery(new DataQuery(vu[i],mu[i],"sum"),i); }
		return new GenotypeDataQuery[]{gdq};
	}
	
	//---------------------------------------------------------------------------
	public int[] processQueryResult( int[] queryResult, int queryIndex ){ return queryResult; }

}
