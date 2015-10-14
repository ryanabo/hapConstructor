package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.ModelCombo;

public class SingleMarkerFormat implements AnalysisFormat {
	
	private String model = null;
	private String type = null;
	private Integer[] stats = null;
	private Integer[] metas = null;
	private int ncols;

	public SingleMarkerFormat( String imodel, Integer[] istatIndices, Integer[] imetaIndices, String itype, int ncolumns )
	{
		model = imodel;
		type = itype;
		stats = istatIndices;
		metas = imetaIndices;
		ncols = ncolumns;
	}

	public SingleMarkerColumnManager[] getColumnManager(AnalysisFiller a, int markerUnitIndex) 
	{
		SingleMarkerColumnManager[] cm = new SingleMarkerColumnManager[1];
		MarkerCombo mc = a.getMarkerCombo(markerUnitIndex);
		ValueCombo vc = a.getValueCombo();
		ModelCombo[] modelcombo = a.getModelCombo();
		ComboSet cset = new ComboSet(mc,vc,modelcombo[0]);
		cm[0] = new SingleMarkerColumnManager(cset,model,type,ncols);
		return cm;
	}

	public Integer[] getMetaIndices() { return metas; }

	public String getModel() { return model; }

	public Integer[] getStatIndices() { return stats; }

	public String getType() { return type; }

}
