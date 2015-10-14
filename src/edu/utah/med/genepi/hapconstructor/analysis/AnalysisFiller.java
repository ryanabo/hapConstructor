package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.ModelCombo;


public class AnalysisFiller {

	private ValueCombo valueCombo = null;
	private MarkerCombo[] markerCombo = null;
	private ModelCombo[] modelCombo = null;
	
	public AnalysisFiller( ValueCombo vc, MarkerCombo[] mc, ModelCombo[] mdc )
	{
		valueCombo = vc;
		markerCombo = mc;
		modelCombo = mdc;
	}
	
	//----------------------------------------------------------------------------
	public int getNMarkerCombos(){ return markerCombo.length; }
	
	//----------------------------------------------------------------------------
	public MarkerCombo getMarkerCombo( int index ){ return markerCombo[index]; }
	
	//----------------------------------------------------------------------------
	public ValueCombo getValueCombo( ){ return valueCombo; }
	
	//----------------------------------------------------------------------------
	public ModelCombo[] getModelCombo(){ return modelCombo; }
}
