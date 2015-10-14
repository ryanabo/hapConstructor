package edu.utah.med.genepi.hapconstructor.analysis;

import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.hapconstructor.hapCResultsManager;

public class HaplotypeAnalysis implements DynamicAnalysis {

	private HaplotypeFormat[] analysisFormats = null;
	private AnalysisFiller[] analysisFillers = null;
	private HaplotypeFillerBuffer fillerBuffer = null;
	
	public HaplotypeAnalysis( HaplotypeFormat[] formats )
	{
		analysisFormats = formats;
		fillerBuffer = new HaplotypeFillerBuffer();
	}
	
	//----------------------------------------------------------------------------
	public void resetFillers(){ analysisFillers = fillerBuffer.getFillers(); }
	
	//----------------------------------------------------------------------------
	public AnalysisFormat[] getFormats(){ return analysisFormats; }
	
	//----------------------------------------------------------------------------
	public AnalysisFiller[] getFillers(){ return analysisFillers; }
	
	//----------------------------------------------------------------------------
	public void run( int simIndex, GenieDataSet genieDataSet, hapCResultsManager hapcResultsManager )
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
	public String getAnalysisType(){ return "haplotype"; }
	
	//----------------------------------------------------------------------------
	public void bufferAdd( ComboSet cset ){	fillerBuffer.add(cset); }

	//----------------------------------------------------------------------------
	public void setFillers(AnalysisFiller[] aFillers){}
}
