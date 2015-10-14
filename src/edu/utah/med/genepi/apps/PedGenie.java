package edu.utah.med.genepi.apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.utah.med.genepi.analysis.Analysis;
import edu.utah.med.genepi.analysis.ExpandedAnalysis;
import edu.utah.med.genepi.genie.GenieDataSet;
import edu.utah.med.genepi.genie.GenieReporter;

public class PedGenie implements GenieAnalysis{

	private GenieDataSet genieDataSet = null;
	
	public PedGenie( GenieDataSet gs )
	{
		genieDataSet = gs;
		ExpandedAnalysis[] rgenAnalyses = genieDataSet.getRgenAnalyses();
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
	    for ( int i = 1; i < (nSims+1); i++ )
	    {
	    	progress = checkProgress(i,(nSims+1),progress);
	    	genieDataSet.simNullData(false);
	    	runAnalysis(analyses,1);
	    }
	    report(analyses);
	}
	
	//----------------------------------------------------------------------------
	public void runAnalysis( ExpandedAnalysis[] analyses, int simIndex )
	{
		for ( int i=0; i < analyses.length; i++ )
		{
			analyses[i].run(simIndex,genieDataSet);
		}	
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
		List<Analysis> allAnalyses = new ArrayList<Analysis>();
		for ( int i=0; i < analyses.length; i++ )
		{
			allAnalyses.addAll(Arrays.asList(analyses[i].getAllAnalyses()));
		}
		try 
		{
			GenieReporter gReporter = new GenieReporter(genieDataSet,allAnalyses.toArray(new Analysis[0]));
		} catch (IOException e) 
		{
			e.printStackTrace();
		}		
	}
}
