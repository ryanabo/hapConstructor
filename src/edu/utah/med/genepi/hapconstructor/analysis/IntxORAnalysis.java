package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.hapCResultsManager;

public class IntxORAnalysis implements DynamicAnalysis {
	
	private InteractionFormat[] analysisFormats = null;
	private AnalysisFiller[] analysisFillers = null;
	private CGFillerBuffer fillerBuffer = null;
	
	public IntxORAnalysis( AnalysisFormat[] formats )
	{
		analysisFormats = (InteractionFormat[]) formats;
		fillerBuffer = new CGFillerBuffer();
	}
	
	//----------------------------------------------------------------------------
	public AnalysisFormat[] getFormats(){ return analysisFormats; }
	
	//----------------------------------------------------------------------------
	public AnalysisFiller[] getFillers(){ return analysisFillers; }
	
	//----------------------------------------------------------------------------
	public void setFillers( AnalysisFiller[] fillers ){ analysisFillers = fillers; }

	//----------------------------------------------------------------------------
	public String getAnalysisType() { return "interaction"; }
	
	//----------------------------------------------------------------------------
	public void run(int simIndex, GenieDataSet genieDataSet, hapCResultsManager hapconresults) 
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
	
	//----------------------------------------------------------------------------
	public void bufferAdd( ComboSet cset )
	{
//		if ( cset.getType().equals("compositebetween") )
//		{
			fillerBuffer.add(cset);
//		}
	}

	//----------------------------------------------------------------------------
	public void resetFillers(){ analysisFillers = fillerBuffer.getFillers(); }
}
