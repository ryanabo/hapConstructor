package edu.utah.med.genepi.analysis;

import edu.utah.med.genepi.genie.GenieDataSet;

public interface ExpandedAnalysis {
	
	public void run( int simIndex, GenieDataSet gs );
	public void report(GenieDataSet genieDataSet);
	public Analysis[] getAllAnalyses();
}
