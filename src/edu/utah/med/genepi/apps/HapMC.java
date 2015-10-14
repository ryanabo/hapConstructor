package edu.utah.med.genepi.apps;

import edu.utah.med.genepi.analysis.ExpandedAnalysis;
import edu.utah.med.genepi.genie.GenieDataSet;

public class HapMC implements GenieAnalysis{

	private GenieDataSet genieDataSet = null;
	
	public HapMC( GenieDataSet gs )
	{
		genieDataSet = gs;
		ExpandedAnalysis[] rgenAnalyses = genieDataSet.getRgenAnalyses();
		genieDataSet.phase(0);
	    run(rgenAnalyses);
	}
	
	//----------------------------------------------------------------------------
	public void run( ExpandedAnalysis[] analyses )
	{
	    System.out.println("Starting analyses...");
	    
	    // Run analyses for observed data.
	    runAnalysis(analyses,0);
	    int progress = 0;
	    int nSims = genieDataSet.getNSims();
	    for ( int i=1; i < (nSims+1); i++ )
	    {
	    	progress = checkProgress(i,(nSims+1),progress);
	    	genieDataSet.simNullData(false,false);
	    	//genieDataSet.phase(i);
	    	runAnalysis(analyses,i);
	    }
	    report(analyses);
	}
	
	//----------------------------------------------------------------------------
	public void runAnalysis( ExpandedAnalysis[] analyses, int simIndex )
	{
		for ( int i=0; i < analyses.length; i++ ) analyses[i].run(simIndex,genieDataSet); 
	}
	
	//----------------------------------------------------------------------------
	public int checkProgress( int simIndex, int nSims, int progress )
	{
		if (simIndex / (double) nSims >= (progress * 0.1) )
		{
			System.out.print(progress % 2 == 0 ? "/" : "\\");
			++progress;
		}
		return progress;
	}
	
	//----------------------------------------------------------------------------
	public void report( ExpandedAnalysis[] analyses )
	{
		for ( int i=0; i < analyses.length; i++ ) analyses[i].report(genieDataSet);
	}
}
