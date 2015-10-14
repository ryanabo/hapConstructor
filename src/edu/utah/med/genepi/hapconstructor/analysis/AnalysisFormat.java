package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.analysis.ColumnManager;

public interface AnalysisFormat {

	public ColumnManager[] getColumnManager( AnalysisFiller a, int markerUnitIndex );
	public Integer[] getStatIndices();
	public Integer[] getMetaIndices();
	public String getModel();
	public String getType();
}
