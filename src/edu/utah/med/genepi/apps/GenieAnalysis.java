package edu.utah.med.genepi.apps;

import edu.utah.med.genepi.analysis.ExpandedAnalysis;

public interface GenieAnalysis {

	public void run(ExpandedAnalysis[] analyses);
	public void runAnalysis(ExpandedAnalysis[] analyses, int simIndex);
	public int checkProgress(int simIndex, int nSims, int progress);
	public void report(ExpandedAnalysis[] analyses);
}
