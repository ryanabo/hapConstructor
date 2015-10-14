package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.analysis.ModelCombo;

public class CompositeFormat implements AnalysisFormat {

	private String model = null;
	private String type = null;
	private Integer[] stats = null;
	private Integer[] metas = null;
	private int ncols = 0;
	
	public CompositeFormat(String imodel, Integer[] istatIndices, Integer[] imetaIndices, String itype, int ncolumns )
	{
		model = imodel;
		type = itype;
		stats = istatIndices;
		metas = imetaIndices;
		ncols = ncolumns;
	}

	//---------------------------------------------------------------------------	
	public ColumnManager[] getColumnManager(AnalysisFiller af, int markerComboIndex) 
	{
		MarkerCombo mc = af.getMarkerCombo(markerComboIndex);
		ValueCombo vc = af.getValueCombo();
		ModelCombo[] modelcombos = af.getModelCombo();
		
		ColumnManager[] cm = null;
		if ( model.contains("CT") )
		{
			cm = new ColumnManager[0];
			int nunits = mc.getMarkerUnits().length;
			ComboSet comboset = new ComboSet(mc,vc,modelcombos[0]);
			int ncolumns = (3*nunits)-1;
			cm[0] = new CTColumnManager(comboset,type,model,ncolumns);
		}
		else if ( model.contains("CG") )
		{
			cm = new ColumnManager[modelcombos.length];
			for ( int i=0; i < modelcombos.length; i++ )
			{
				ComboSet comboset = new ComboSet(mc,vc,modelcombos[i]);
				cm[i] = new CGColumnManager(comboset,type,model,ncols);
			}
		}
		return cm;
	}

	//---------------------------------------------------------------------------	
	public Integer[] getMetaIndices(){ return metas; }

	//---------------------------------------------------------------------------	
	public String getModel(){ return model; } 

	//---------------------------------------------------------------------------	
	public Integer[] getStatIndices(){ return stats; }

	//---------------------------------------------------------------------------	
	public String getType(){ return "composite"; }
}
