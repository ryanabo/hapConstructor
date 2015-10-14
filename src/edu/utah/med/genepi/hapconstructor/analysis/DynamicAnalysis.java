package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.hapCResultsManager;

public interface DynamicAnalysis {

	public String getAnalysisType();
	public void setFillers(AnalysisFiller[] aFillers);
	public void run( int simIndex, GenieDataSet genieDataSet, hapCResultsManager hapconresults );
	public AnalysisFormat[] getFormats();
	public AnalysisFiller[] getFillers();
	public void bufferAdd(ComboSet cset);
	public void resetFillers();
}
