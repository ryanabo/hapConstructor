package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.hapCResultsManager;

public class CTAnalysis implements DynamicAnalysis{

	private CompositeFormat[] analysisFormats = null;
	private AnalysisFiller[] analysisFillers = null;
	private CompositeFillerBuffer fillerBuffer = null;
	
	public CTAnalysis( AnalysisFormat[] formats )
	{
		analysisFormats = (CompositeFormat[]) formats;
		fillerBuffer = new CompositeFillerBuffer();
	}
	
	//----------------------------------------------------------------------------
	public AnalysisFormat[] getFormats(){ return analysisFormats; }
	
	//----------------------------------------------------------------------------
	public AnalysisFiller[] getFillers(){ return analysisFillers; }
	
	//----------------------------------------------------------------------------
	public String getAnalysisType(){ return "composite"; }

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
	public void setFillers( AnalysisFiller[] fillers ){ analysisFillers = fillers; }
	
	//----------------------------------------------------------------------------
	public void resetFillers() { analysisFillers = fillerBuffer.getFillers(); }
	
	//----------------------------------------------------------------------------
	public void bufferAdd( ComboSet cset )
	{
//		if ( cset.getType().contains("composite") )
//		{
			fillerBuffer.add(cset);
//		}
	}
}
