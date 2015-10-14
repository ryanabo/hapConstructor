package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.hapCResultsManager;

public class SingleMarkerAnalysis implements DynamicAnalysis {

	private SingleMarkerFormat[] analysisFormats = null;
	private AnalysisFiller[] analysisFillers = null;
	
	public SingleMarkerAnalysis( SingleMarkerFormat[] formats )
	{
		analysisFormats = formats;
	}
	
	//----------------------------------------------------------------------------
	public String getAnalysisType(){ return "singlemarker"; }
	
	//----------------------------------------------------------------------------
	public void setFillers( AnalysisFiller[] afillers ){ analysisFillers = afillers; }
	
	//----------------------------------------------------------------------------
	public void run( int simIndex, GenieDataSet genieDataSet )
	{
		for ( int i=0; i < analysisFormats.length; i++ )
		{
			for ( int j=0; j < analysisFillers.length; j++)
			{
				ConstructionAnalysis ca = new ConstructionAnalysis(analysisFillers[j],analysisFormats[i]);
				ca.run(simIndex,genieDataSet);
			}
		}
	}

	public void bufferAdd(ComboSet cset) {
		// TODO Auto-generated method stub
		
	}

	public AnalysisFiller[] getFillers() { return analysisFillers; }

	public AnalysisFormat[] getFormats() { return analysisFormats; }

	public void resetFillers() {
		// TODO Auto-generated method stub
		
	}

	public void run(int simIndex, GenieDataSet genieDataSet,
			hapCResultsManager hapconresults) {
		// TODO Auto-generated method stub
		
	}
}
