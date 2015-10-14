package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.ColumnManager;
import edu.utah.med.genepi.analysis.ModelCombo;

public class InteractionFormat implements AnalysisFormat {

	private String model = null;
	private String type = null;
	private Integer[] stats = null;
	private Integer[] metas = null;
	private int ncols = 0;
	
	public InteractionFormat( String imodel, Integer[] istatIndices, Integer[] imetaIndices, String itype, int ncolumns )
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
		if ( model.equals("IntxLD") )
		{
			cm = new ColumnManager[1];
			ComboSet cset = new ComboSet(mc,vc,modelcombos[0]);
			cm[0] = new IntxLDColumnManager(cset,type,model,ncols);
		}
		else
		{
			cm = new ColumnManager[modelcombos.length];
			for ( int i=0; i < modelcombos.length; i++ )
			{
				ComboSet comboset = new ComboSet(mc,vc,modelcombos[i]);
				cm[i] = new IntxORColumnManager(comboset,type,model,ncols);
			}
		}

		return cm;
	}

	//---------------------------------------------------------------------------	
	public Integer[] getMetaIndices() { return metas; }

	//---------------------------------------------------------------------------	
	public String getModel() { return model; }

	//---------------------------------------------------------------------------	
	public Integer[] getStatIndices() { return stats; }

	//---------------------------------------------------------------------------	
	public String getType() { return type; }

}
