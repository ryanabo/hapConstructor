package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.ModelCombo;

public class HaplotypeFormat implements AnalysisFormat {

	private String model = null;
	private String type = null;
	private Integer[] stats = null;
	private Integer[] metas = null;
	private int ncols = 0;
	
	public HaplotypeFormat( String imodel, Integer[] istatIndices, Integer[] imetaIndices, String itype, int ncolumns )
	{
		model = imodel;
		type = itype;
		stats = istatIndices;
		metas = imetaIndices;
		ncols = ncolumns;
	}
	
	//---------------------------------------------------------------------------	
	public HapColumnManager[] getColumnManager( AnalysisFiller af, int markerComboIndex )
	{
		HapColumnManager[] hapcm = new HapColumnManager[1];
		MarkerCombo mc = af.getMarkerCombo(markerComboIndex);
		ModelCombo[] modelcombo = af.getModelCombo();
		ValueCombo vc = af.getValueCombo();
		ComboSet cset = new ComboSet(mc,vc,modelcombo[0]);
		hapcm[0] = new HapColumnManager(cset,model,type,ncols);
		return hapcm;
	}
	
	//---------------------------------------------------------------------------	
	public Integer[] getStatIndices(){ return stats; }
	
	//---------------------------------------------------------------------------	
	public Integer[] getMetaIndices(){ return metas; }
	
	//---------------------------------------------------------------------------	
	public String getModel(){ return model; }
	
	//---------------------------------------------------------------------------
	public String getType(){ return type; }
}
