package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.BlankColumn;
import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.analysis.ModelCombo;
import edu.utah.med.genepi.gm.DataQuery;
import edu.utah.med.genepi.gm.GenotypeDataQuery;
import edu.utah.med.genepi.hapconstructor.analysis.AnalysisTable.Column;

public class CGColumnManager extends ColumnManager {

	private String model = null;
	private ComboSet comboset = null;
	private String type = null;
	private int ncols = 0;
	
	public CGColumnManager( ComboSet cset, String itype, String imodel, int ncolumns )
	{
		super((Column[]) new BlankColumn[ncolumns],itype);
		model = imodel;
		type = itype;
		ncols = ncolumns;
		comboset = cset;
	}
	
	//---------------------------------------------------------------------------
	public String getAnalysisType(){ return "compositegenotype"; }
	
	//---------------------------------------------------------------------------
	public ComboSet getComboSet(){ return comboset; }
	
	//---------------------------------------------------------------------------
	public Column[] getCols()
	{
		BlankColumn[] cols = new BlankColumn[ncols];
		for ( int i=0; i < ncols; i++) cols[i] = new BlankColumn(i); 
		return cols;
	}
	
	//---------------------------------------------------------------------------
	public int[] getLoci(){ return comboset.getLoci();  }
	
	//---------------------------------------------------------------------------
	public int getRefColIndex(){ return 0; }
	
	//---------------------------------------------------------------------------
	public boolean checkDistinctRefWt(){ return true; }
	
	//---------------------------------------------------------------------------
	public GenotypeDataQuery[] getDataQuery()
	{		
		ValueUnit[] vu = comboset.getValueUnits();
		GenotypeDataQuery gdq = new GenotypeDataQuery(vu.length,"AND",getType());
		MarkerUnit[] mu = comboset.getMarkerUnits();
		ModelCombo modelcombo = comboset.getModelCombo();
		String[] models = modelcombo.getModels();
		for ( int i=0; i < models.length; i++)
		{	
			String filter = "OR";
			if ( models[i].contains("Rec") ) filter = "AND";
			gdq.setDataQuery(new DataQuery(vu[i],mu[i],filter),i); 
		}
		return new GenotypeDataQuery[]{gdq};
	}
	
	//---------------------------------------------------------------------------
	public int[] processQueryResult( int[] queryResult, int queryIndex ){ return queryResult; }
}
